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
import ch.tool.browsinghis.model.Url;
import ch.tool.browsinghis.model.UrlVisit;
import ch.tool.browsinghis.model.Visit;
public class HistoryDao {
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String SELECT_URLS_ALL = "select id,url,title,visit_count,typed_count," +
			"datetime(last_visit_time / 1000000 + (strftime('%s', '1601-01-01 08:00:00')), 'unixepoch') last_visit_time,hidden from urls";
	private static final String SELECT_VISITS_ALL = "select id,url," +
			"datetime(visit_time / 1000000 + (strftime('%s', '1601-01-01 08:00:00')), 'unixepoch') visit_time,from_visit," +
			"transition,segment_id,visit_duration from visits";
	private static final String SELECT_URL_VISIT_ALL = "select url id,count(*) visit_count," +
			"max(datetime(visit_time / 1000000 + (strftime('%s', '1601-01-01 08:00:00')), 'unixepoch')) last_visit_time " +
			"from visits where visit_duration >= 0 group by url";
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
}