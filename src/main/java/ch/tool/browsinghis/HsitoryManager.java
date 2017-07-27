package ch.tool.browsinghis;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import ch.tool.browsinghis.dao.HistoryDao;
import ch.tool.browsinghis.model.Sequence;
import ch.tool.browsinghis.model.Url;
import ch.tool.browsinghis.model.UrlVisit;
import ch.tool.browsinghis.model.Visit;
public class HsitoryManager {
	private Map<Long, Set<Visit>> allVisitMap;
	private Map<Long, Set<Url>> allUrlMap;
	private int visitsSize;
	private int urlsSize;
	// throw new RuntimeException("all
	// throw new RuntimeException("temp
	// throw new RuntimeException("url.
	// throw new RuntimeException("size
	// throw new RuntimeException(""
	// throw new RuntimeException("ascending"
	// .get(id).size() > 1)
	private void checkVisits(List<Visit> visits){
		if (allVisitMap == null)
    		allVisitMap = new HashMap<Long, Set<Visit>>();
    	Map<Long, Set<Visit>> tempVisitMap = new HashMap<Long, Set<Visit>>();
    	tempVisitMap.putAll(allVisitMap);
    	for (Visit visit : visits){
    		long id = visit.getId();
    		Set<Visit> visitSet = tempVisitMap.remove(id);
    		if (visitSet == null)
    			if (!allVisitMap.containsKey(id)){
    				visitSet = new LinkedHashSet<Visit>();
    				visitSet.add(visit);
    				allVisitMap.put(id, visitSet);
    			} else
    				throw new RuntimeException("allVisitMap");
    		else
    			visitSet.add(visit);
    	}
    	if (tempVisitMap.size() > 0)
    		throw new RuntimeException("tempVisitMap.size");
    	if (visitsSize == 0)
    		visitsSize = visits.size();
    	if (visits.size() < visitsSize || visits.size() != allVisitMap.size())
    		throw new RuntimeException("sizeVisits");
    	visitsSize = visits.size();
    	System.out.print(String.format("\tvisits.size() = %s", visits.size()));
	}
	private void checkUrls(List<Url> urls, Map<Long, UrlVisit> urlVisitMap){
		if (allUrlMap == null)
    		allUrlMap = new HashMap<Long, Set<Url>>();
    	Map<Long, Set<Url>> tempUrlMap = new HashMap<Long, Set<Url>>();
    	tempUrlMap.putAll(allUrlMap);
    	for (Url url : urls){
    		long id = url.getId();
    		Set<Url> urlSet = tempUrlMap.remove(id);
    		if (urlSet == null)
    			if (!allUrlMap.containsKey(id)){
    				urlSet = new LinkedHashSet<Url>();
    				urlSet.add(url);
    				allUrlMap.put(id, urlSet);
    			} else
    				throw new RuntimeException("allUrlMap");
    		else
    			urlSet.add(url);
    		if (url.getVisitCount() > urlVisitMap.get(id).getVisitCount())
    			throw new RuntimeException("url.getVisitCount");
    		if (!url.getLastVisitTime().equals(urlVisitMap.get(id).getLastVisitTime()))
    			throw new RuntimeException("url.getLastVisitTime");
    	}
    	if (tempUrlMap.size() > 0)
    		throw new RuntimeException("tempUrlMap.size");
    	if (urlsSize == 0)
    		urlsSize = urls.size();
    	if (urls.size() < urlsSize || urls.size() != allUrlMap.size())
    		throw new RuntimeException("sizeUrls");
    	urlsSize = urls.size();
    	System.out.print(String.format("\turls.size() = %s", urls.size()));
	}
	public void checkAll(String folderPath){
		Set<String> filePathSet = new TreeSet<String>();
        for (File file : new File(folderPath).listFiles())
        	filePathSet.add(file.getAbsolutePath());
        int n = 0;
        for (String filePath : filePathSet){
        	System.out.print((++n < 10 ? "0" : "") + n + ": ");
        	System.out.print(filePath);
        	HistoryDao history = new HistoryDao(filePath);
        	checkVisits(history.getVisits());
        	checkUrls(history.getUrls(), history.getUrlVisitMap());
        	System.out.println();
        }
        System.out.println(String.format("allVisitMap.size() = %s\tallUrlMap.size() = %s", allVisitMap.size(), allUrlMap.size()));
        System.out.println("------------------------------------");
        Map<Long, Set<Visit>> resultVisitMap = new HashMap<Long, Set<Visit>>();
        for (Long id : allVisitMap.keySet())
        	if (allVisitMap.get(id).size() == 0)
        		throw new RuntimeException("");
        	else if (allVisitMap.get(id).size() > 1)
        		resultVisitMap.put(id, allVisitMap.get(id));
        for (Long id : resultVisitMap.keySet())
        	System.out.println(String.format("%s\t%s", id, diffSet(resultVisitMap.get(id), Visit.class, null)));
        System.out.println("------------------------------------");
        Map<Long, Set<Url>> resultUrlMap = new HashMap<Long, Set<Url>>();
        for (Long id : allUrlMap.keySet())
        	if (allUrlMap.get(id).size() == 0)
        		throw new RuntimeException("");
        	else if (allUrlMap.get(id).size() > 1)
        		resultUrlMap.put(id, allUrlMap.get(id));
        List<Set<Url>> resultUrlList = new java.util.ArrayList<Set<Url>>(resultUrlMap.values());
        Collections.sort(resultUrlList, new Comparator<Set<Url>>() {
			public int compare(Set<Url> set1, Set<Url> set2) {
				return set1.iterator().next().getId() - set2.iterator().next().getId() < 0 ? -1 : 1;
			}
		});
        for (Set<Url> urlSet : resultUrlList){
        	Iterator<Url> iter = urlSet.iterator();
        	Url url = iter.next();
        	String print = String.format("%s\t'%s'\t%s", url.getId(), url.getTitle(), diffSet(urlSet, Url.class, null));
        	if (diffSet(urlSet, Url.class, "title").size() == 0)
        		System.out.println(print);
        	else
        		System.err.println(print);
        	while (iter.hasNext()){
        		Url temp = iter.next();
        		if (temp.getLastVisitTime().before(url.getLastVisitTime()))
        			throw new RuntimeException("ascending");
        		if (temp.getVisitCount() < url.getVisitCount())
        			throw new RuntimeException("ascending");
        		url = temp;
        	}
        }
        System.out.println("------------------------------------");
	}
	private <T> List<Set<Object>> diffSet(Set<T> set, Class<T> cls, String name){
		List<Set<Object>> list = new LinkedList<Set<Object>>();
		try {
			for (PropertyDescriptor prop : Introspector.getBeanInfo(cls).getPropertyDescriptors()){
				if (name == null || name.trim().isEmpty() || name.trim().equals(prop.getName())){
					Set<Object> unequal = new LinkedHashSet<Object>();
					for (T t : set)
						unequal.add(prop.getReadMethod().invoke(t));
					if (unequal.size() > 1)
						list.add(unequal);
				}
			}
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		return list;
	}
	public void checkSequence(String folderPath, String url, int before, int after){
		List<File> fileList = java.util.Arrays.asList(new File(folderPath).listFiles());
		Collections.sort(fileList, new java.util.Comparator<File>(){
			public int compare(File f1, File f2) {
				long last1 = f1.lastModified();
				long last2 = f2.lastModified();
				return last1 == last2 ? 0 : last1 < last2 ? 1 : -1;
			}
		});
		System.out.println(fileList.get(0) + "\n");
		for (Sequence seq : HistoryDao.querySequence(fileList.get(0).getAbsolutePath(), url, before, after)){
			if (seq != null){
				String strUrlDate = new java.text.SimpleDateFormat(HistoryDao.DATE_FORMAT).format(seq.getUrlDate());
				String strUrlId = fixLen(seq.getUrlId(), 5);
				String strFromUrlId = fixLen(seq.getFromUrlId(), 5);
				String print = String.format("%s, %s, %s, %s, %s, %s", strUrlDate, strUrlId, strFromUrlId, seq.getTitle(), seq.getFromTitle(), seq.getUrl());
				if (seq.getUrl().equals(url) || seq.getTitle().indexOf(url) > -1)
					System.err.println(print);
				else
					System.out.println(print);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else
				System.out.println();
    	}
	}
	private String fixLen(Object obj, int len){
		if (obj == null) obj = "";
		StringBuilder builder = new StringBuilder(obj.toString());
		while (builder.length() < len)
			builder.insert(0, " ");
		builder.setLength(len);
		return builder.toString();
	}
}