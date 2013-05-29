package xlive.method.rst.barter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import xlive.xUtility;
import xlive.method.*;
import xlive.xml.xXmlDocument;

public class xAgreeLoveMeMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		Element data = this.setReturnArguments("data", "");
		String my_love_keystr=this.getArguments("my-love-key");
		String love_me_keystr=this.getArguments("love-me-key");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		ArrayList<Entity> put_array = new ArrayList<Entity>();
		xBarter my_love_barter=null;
		xBarter love_me_barter=null;
		try {
			Entity my_love_entity = ds.get(KeyFactory.stringToKey(my_love_keystr));
	    	my_love_barter = new xBarter(my_love_entity);
			Entity love_me_entity=ds.get(KeyFactory.stringToKey(love_me_keystr));
			love_me_barter = new xBarter(love_me_entity);
			String love_me_owner_fid=love_me_barter.getOwnerFid();
			String love_me_owner_name=love_me_barter.getOwnerName();
			Date love_me_squdate =love_me_barter.getSquDate();
			String my_love_owner_fid=my_love_barter.getOwnerFid();
			String my_love_owner_name=my_love_barter.getOwnerName();
			Date my_love_squdate =my_love_barter.getSquDate();
	    	put_array.add(my_love_entity);
	    	put_array.add(love_me_entity);
			//check she donot love me anymore
			// 
			String still_love_me = love_me_barter.getILove();
			if(!my_love_keystr.equals(still_love_me)){
				my_love_barter.removeLoveMe(love_me_keystr);
				ds.put(my_love_barter.entity);
				why = "在當下,對方已取消易物";
				data.appendChild(xBarter.barterXml(my_love_barter, true));
				this.setReturnArguments("valid", String.valueOf(valid));
				this.setReturnArguments("why", why);
				return this.getServiceContext().doNextProcess();
			}
			//clear love_me_barter
			love_me_barter.setILove(null);
			List<String> love_me_barter_lovemes = love_me_barter.getLoveMes();
			if(love_me_barter_lovemes != null){
				for(String love_me : love_me_barter_lovemes){
					Entity entity = this.seekEntity(put_array,love_me);
					xBarter barter = new xBarter((entity!=null)? entity : ds.get(KeyFactory.stringToKey(love_me)));
					barter.setILove(null);
					put_array.add(barter.entity);
				}
			}
			//clear my_love_bartet
			//-- i_love
			String i_love=my_love_barter.getILove();
			if(i_love!=null && i_love.trim().length()>0){
				Entity entity = this.seekEntity(put_array,i_love);
				xBarter barter = new xBarter((entity!=null)? entity : ds.get(KeyFactory.stringToKey(i_love)));
				barter.removeLoveMe(my_love_keystr);
				put_array.add(barter.entity);
			}
			// love mes
			List<String> my_love_barter_lovemes = my_love_barter.getLoveMes();
			if(my_love_barter_lovemes != null){
				for(String love_me : my_love_barter_lovemes){
					if(!love_me.equals(love_me_keystr)){
						Entity entity = this.seekEntity(put_array,love_me);
						xBarter barter = new xBarter((entity!=null)? entity : ds.get(KeyFactory.stringToKey(love_me)));
						barter.setILove(null);
						put_array.add(barter.entity);
					}
				}
			}
			//history
			org.w3c.dom.Document doc = data.getOwnerDocument();
			byte[] bytes=love_me_barter.getHistXml();
			Element hists=(bytes==null || bytes.length==0)? doc.createElement("hist-xml"):(Element)xXmlDocument.bytesToNode(bytes);
			Element hist=doc.createElement("hist");
			hist.setAttribute("date", xUtility.formatDate());
			hist.setAttribute("owner", love_me_barter.getOwnerFid());
			hist.setAttribute("xOwner", my_love_barter.getOwnerFid());
			hist.setAttribute("xKey", my_love_barter.getKey());
			hist.setAttribute("xName", my_love_barter.getName());
			hist.setAttribute("xIcon", my_love_barter.getIcon());
			hists.insertBefore(hists.getOwnerDocument().adoptNode(hist), hists.getFirstChild());
			love_me_barter.setHistXml(xXmlDocument.nodeToBytes(hists,null));
			//
			bytes=my_love_barter.getHistXml();
			hists=(bytes==null || bytes.length==0)? doc.createElement("hist-xml"):(Element)xXmlDocument.bytesToNode(bytes);
			hist=doc.createElement("hist");
			hist.setAttribute("date", xUtility.formatDate());
			hist.setAttribute("owner", my_love_barter.getOwnerFid());
			hist.setAttribute("xOwner", love_me_barter.getOwnerFid());
			hist.setAttribute("xKey", love_me_barter.getKey());
			hist.setAttribute("xName", love_me_barter.getName());
			hist.setAttribute("xIcon", love_me_barter.getIcon());
			hists.insertBefore(hists.getOwnerDocument().adoptNode(hist), hists.getFirstChild());
			my_love_barter.setHistXml(xXmlDocument.nodeToBytes(hists,null));
			//
			love_me_barter.setILove(null);
			love_me_barter.setLoveMes(null);
			love_me_barter.setOwnerFid(my_love_owner_fid);
			love_me_barter.setOwnerName(my_love_owner_name);
			love_me_barter.setSquDate(my_love_squdate);
			love_me_barter.setStatus(xBarter.status_barter);
			
			my_love_barter.setILove(null);
			my_love_barter.setLoveMes(null);
			my_love_barter.setOwnerFid(love_me_owner_fid);
			my_love_barter.setOwnerName(love_me_owner_name);
			my_love_barter.setSquDate(love_me_squdate);
			my_love_barter.setStatus(xBarter.status_barter);
			
			ds.put(put_array);
			data.appendChild(xBarter.barterXml(love_me_barter, true));
		} catch (EntityNotFoundException e) {}
		
		
		if(valid){
			String client_id=this.getArguments("client-id");
			for(Entity entity : put_array){
				try{
					xBarter barter = new xBarter(entity);
					String key = barter.getKey();
					if(key.equals(my_love_keystr) || key.equals(love_me_keystr))continue;
					String data_str=xXmlDocument.nodeToString(xBarter.barterXml(barter,false));
					StringBuffer msg =  new StringBuffer();
					msg.append("<message>")
						.append("<target>barter</target>")
						.append("<op>set</op>")
						.append("<data>").append(data_str).append("</data>")
					.append("</message>");
					xlive.method.channel.xSendMessageMethod.sendMessage(null, null, null, null,barter.getOwnerFid(), msg.toString());
				}catch(Exception e){}
			}
			//
			String my_love=xXmlDocument.nodeToString(xBarter.barterXml(my_love_barter,false));
			String love_me=xXmlDocument.nodeToString(xBarter.barterXml(love_me_barter,false));
			StringBuffer msg =  new StringBuffer();
			msg.append("<message>")
				.append("<target>barter</target>")
				.append("<op>agree-love-me</op>")
				.append("<data>")
					.append("<my-love>").append(my_love).append("</my-love>")
					.append("<love-me>").append(love_me).append("</love-me>")
				.append("</data>")
			.append("</message>");
			xlive.method.channel.xSendMessageMethod.sendMessage(client_id, null, null, null,love_me_barter.getOwnerFid(), msg.toString());
				
				
			msg =  new StringBuffer();
			msg.append("<message>")
				.append("<target>barter</target>")
				.append("<op>agree-love-me</op>")
				.append("<data>")
					.append("<my-love>").append(love_me).append("</my-love>")
					.append("<love-me>").append(my_love).append("</love-me>")
				.append("</data>")
			.append("</message>");
			xlive.method.channel.xSendMessageMethod.sendMessage(client_id, null, null, null,my_love_barter.getOwnerFid(), msg.toString());
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
	private Entity seekEntity(ArrayList<Entity> put_array, String keystr){
		Key key=KeyFactory.stringToKey(keystr);
		for(Entity seek : put_array){
			if(seek.getKey().equals(key)) return seek;
		}
		return null;
	}
}
