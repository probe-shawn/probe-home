package xlive.method.rst.menu;

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
import com.google.appengine.api.datastore.Transaction;

import xlive.method.*;
import xlive.xml.xXmlDocument;

public class xSetBomMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String keystr=this.getArguments("key");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity entity = null;
		
		if(keystr != null && keystr.trim().length()>0){
			try{
				entity = ds.get(KeyFactory.stringToKey(keystr));
				if(entity.getProperty("createDte")==null)entity.setProperty("createDate", new Date());
			}catch(EntityNotFoundException e){entity = null;}
		}
		if(entity == null) {
			entity = new Entity(xBom.class.getSimpleName());
			entity.setProperty("createDate", new Date());
		}
		xBom bom = new xBom(entity);
		
		bom.setParentKey(this.getArguments("parent-key"));
		bom.setMallFid(this.getArguments("mall-fid"));
		bom.setId(this.getArguments("id"));
		bom.setName(this.getArguments("name"));
		bom.setBarcode(this.getArguments("barcode"));
		bom.setPrice(this.getArguments("price"));
		bom.setListPrice(this.getArguments("list-price"));
		bom.setDate(new Date());
		bom.setIcon(this.getArguments("icon"));
		bom.setIcon2(this.getArguments("icon2"));
		bom.setType(this.getArguments("type"));
		bom.setStatus(this.getArguments("status"));
		bom.setMarkers(this.getArguments("markers"));
		bom.setBonus(this.getArguments("bonus"));
		bom.setQty(this.getArguments("qty"));
		long old_limit_qty = bom.getLimitQty();
		bom.setLimitQty(this.getArguments("limit-qty"));
		
		bom.setMaxBuy(this.getArguments("max-buy"));
		bom.setFans(this.getArguments("fans"));
		bom.setLimitBuy(this.getArguments("limit-buy"));
		//icons
		Element icons_element=(Element)this.getArguments("icons", XPathConstants.NODE);
		if(icons_element != null){
			List<String> icons = new ArrayList<String>();
			NodeList node_list = (NodeList)icons_element.getElementsByTagName("icon");
			for(int i = 0; i < node_list.getLength();++i){
				Element ele = (Element)node_list.item(i);
				String file=ele.getTextContent();
				if(file != null && file.trim().length() > 0) icons.add(ele.getTextContent());
			}
			bom.setIcons(icons);
		}else bom.setIcons(null);
		//
		bom.setBriefHtml(this.getArguments("brief-html"));
		bom.setDescHtml(this.getArguments("desc-html"));
		
		Element spec_element=(Element)this.getArguments("spec-xml", XPathConstants.NODE);
		if(spec_element != null && spec_element.hasChildNodes())
			 bom.setSpecXml(xXmlDocument.nodeToBytes(spec_element,null));
		else bom.setSpecXml(null);

		Element option_element=(Element)this.getArguments("option-xml", XPathConstants.NODE);
		if(option_element != null && option_element.getTextContent().trim().length() > 0)
			 bom.setOptionXml(xXmlDocument.nodeToBytes(option_element,null));
		else bom.setOptionXml(null);
		Element addon_element=(Element)this.getArguments("addon-xml", XPathConstants.NODE);
		if(addon_element != null && addon_element.getTextContent().trim().length() > 0)
			 bom.setAddonXml(xXmlDocument.nodeToBytes(addon_element,null));
		else bom.setAddonXml(null);
		//
		//options
		bom.setOptions(this.getArguments("options"));
		bom.setAddons(this.getArguments("addons"));
		//
		ds.put(bom.entity);
		//
		long add_qty=0;
		String add_qty_string = this.getArguments("add-qty");
		if(add_qty_string != null){
			try{
				add_qty=Long.parseLong(add_qty_string);
			}catch(Exception e){}
		}
		long current_qty = 0;
		if((bom.getLimitQty()==1 && add_qty != 0) || (old_limit_qty == 1 && bom.getLimitQty() == 0) ){
			Entity bomlimit_entity=null;
			try {
				bomlimit_entity=ds.get(KeyFactory.createKey(xBomLimit.class.getSimpleName(), bom.getKey()));
				xBomLimit bomlimit = new xBomLimit(bomlimit_entity);
				current_qty=bomlimit.getQty()+add_qty;
				if(old_limit_qty == 1 && bom.getLimitQty()==0){
					add_qty = 0;
					current_qty = 0;
				}
				bomlimit.setQty(current_qty);
				int retries = 3;
		    	while(valid) {
		    	    Transaction txn = ds.beginTransaction();
		    	    try {
	    	    		ds.put(bomlimit_entity);
	    	    		txn.commit();
		    	        break;
		    	    } catch (java.util.ConcurrentModificationException  e) {
		    	        if(retries == 0) throw e;
		    	        --retries;
		    	    } finally {
		    	        if(txn.isActive())txn.rollback();
		    	    }
		    	} 
			} catch (EntityNotFoundException e) {
				bomlimit_entity = new Entity(KeyFactory.createKey(xBomLimit.class.getSimpleName(), bom.getKey()));
				xBomLimit bomlimit = new xBomLimit(bomlimit_entity);
				bomlimit.setQty(add_qty);
				current_qty=add_qty;
				ds.put(bomlimit_entity);
			}
		}else{
			if(bom.getLimitQty()==1){
	        	try {
	        		Entity bomlimit_entity=ds.get(KeyFactory.createKey(xBomLimit.class.getSimpleName(), bom.getKey()));
	        		current_qty = new xBomLimit(bomlimit_entity).getQty();
				} catch (EntityNotFoundException e) {}
			}
		}
		//
		if(bom != null){
			Element data = this.setReturnArguments("data", "");
			data.appendChild(xBom.bomXml(bom, 0));
			//this.setReturnArguments("data.bom.key", KeyFactory.keyToString(bom.entity.getKey()));
			//this.setReturnArguments("data.bom.current-qty", String.valueOf(current_qty));
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		//
		return this.getServiceContext().doNextProcess();
	}
}
