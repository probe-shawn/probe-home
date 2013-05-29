package xlive;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;

import xlive.google.ds.xFile;
import xlive.wrap.xWrapFileOutputStream;

public class xUtility {
	private static SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public xUtility(){}
	
	public static String formatDate(){
		return simpleDateFormat.format(new Date(System.currentTimeMillis()));
	}
	public static String formatDate(long currentTimeMillis){
		return simpleDateFormat.format(new Date(currentTimeMillis));
	}
	public static String formatDate(Date date){
		return simpleDateFormat.format(date);
	}
	public static String postfixTimeStamp(String name){
		String time=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		if(name==null)return time;
		int index=name.lastIndexOf(".");
		return (index>=0) ? name=name.substring(0,index)+time+name.substring(index) : name+time;
	}
	/*
	public static Date parseDate(String date_string){
		if(date_string==null||date_string.trim().length()==0)return null;
		try{
			return simpleDateFormat.parse(date_string);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	*/
    
    private static Pattern patternDate=null;
    public static Date parseDate(String date_string){
    	if(patternDate==null){
    		String r="(\\d{4})\\D*(\\d{2}|\\d{1})\\D*(\\d{2}|\\d{1})\\D*(\\d{2}|\\d*)\\D*(\\d{2}|\\d*)\\D*(\\d{2}|\\d*)\\D*(\\d{3}|\\d*)";
    		patternDate = Pattern.compile(r); 
    	}
    	Matcher matcher=patternDate.matcher(date_string);
    	int[] date_value = new int[7];
    	for(int i=0;i<date_value.length;++i)date_value[i]=-1;
    	boolean matches=matcher.matches();
    	if(matches){
    		for(int i=1;i<matcher.groupCount();++i){
    			try{
    				date_value[i-1]=Integer.parseInt(matcher.group(i));
    			}catch(Exception e){
    			}
    		}
    		if(date_value[0]>0 && date_value[1]>0 && date_value[2]>0){
    			Calendar cal=Calendar.getInstance();
    			cal.set(date_value[0],date_value[1]-1,date_value[2]);
    			if(date_value[3]>=0)cal.set(Calendar.HOUR_OF_DAY, date_value[3]);
    			else cal.set(Calendar.HOUR_OF_DAY, 0);
    			if(date_value[4]>=0) cal.set(Calendar.MINUTE, date_value[4]);
    			else cal.set(Calendar.MINUTE, 0);
    			if(date_value[5]>=0) cal.set(Calendar.SECOND, date_value[5]);
    			else cal.set(Calendar.SECOND, 0);
    			if(date_value[6]>=0) cal.set(Calendar.MILLISECOND, date_value[6]);
    			else cal.set(Calendar.MILLISECOND, 0);

    			return new Date(cal.getTimeInMillis());
    		}
    	}
		return null;
    }

	
    public static long copyStream(InputStream input, OutputStream output) {
        try {
        	long total=0;
            byte[] by = new byte[8*1024];
            int got = 0;
            while((got = input.read(by, 0, by.length)) != -1) {
            	output.write(by, 0, got);
            	total +=got;
            }
            return total;
        }catch(IOException e) {}
        return -1;
    }
    public static String streamToString(InputStream input) throws UnsupportedEncodingException{
    	return xUtility.streamToString(input, null);
    }
    public static String streamToString(InputStream input, String charname) throws UnsupportedEncodingException{
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	xUtility.copyStream(input, baos);
		return charname== null ? baos.toString() : baos.toString(charname);
    }
    
