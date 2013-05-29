package xlive.method.rst.circle;
import java.util.Date;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Element;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import xlive.method.*;
import xlive.xml.xXmlDocument;

public class xSetDistrictMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String key=this.getArguments("district.key");
		String fid=this.getArguments("district.circle-fid");
	//	System.out.println("key="+key);
	//	System.out.println("fid="+fid);
		Entity entity =null;
		xDistrict district=null;
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		if(entity == null && key != null && key.trim().length()>0){
			try{
				entity = ds.get(KeyFactory.stringToKey(key));
			}catch(EntityNotFoundException e){entity = null;}
		}
		if(entity == null){
			entity = new Entity(xDistrict.class.getSimpleName());
			district = new xDistrict(entity);
			district.setCircleFid(fid);
			district.setDate(new Date());
			district.setStatus("0");
			district.setSort(new Date().getTime());
		}else{
			district = new xDistrict(entity);
		}
		if(this.getArguments("district.name").length()>0)
			district.setName(this.getArguments("district.name"));
		//district.setDesc(this.getArguments("district.desc"));	
		if(this.getArguments("district.status").length()>0)
			district.setStatus(this.getArguments("district.status"));
		if(this.getArguments("district.qty").length()>0)
			district.setQty(this.getArguments("district.qty"));
		if(this.getArguments("district.sort").length()>0)
			district.setSort(this.getArguments("district.sort"));
		Element spec_element=(Element)this.getArguments("district.spec-xml", XPathConstants.NODE);
		if(spec_element != null && spec_element.hasChildNodes())
			district.setSpecXml(xXmlDocument.nodeToBytes(spec_element,null));
		else district.setSpecXml(null);
		String n1,n2,n3;
		n1=this.getArguments("district.news1");
		n2=this.getArguments("district.news2");
		n3=this.getArguments("district.news3");
		district.setNews(null);		
		if(n1.length()>0)
			district.addNew(n1);
		if(n2.length()>0)
			district.addNew(n2);
		if(n3.length()>0)
			district.addNew(n3);
    	//district.setIcon(this.getArguments("district.icon"));
		//icons
		/*
		Element icons_element=(Element)this.getArguments("district.icons", XPathConstants.NODE);
		if(icons_element != null){
			List<String> icons = new ArrayList<String>();
			NodeList node_list = (NodeList)icons_element.getElementsByTagName("icon");
			for(int i = 0; i < node_list.getLength();++i){
				Element ele = (Element)node_list.item(i);
				String file=ele.getTextContent();
				if(file != null && file.trim().length() > 0) icons.add(ele.getTextContent());
			}
			district.setIcons(icons);
		}else district.setIcons(null);
		
		//news
		Element news_element=(Element)this.getArguments("district.news", XPathConstants.NODE);
		if(news_element != null){
			List<String> news = new ArrayList<String>();
			NodeList node_list = (NodeList)news_element.getElementsByTagName("new");
			for(int i = 0; i < node_list.getLength();++i){
				Element ele = (Element)node_list.item(i);
				String str=ele.getTextContent();
				if(str != null && str.trim().length() > 0) news.add(ele.getTextContent());
			}
			district.setNews(news);
		}else district.setNews(null);
		*/
		ds.put(district.entity);
		/*
		*/
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		this.setReturnArguments("key", district.getKey());
		//
		return this.getServiceContext().doNextProcess();
	}
}
