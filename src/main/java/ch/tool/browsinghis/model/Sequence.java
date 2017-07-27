package ch.tool.browsinghis.model;
import java.util.Date;
public class Sequence {
	private long visitId;
	private long fromVisitId;
	private long urlId;
	private long fromUrlId;
	private String url;
	private String fromUrl;
	private Date urlDate;
	private Date fromUrlDate;
	private String title;
	private String fromTitle;
	public long getVisitId() {
		return visitId;
	}
	public void setVisitId(long visitId) {
		this.visitId = visitId;
	}
	public long getFromVisitId() {
		return fromVisitId;
	}
	public void setFromVisitId(long fromVisitId) {
		this.fromVisitId = fromVisitId;
	}
	public long getUrlId() {
		return urlId;
	}
	public void setUrlId(long urlId) {
		this.urlId = urlId;
	}
	public long getFromUrlId() {
		return fromUrlId;
	}
	public void setFromUrlId(long fromUrlId) {
		this.fromUrlId = fromUrlId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFromUrl() {
		return fromUrl;
	}
	public void setFromUrl(String fromUrl) {
		this.fromUrl = fromUrl;
	}
	public Date getUrlDate() {
		return urlDate;
	}
	public void setUrlDate(Date urlDate) {
		this.urlDate = urlDate;
	}
	public Date getFromUrlDate() {
		return fromUrlDate;
	}
	public void setFromUrlDate(Date fromUrlDate) {
		this.fromUrlDate = fromUrlDate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFromTitle() {
		return fromTitle;
	}
	public void setFromTitle(String fromTitle) {
		this.fromTitle = fromTitle;
	}
}