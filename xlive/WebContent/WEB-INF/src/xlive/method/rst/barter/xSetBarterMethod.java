package xlive.method.rst.barter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import xlive.method.*;
import xlive.xml.xXmlDocument;

public class xSetBarterMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String keystr=this.getArguments("key");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity entity = null;
		if(keystr != null && keystr.trim().length()>0){
			try{
				entity = ds.get(KeyFactory.stringToKey(keystr));
			}catch(EntityNotFoundException e){entity = null;}
		}
		if(entity == null) {
			entity = new Entity(xBarter.class.getSimpleName());
			entity.setProperty("squDate", new Date());
			entity.setProperty("createDate", new Date());
			entity.setProperty("creatorFid",this.getArguments("creator-fid"));
			entity.setProperty("creatorName",this.getArguments("creator-name"));
		}
		xBarter barter = new xBarter(entity);
		barter.setDate(new Date());
		barter.setOwnerFid(this.getArguments("owner-fid"));
		barter.setOwnerName(this.getArguments("owner-name"));
		barter.setId(this.getArguments("id"));
		barter.setName(this.getArguments("name"));
		barter.setIcon(this.getArguments("icon"));
		barter.setType(this.getArguments("type"));
		barter.setStatus(this.getArguments("status"));
		Element icons_element=(Element)this.getArguments("icons", XPathConstants.NODE);
		if(icons_element != null){
			List<String> icons = new ArrayList<String>();
			NodeList node_list = (NodeList)icons_element.getElementsByTagName("icon");
			for(int i = 0; i < node_list.getLength();++i){
				Element ele = (Element)node_list.item(i);
				String file=ele.getTextContent();
				if(file != null && file.trim().length() > 0) icons.add(ele.getTextContent());
			}
			barter.setIcons(icons);
		}else barter.setIcons(null);
		//
		barter.setDescHtml(this.getArguments("desc-html"));
		
		Element spec_element=(Element)this.getArguments("spec-xml", XPathConstants.NODE);
		if(spec_element != null && spec_element.hasChildNodes())
			 barter.setSpecXml(xXmlDocument.nodeToBytes(spec_element,null));
		else barter.setSpecXml(null);
		
		Element hist_element=(Element)this.getArguments("hist-xml", XPathConstants.NODE);
		if(hist_element != null && hist_element.hasChildNodes())
			 barter.setHistXml(xXmlDocument.nodeToBytes(hist_element,null));
		else barter.setHistXml(null);

		barter.setRemoved(0);
		//
		ds.put(barter.entity);
		//
		if(barter != null){
			Element data=this.setReturnArguments("data", "");
			Element barter_element=xBarter.barterXml(barter, true);
			data.appendChild(barter_element);
			try{
				String data_str=xXmlDocument.nodeToString(xBarter.barterXml(barter,false));
				StringBuffer msg =  new StringBuffer();
				msg.append("<message>")
					.append("<target>barter</target>")
					.append("<op>set</op>")
					.append("<data>").append(data_str).append("</data>")
				.append("</message>");
				xlive.method.channel.xSendMessageMethod.sendMessage(this.getArguments("client-id"), null, null, null, barter.getOwnerFid(), msg.toString());
			}catch(Exception e){}
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		//
		return this.getServiceContext().doNextProcess();
	}
}
