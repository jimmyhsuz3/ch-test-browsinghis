package ch.tool.browsinghis.model;
import java.util.Date;
public class Visit {
	private long id;
	private long url;
	private Date visitTime;
	private long fromVisit;
	private long transition;
	private int segmentId;
	private long visitDuration;
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Visit){
			Visit visit = (Visit) obj;
			return id == visit.id && url == visit.url && visitTime.equals(visit.visitTime) && fromVisit == visit.fromVisit &&
					transition == visit.transition && segmentId == visit.segmentId && visitDuration == visit.visitDuration;
		}
		return false;
	}
	@Override
	public int hashCode() {
		return java.util.Objects.hash(id, url, visitTime, fromVisit, transition, segmentId, visitDuration);
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getUrl() {
		return url;
	}
	public void setUrl(long url) {
		this.url = url;
	}
	public Date getVisitTime() {
		return visitTime;
	}
	public void setVisitTime(Date visitTime) {
		this.visitTime = visitTime;
	}
	public long getFromVisit() {
		return fromVisit;
	}
	public void setFromVisit(long fromVisit) {
		this.fromVisit = fromVisit;
	}
	public long getTransition() {
		return transition;
	}
	public void setTransition(long transition) {
		this.transition = transition;
	}
	public int getSegmentId() {
		return segmentId;
	}
	public void setSegmentId(int segmentId) {
		this.segmentId = segmentId;
	}
	public long getVisitDuration() {
		return visitDuration;
	}
	public void setVisitDuration(long visitDuration) {
		this.visitDuration = visitDuration;
	}
}