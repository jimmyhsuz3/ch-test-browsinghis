package ch.tool.browsinghis.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ch.tool.browsinghis.model.Sequence;
import ch.tool.browsinghis.model.Url;
import ch.tool.browsinghis.model.UrlVisit;
import ch.tool.browsinghis.model.Visit;
public class HistoryDao {
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String SELECT_URLS_ALL = "select id,url,title,visit_count,typed_count," +
			"datetime(last_visit_time / 1000000 + (strftime('%s', '1601-01-01 08:00:00')), 'unixepoch') last_visit_time,hidden from urls";
	private static final String SELECT_VISITS_ALL = "select id,url," +
			"datetime(visit_time / 1000000 + (strftime('%s', '1601-01-01 08:00:00')), 'unixepoch') visit_time,from_visit," +
			"transition,segment_id,visit_duration from visits";
	private static final String SELECT_URL_VISIT_ALL = "select url id,count(*) visit_count," +
			"max(datetime(visit_time / 1000000 + (strftime('%s', '1601-01-01 08:00:00')), 'unixepoch')) last_visit_time " +
			"from visits where visit_duration >= 0 group by url";
	private static final String SELECT_SEQUENCE_ALL = "select " +
			"v1.id visitId, v1.from_visit fromVisitId, v1.url urlId, v2.url fromUrlId, u1.url url, u2.url fromUrl, " +
			"datetime(v1.visit_time / 1000000 + (strftime('%s', '1601-01-01 08:00:00')), 'unixepoch') urlDate, " +
			"datetime(v2.visit_time / 1000000 + (strftime('%s', '1601-01-01 08:00:00')), 'unixepoch') fromUrlDate, " +
			"u1.title title, u2.title fromTitle " +
			"from visits v1 " +
			"join urls u1 on u1.id=v1.url " +
			"left join visits v2 on v2.id=v1.from_visit " +
			"left join urls u2 on u2.id=v2.url " +
			"order by v1.visit_time";
	private final List<Url> urls = new ArrayList<Url>();;
	private final List<Visit> visits = new ArrayList<Visit>();;
	private final Map<Long, UrlVisit> urlVisitMap = new HashMap<Long, UrlVisit>();
	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	public HistoryDao(String filePath){
		query(String.format("jdbc:sqlite:%s", filePath));
	}
	private void query(final String dbUrl){
		Connection conn = null;
        try {
			conn = DriverManager.getConnection(dbUrl);
			ResultSet rs = null;
			try {
				rs = conn.createStatement().executeQuery(SELECT_URLS_ALL);
				while (rs.next()){
					Url url = new Url();
					url.setId(rs.getLong("id"));
					url.setUrl(rs.getString("url"));
					url.setTitle(rs.getString("title"));
					url.setVisitCount(rs.getInt("visit_count"));
					url.setTypedCount(rs.getInt("typed_count"));
					String value = rs.getString("last_visit_time");
					try {
						url.setLastVisitTime(new SimpleDateFormat(DATE_FORMAT).parse(value));
					} catch (ParseException e) {
						throw new RuntimeException(e);
					}
					url.setHidden(rs.getInt("hidden"));
					urls.add(url);
				}
			} finally {
				if (rs != null)
					rs.close();
			}
			try {
				rs = conn.createStatement().executeQuery(SELECT_VISITS_ALL);
				while (rs.next()){
					Visit visit = new Visit();
					visit.setId(rs.getLong("id"));
					visit.setUrl(rs.getLong("url"));
					String value = rs.getString("visit_time");
					try {
						visit.setVisitTime(new SimpleDateFormat(DATE_FORMAT).parse(value));
					} catch (ParseException e) {
						throw new RuntimeException(e);
					}
					visit.setFromVisit(rs.getLong("from_visit"));
					visit.setTransition(rs.getInt("transition"));
					visit.setSegmentId(rs.getInt("segment_id"));
					visit.setVisitDuration(rs.getInt("visit_duration"));
					visits.add(visit);
				}
			} finally {
				if (rs != null)
					rs.close();
			}
			try {
				rs = conn.createStatement().executeQuery(SELECT_URL_VISIT_ALL);
				while (rs.next()){
					UrlVisit urlVisit = new UrlVisit();
					urlVisit.setId(rs.getLong("id"));
					urlVisit.setVisitCount(rs.getInt("visit_count"));
					String value = rs.getString("last_visit_time");
					try {
						urlVisit.setLastVisitTime(new SimpleDateFormat(DATE_FORMAT).parse(value));
					} catch (ParseException e) {
						throw new RuntimeException(e);
					}
					if (urlVisitMap.put(urlVisit.getId(), urlVisit) != null)
						throw new RuntimeException("urlVisitMap");
				}
			} finally {
				if (rs != null)
					rs.close();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
		}
	}
	public List<Url> getUrls() {
		return urls;
	}
	public List<Visit> getVisits() {
		return visits;
	}
	public Map<Long, UrlVisit> getUrlVisitMap() {
		return urlVisitMap;
	}
	public static List<Sequence> querySequence(String filePath, String url, int before, int after){
		List<Sequence> seqs = new ArrayList<Sequence>();
		Connection conn = null;
        try {
			conn = DriverManager.getConnection(String.format("jdbc:sqlite:%s", filePath));
			ResultSet rs = null;
			try {
				rs = conn.createStatement().executeQuery(SELECT_SEQUENCE_ALL);
				List<Sequence> temp = new java.util.LinkedList<Sequence>();
				int len = 0;
				while (rs.next()){
					Sequence seq = new Sequence();
					seq.setVisitId(rs.getLong("visitId"));
					seq.setFromVisitId(rs.getLong("fromVisitId"));
					seq.setUrlId(rs.getLong("urlId"));
					seq.setFromUrlId(rs.getLong("fromUrlId"));
					seq.setUrl(rs.getString("url"));
					seq.setFromUrl(rs.getString("fromUrl"));
					String urlDate = rs.getString("urlDate");
					try {
						seq.setUrlDate(new SimpleDateFormat(DATE_FORMAT).parse(urlDate));
					} catch (ParseException e) {
						throw new RuntimeException(e);
					}
					String fromUrlDate = rs.getString("fromUrlDate");
					if (fromUrlDate != null)
						try {
							seq.setFromUrlDate(new SimpleDateFormat(DATE_FORMAT).parse(fromUrlDate));
						} catch (ParseException e) {
							throw new RuntimeException(e);
						}
					seq.setTitle(rs.getString("title"));
					seq.setFromTitle(rs.getString("fromTitle"));
					if (len == 0 && temp.size() > before)
						temp.remove(0);
					temp.add(seq);
					if (seq.getUrl().equals(url) || seq.getTitle().indexOf(url) > -1)
						len = temp.size() + after;
					if (len == temp.size()){
						seqs.addAll(temp);
						seqs.add(null);
						temp.clear();
						len = 0;
					}
				}
				if (len > 0 && temp.size() > 0)
					seqs.addAll(temp);
			} finally {
				if (rs != null)
					rs.close();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
		}
        return seqs;
	}
}