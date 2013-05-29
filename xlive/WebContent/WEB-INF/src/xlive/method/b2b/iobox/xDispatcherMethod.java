package xlive.method.b2b.iobox;

import java.io.File;
import java.util.Vector;
import java.util.regex.Pattern;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import xlive.xUtility;
import xlive.method.*;

public class xDispatcherMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		return synchronized_process();
	}
	public synchronized boolean synchronized_process() throws xMethodException{
	    String inbox_directory=getProperties("inbox-directory");
	    File inbox_file=directoryResolve(inbox_directory);
	    NodeList node_list=(NodeList)getArguments("match", XPathConstants.NODESET);
		boolean valid=true;
		String why="";
	    Element data = setReturnArguments("data", "");
	    Vector<matcher> matchers = new Vector<matcher>();
	    for(int i =0; node_list != null && i < node_list.getLength();++i){
	    	Node node = (Node)node_list.item(i);
	    	String file_regex=(String)evaluate("file-regex", node, XPathConstants.STRING);
	    	String dir_regex=(String)evaluate("directory-regex", node, XPathConstants.STRING);
	    	String to_directory=(String)evaluate("to-directory", node, XPathConstants.STRING);
	    	if(to_directory != null && to_directory.trim().length() > 0){
	    		matchers.add(new matcher(file_regex, dir_regex, directoryResolve(to_directory)));
	    	}
	    }
	    if(matchers.size() > 0){
    		File[] files = inbox_file.listFiles();
    		for(int i = 0; i < files.length; ++i){
    			for(int j = 0; j < matchers.size(); ++j){
    				if(matchers.get(j).matchAndDispatch(files[i])) {
    					Element file=createElement(files[i].isFile() ? "file" : "directory");
    					file.setAttribute("name", files[i].getName());
    					file.setAttribute("toDirectory", matchers.get(j).getToDirectory().getAbsolutePath());
    					data.appendChild(file);
    					break;
    				}
    			}
    		}
    	}
	    setReturnArguments("valid", valid ? "true":"false");
	    setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
	class matcher {
		Pattern filePattern;
		Pattern dirPattern;
		File toDirectory;
		matcher(String file_regex, String dir_regex, File to_directory){
			if(file_regex != null && file_regex.trim().length()>0)filePattern = Pattern.compile(file_regex);
			if(dir_regex != null && dir_regex.trim().length()>0)dirPattern = Pattern.compile(dir_regex);
			toDirectory=to_directory;
		}
		boolean isMatch(File file){
			if(file.isFile() && filePattern != null && filePattern.matcher(file.getName()).matches()) return true;
			if(file.isDirectory() && dirPattern != null && dirPattern.matcher(file.getName()).matches())return true;
			return false;
		}
		boolean dispatch(File file){
			if(!toDirectory.exists()) toDirectory.mkdirs();
			File to_file = new File(toDirectory, file.getName());
			boolean valid=true;
			if(file.isFile()) valid &= xUtility.moveFile(file, to_file);
			if(file.isDirectory()) valid &= xUtility.moveDirectory(file, to_file);
			return valid;
		}
		boolean matchAndDispatch(File file){
			if(isMatch(file)) return dispatch(file);
			return false;
		}
		File getToDirectory(){
			return toDirectory;
		}
	}

}