    public static boolean copyStream(InputStream input, OutputStream output,long length) {
        try {
        	long total_size=length;
            byte[] by = new byte[8*1024];
            int got = 0;
            long size=Math.min(by.length, total_size);
            while((got = input.read(by, 0, (int)size)) != -1) {
            	output.write(by, 0, got);
            	total_size-=got;
            	if(total_size<=0) break;
            	size=Math.min(by.length, total_size);
            }
            return true;
        }catch(IOException e) {}
        return false;
    }
    public static boolean copyFile(File file, File to_file){
    	if(file==null||to_file == null||!file.exists()) return false;
    	if(file.equals(to_file))return true;
    	boolean valid=true;
    	InputStream input=null;
    	OutputStream output=null;
    	try{
    		input=new FileInputStream(file);
    		output=xWrapFileOutputStream.fileOutputStream(to_file);
    		valid = xUtility.copyStream(input, output) >= 0;
    	}catch(Exception e){
    		valid=false;
    	}finally{
       		try{if(input != null) input.close();}catch(Exception e){}
       		try{if(output != null) output.close();}catch(Exception e){}
    	}
    	return valid;	
    }
    public static boolean copyFile(xFile file, xFile to_file){
    	if(file==null||to_file == null||!file.exists()) return false;
    	if(file.getFileName().equals(to_file.getFileName()))return true;
    	boolean valid=true;
    	try{
    		valid = to_file.setBytes(file.getBytes());
    	}catch(Exception e){
    		valid=false;
    	}
    	return valid;	
    }
    public static boolean moveFile(File file, File to_file){
    	if(file.equals(to_file)) return true;
    	boolean valid=xUtility.copyFile(file, to_file);
    	if(valid) file.delete();
    	return valid;
    }
    public static boolean moveDirectory(File dir, File to_dir){
       	if(dir==null||to_dir == null||!dir.exists()||!dir.isDirectory()) return false;
		boolean valid=true;
		if(!to_dir.exists()) valid &= to_dir.mkdirs();
		File[] files = dir.listFiles();
		for(int i =0; valid && i < files.length;++i){
			if(files[i].isDirectory()){
				valid &= moveDirectory(files[i], new File(to_dir, files[i].getName()));
				if(valid) files[i].delete();
			}else{
				valid &= moveFile(files[i], new File(to_dir, files[i].getName()));
			}
		}
		if(valid) valid &=dir.delete();
		return valid;
    }
    public static boolean moveDirectory(xFile dir, String to_dir){
       	if(dir==null||to_dir == null||!dir.exists()||!dir.isDirectory()) return false;
		boolean valid=true;
		xFile to_dir_file = new xFile(to_dir);
		if(!to_dir_file.exists()) valid &= to_dir_file.makeDirs();
		List<xFile> files = dir.list();
		Iterator<xFile> it = files.iterator();
		while(it.hasNext()){
			xFile file = it.next();
			if(file.isDirectory()){
				valid &= moveDirectory(file, to_dir+file.getName()+"/");
			}else{
				valid = file.renameTo(to_dir+file.getName(), true);
			}
		}
		if(valid) valid &=dir.rd();
		return valid;
    }
	public static String unixUNC(String unc_name){
		try {
			String unc = xUtility.unixPath(unc_name);
	        URL url = new URL("file:"+unc);
	        String uri = url.getPath();
	        String filename = url.getFile();
	        uri += (filename == null) ? "" : (filename.length() == 0 || uri.equals(filename)) ? "" : File.separator+filename;
	        return uri;
	    }catch(Exception e) {
	        return unc_name;
	    }
	}
	public static String unixPath(String path) {
		if(path == null) return null;
	    StringBuffer buf = new StringBuffer(path.length());
	    for(int i = 0; i < path.length(); ++i) {
	        buf.append((path.charAt(i) == '\\') ? File.separatorChar : path.charAt(i));
	    }
	    return buf.toString();
	}
	public static boolean isWindows(){
	    String osname = System.getProperty("os.name");
	    String filesep = System.getProperty("file.separator");
	    return (osname.toLowerCase().indexOf("window") > 0) || filesep.equals("\\");
	}
	public static String toUTFString(String string) {
	    StringBuffer buf = new StringBuffer();
	    int len = string.length();
	    char ch;
	    for(int i = 0; i < len; ++i) {
	         ch = string.charAt(i);
	         switch(ch) {
	             case '\\' : buf.append("\\\\"); break;
	             case '\t' : buf.append("\\t"); break;
	             case '\n' : buf.append("\\n"); break;
	             case '\r' : buf.append("\\r"); break;
	             default :
	               if(ch >= ' ' && ch <= 127) buf.append(ch);
	               else {
	                   buf.append('\\');
	                   buf.append('u');
	                   buf.append(toHex((ch >> 12) & 0xF));
	                   buf.append(toHex((ch >> 8) & 0xF));
	                   buf.append(toHex((ch >> 4) & 0xF));
	                   buf.append(toHex((ch >> 0) & 0xF));
	               }
	         }
	    }
	    return buf.toString();
	}
    private static char toHex(int nibble) {
    	char[] hexDigit = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e', 'f'};
        return hexDigit[(nibble & 0xF)];
    }
    public static void EntityToJSO(Entity entity, JSONObject jso) throws JSONException{
    	Map<String, Object> map = entity.getProperties();
    	for (Map.Entry<String, Object> entry : map.entrySet()) {
    		String key = entry.getKey();
    		Object value=entry.getValue();
    		if(value instanceof Text){
    			try {
    				String text=((Text)value).getValue();
    				if(text.startsWith("{")&&text.endsWith("}")) {
    					jso.put(key, new JSONObject(text));
    				} else if(text.startsWith("[")&&text.endsWith("]")){
    					jso.put(key, new JSONArray(text));
    				} else jso.put(key, text);
				} catch (JSONException e) {
					e.printStackTrace();
				}
    		}else if(value instanceof java.util.Date){
    			jso.put(key, xUtility.formatDate((Date)value));
    		}else {
    			jso.put(key, value);
    		}
    	}
    }
    public static void JSOToEntity(JSONObject jso, Entity entity) throws JSONException{
    	 Iterator<?> keys = jso.keys();
    	 while(keys.hasNext()){
             String key = (String)keys.next();
             if("key".equals(key)) continue;
             Object value = jso.get(key);
             if(key.endsWith("date")){
            	String date_str=(String) value; 
             	Date date=(date_str != null && date_str.trim().length() >= 8)? xUtility.parseDate(date_str):null;
            	entity.setProperty(key, date);
            	continue;
             }
             if(value instanceof JSONObject){
            	 String str= "{}";
            	 if(value != null && value instanceof JSONObject) str = ((JSONObject)value).toString();
            	 else if(value != null && value instanceof String) str = new JSONObject(str).toString();
            	 entity.setProperty(key, new Text(str.trim()));
             }else if(value instanceof JSONArray){
            	 String str= "[]";
            	 if(value != null && value instanceof JSONArray) str = ((JSONArray)value).toString();
            	 entity.setProperty(key, new Text(str.trim()));
             }else if(value instanceof String){
            	 String str= (String)value;
            	 if(str != null && str.length() > 255) entity.setProperty(key, new Text(str));
            	 else entity.setProperty(key, str);
             }else{
            	 entity.setProperty(key, value);
             }
    	 }
    }
    /*
    public static void JSOToEntityStrictUpdate(JSONObject jso, Entity entity) throws JSONException{
    	Map<String, Object> map = entity.getProperties();
    	for (Map.Entry<String, Object> entry : map.entrySet()) {
    		String key = entry.getKey();
    		Object value = jso.opt(key);
    		if(value == null) continue;
    		if(value instanceof JSONObject){
    			entity.setProperty(key, new Text(((JSONObject) value).toString()));
            }else if(value instanceof String){
	           	 String str= (String)value;
	           	 if(str != null && str.length() > 255) entity.setProperty(key, new Text(str));
	           	 else entity.setProperty(key, str);
    		}else{
    			entity.setProperty(key, value);
    		}
    	}
    }
    */
   
    
}
