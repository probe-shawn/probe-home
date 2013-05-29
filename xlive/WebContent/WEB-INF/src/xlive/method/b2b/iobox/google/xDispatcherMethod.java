package xlive.method.b2b.iobox.google;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import xlive.xUtility;
import xlive.google.ds.xFile;
import xlive.method.*;

public class xDispatcherMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		return synchronized_process();
	}
	public synchronized boolean synchronized_process() throws xMethodException{
	    String inbox_directory=getProperties("inbox-directory");
	    inbox_directory = this.resourceDirectoryConvert(inbox_directory);
	    //
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
	    		to_directory = resourceDirectoryConvert(to_directory);
	    		matchers.add(new matcher(file_regex, dir_regex, to_directory));
	    	}
	    }
	    if(matchers.size() > 0){
	    	xFile inbox_directory_file = new xFile(inbox_directory);
	    	List<xFile> files = inbox_directory_file.list();
	    	Iterator<xFile> it = files.iterator();
	    	while(it.hasNext()){
	    		xFile file = it.next();
    			for(int j = 0; j < matchers.size(); ++j){
    				if(matchers.get(j).matchAndDispatch(file)) {
    					Element file_element=createElement(file.isFile() ? "file" : "directory");
    					file_element.setAttribute("name", file.getName());
    					file_element.setAttribute("toDirectory", matchers.get(j).getToDirectory());
    					data.appendChild(file_element);
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
		String toDirectory;
		matcher(String file_regex, String dir_regex, String to_directory){
			if(file_regex != null && file_regex.trim().length()>0)filePattern = Pattern.compile(file_regex);
			if(dir_regex != null && dir_regex.trim().length()>0)dirPattern = Pattern.compile(dir_regex);
			toDirectory=to_directory;
		}
		boolean isMatch(xFile xfile){
			if(xfile.isFile() && filePattern != null && filePattern.matcher(xfile.getName()).matches()) return true;
			if(xfile.isDirectory() && dirPattern != null && dirPattern.matcher(xfile.getName()).matches())return true;
			return false;
		}
		boolean dispatch(xFile xfile){
			boolean valid=true;
			if(xfile.isFile()) valid &= xfile.renameTo(toDirectory+xfile.getName(), true);
			if(xfile.isDirectory()) valid &= xUtility.moveDirectory(xfile, toDirectory);
			return valid;
		}
		boolean matchAndDispatch(xFile file){
			if(isMatch(file)) return dispatch(file);
			return false;
		}
		String getToDirectory(){
			return toDirectory;
		}
	}

}
