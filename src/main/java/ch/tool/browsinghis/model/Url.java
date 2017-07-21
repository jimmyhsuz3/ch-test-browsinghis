package ch.tool.browsinghis.model;
import java.util.Date;
public class Url {
	private long id;
	private String url;
	private String title;
	private int visitCount;
	private int typedCount;
	private Date lastVisitTime;
	private int hidden;
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Url){
			Url url = (Url) obj;
			return id == url.id && this.url.equals(url.url) && title.equals(url.title) && visitCount == url.visitCount &&
					typedCount == url.typedCount && lastVisitTime.equals(url.lastVisitTime) && hidden == url.hidden;
		}
		return false;
	}
	@Override
	public int hashCode() {
		return java.util.Objects.hash(id, url, title, visitCount, typedCount, lastVisitTime, hidden);
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getVisitCount() {
		return visitCount;
	}
	public void setVisitCount(int visitCount) {
		this.visitCount = visitCount;
	}
	public int getTypedCount() {
		return typedCount;
	}
	public void setTypedCount(int typedCount) {
		this.typedCount = typedCount;
	}
	public Date getLastVisitTime() {
		return lastVisitTime;
	}
	public void setLastVisitTime(Date lastVisitTime) {
		this.lastVisitTime = lastVisitTime;
	}
	public int getHidden() {
		return hidden;
	}
	public void setHidden(int hidden) {
		this.hidden = hidden;
	}
}