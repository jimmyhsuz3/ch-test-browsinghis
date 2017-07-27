package ch.tool.browsinghis;
public class HistoryMain {
    public static void main(String[] args){
    	System.out.println(java.util.Arrays.asList(args));
    	String folderPath;
    	folderPath= "C:/Users/jimmy.shu/Downloads/SQLiteDatabaseBrowserPortable/test";
    	if (args[0].equals("checkAll"))
    		new HsitoryManager().checkAll(folderPath);
    	else {
    		String url;
    		url = "InvocationHandler";
    		url = "http://www.cnblogs.com/xiaoluo501395377/p/3383130.html";
    		int before = 10;
    		int after = 5;
    		if (args[0].length() > 1)
    			url = args[0].trim();
    		if (args.length > 1)
    			before = Integer.parseInt(args[1]);
    		if (args.length > 2)
    			after = Integer.parseInt(args[2]);
    		new HsitoryManager().checkSequence(folderPath, url, before, after);
    	}
    }
}