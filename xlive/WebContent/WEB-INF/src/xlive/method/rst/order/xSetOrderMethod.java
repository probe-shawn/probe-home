package xlive.method.rst.order;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import xlive.google.ds.xLong;
import xlive.google.xMemCache;
import xlive.method.*;
import xlive.method.rst.bonus.xBonus;
import xlive.method.rst.customer.xCustomer;
import xlive.method.rst.mall.xMall;
import xlive.method.rst.menu.xBom;
import xlive.method.rst.menu.xBomBuy;
import xlive.method.rst.menu.xBomLimit;
import xlive.method.rst.statistic.xGetMethod;

//import com.google.appengine.repackaged.org.json.*;
import org.json.*;

public class xSetOrderMethod extends xDefaultMethod{
	
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		
		String keystr=this.getArguments("key");
		String cust_fid=this.getArguments("customer-fid");
		String mall_fid=this.getArguments("mall-fid");
		Key mall_key=xMall.generateKey(mall_fid);
		int processed=0;
		try{
			processed = Integer.parseInt(this.getArguments("processed"));
		}catch(Exception e){processed=0;}
		
		boolean key_existed = (keystr != null && keystr.trim().length()>0);
		boolean update_normal = (key_existed && processed==0);
		boolean update_commit = (key_existed && processed > 0);
		boolean update_cancel = (key_existed && processed < 0);
		boolean create_new = (!key_existed);
		boolean mail_flag=false;
		xOrder order = null;
		Date current = new Date();
		ArrayList<Entity> put_array = new ArrayList<Entity>();
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		///////////////////////////////
		if(update_normal){
			try{
				Entity entity = ds.get(KeyFactory.stringToKey(keystr));
				order = new xOrder(entity);				
				order.setOperator(this.getArguments("operator"));
				order.setProcessed(this.getArguments("processed"));
				if(order.getAccept()==0 && "1".equals(this.getArguments("accept"))) mail_flag=true;
				order.setAccept(this.getArguments("accept"));
				order.setNote(this.getArguments("note"));
				/*
				if(order.getStatus()==null || order.getStatus().length()==0)
					if(this.getArguments("status")!=null && this.getArguments("status").length()>0)
						mail_flag=true;		
				*/				
				order.setStatus(this.getArguments("status"));
				order.setLast(current);
				ds.put(entity);
			}catch(EntityNotFoundException e){
				valid = false;
				why = "order not found";
			}
		}
		///////////////////////////////////////////////
		if(update_commit){
			Entity order_entity =null;
			order = null;
			try{
				order_entity = ds.get(KeyFactory.stringToKey(keystr));
				order = new xOrder(order_entity);
				order.setOperator(this.getArguments("operator"));
				order.setProcessed(this.getArguments("processed"));
				order.setNote(this.getArguments("note"));
				/*
				if(order.getStatus()==null || order.getStatus().length()==0)
						mail_flag=true;	
				*/
				mail_flag=true;
				order.setStatus(this.getArguments("status"));
				order.setLast(current);
				put_array.add(order_entity);
			}catch(EntityNotFoundException e){
				valid = false;
				why = "order not found";
			}
			if(valid && order.getBonusC2M() > 0){ //use bonus
				Entity c2m_entity = new Entity(xBonus.class.getSimpleName(),mall_key);
				xBonus bonus = new xBonus(c2m_entity);
				bonus.setFromId(order.getId());
				bonus.setFromType(xOrder.bonusType);
				bonus.setBonus(order.getBonusC2M());
				bonus.setToId(mall_fid);
				bonus.setToType(xMall.bonusType);
				bonus.setDate(current);
				put_array.add(c2m_entity);
			}
			if(valid && order.getBonusM2C() > 0){ //offer bonus
				Entity m2c_entity = new Entity(xBonus.class.getSimpleName(),mall_key);
				xBonus bonus = new xBonus(m2c_entity);
				bonus.setFromId(order.getId());
				bonus.setFromType(xOrder.bonusType);
				bonus.setBonus(order.getBonusM2C());
				bonus.setToId(cust_fid);
				bonus.setToType(xCustomer.bonusType);
				bonus.setDate(current);
				put_array.add(m2c_entity);
			}
			int retries = 3;
	    	while(valid) {
	    	    Transaction txn = ds.beginTransaction();
	    	    try {
	    	    	Entity mall_entity=null;
	    	    	if(order.getBonusC2M() > 0){
		    	    	try{
		    	    		mall_entity = ds.get(mall_key);
		    	    		xMall mall = new xMall(mall_entity);
		    	    		mall.setBonusRefund(mall.getBonusRefund()+order.getBonusC2M());
		    	    		put_array.add(mall_entity);
		    	    	}catch(EntityNotFoundException e){
		    	    		valid = false;
		    	    		why = "mall not found";
		    	    	}
	    	    	}
	    	    	if(valid){
	    	    		ds.put(put_array);
	    	    		txn.commit();
	    	    	}
	    	        break;
	    	    } catch (java.util.ConcurrentModificationException  e) {
	    	        if(retries == 0) throw e;
	    	        --retries;
	    	    } finally {
	    	        if(txn.isActive())txn.rollback();
	    	    }
	    	} 
		}
		///////////////////////////////////////////////
		if(update_cancel){
			Entity order_entity =null;
			order = null;
			try{
				order_entity = ds.get(KeyFactory.stringToKey(keystr));
				order = new xOrder(order_entity);
				order.setOperator(this.getArguments("operator"));
				order.setProcessed(this.getArguments("processed"));
				order.setNote(this.getArguments("note"));
				order.setStatus(this.getArguments("status"));
				order.setLast(current);
				put_array.add(order_entity);
			}catch(EntityNotFoundException e){
				valid = false;
				why = "order not found";
			}
			if(valid && order.getBonusM2C() > 0){ //cancel
				Entity m2c_entity = new Entity(xBonus.class.getSimpleName(),mall_key);
				xBonus bonus = new xBonus(m2c_entity);
				bonus.setFromId(mall_fid);
				bonus.setFromType(xMall.bonusType);
				bonus.setBonus(-1*order.getBonusM2C());
				bonus.setToId(order.getId());
				bonus.setToType(xOrder.bonusType);
				bonus.setDate(current);
				put_array.add(m2c_entity);
			}
			if(valid && order.getBonusC2M() > 0){ //cancel
				Entity c2m_entity = new Entity(xBonus.class.getSimpleName(),mall_key);
				xBonus bonus = new xBonus(c2m_entity);
				bonus.setFromId(cust_fid);
				bonus.setFromType(xCustomer.bonusType);
				bonus.setBonus(-1*order.getBonusC2M());
				bonus.setToId(order.getId());
				bonus.setToType(xOrder.bonusType);
				bonus.setDate(current);
				put_array.add(c2m_entity);
			}
			int retries = 3;
	    	while(valid) {
	    		TransactionOptions xgoptions = TransactionOptions.Builder.withXG(true);
	    	    Transaction txn = ds.beginTransaction(xgoptions);
	    	    try {
	    	    	List<String> limit_goods = order.getLimitGoodsQty();
	    	    	if(limit_goods != null){
	    	    		for(String found : limit_goods){
	    	    			String[] gcs=found.split(";");
	    	    			try {
								Entity bomlimit_entity=ds.get(KeyFactory.createKey(xBomLimit.class.getSimpleName(), gcs[0]));
								long qty=Long.valueOf(gcs[1]);
								xBom bom = new xBom(bomlimit_entity);
								qty += bom.getQty();
								if(qty < 0) qty = 0;
								bom.setQty(qty);
								put_array.add(bomlimit_entity);
							}catch (EntityNotFoundException e) {
							}
	    	    		}
	    	    	}
	    	    	Entity mall_entity=null;
	    	    	if(valid && order.getBonusM2C() > 0){
		    	    	try{
		    	    		mall_entity = ds.get(mall_key);
		    	    		xMall mall = new xMall(mall_entity);
		    	    		mall.setBonus(mall.getBonus()+order.getBonusM2C());
		    	    		put_array.add(mall_entity);
		    	    	}catch(EntityNotFoundException e){
		    	    		valid = false;
		    	    		why = "mall not found";
		    	    	}
	    	    	}
	    	    	if(valid){
	    	    		ds.put(put_array);
	    	    		txn.commit();
	    	    	}
	    	        break;
	    	    } catch (java.util.ConcurrentModificationException  e) {
	    	        if(retries == 0) throw e;
	    	        --retries;
	    	    } finally {
	    	        if(txn.isActive())txn.rollback();
	    	    }
	    	} 
		}

		///////////////////////////////////////////// 
		JSONObject json = null;
		if(create_new){
			order = this.createOrderEntity(mall_fid, put_array, current,this.getArguments("tz-offset"));
			if(order.getBonusM2C() > 0){
				Entity m2c_entity = new Entity(xBonus.class.getSimpleName(),mall_key);
				xBonus bonus = new xBonus(m2c_entity);
				bonus.setFromId(order.getMallFid());
				bonus.setFromType(xMall.bonusType);
				bonus.setBonus(order.getBonusM2C());
				bonus.setToId(order.getId());
				bonus.setToType(xOrder.bonusType);
				bonus.setDate(current);
				put_array.add(m2c_entity);
			}
			if(order.getBonusC2M() != 0){
				Entity c2m_entity = new Entity(xBonus.class.getSimpleName(),mall_key);
				xBonus bonus = new xBonus(c2m_entity);
				bonus.setFromId(cust_fid);
				bonus.setFromType(xCustomer.bonusType);
				bonus.setBonus(order.getBonusC2M());
				bonus.setToId(order.getId());
				bonus.setToType(xOrder.bonusType);
				bonus.setDate(current);
				put_array.add(c2m_entity);
			}
			//
			int retries = 3;
	    	while(valid) {
	    		TransactionOptions xgoptions = TransactionOptions.Builder.withXG(true);
	    	    Transaction txn = ds.beginTransaction(xgoptions);
	    	    try {
	    	    	JSONArray list= new JSONArray();
	    	    	List<String> limit_goods = order.getLimitGoodsQty();
	    	    	if(limit_goods != null && limit_goods.size() >0){
	    	    		for(int v=0; v < limit_goods.size() && valid;++v){
	    	    			String[]lgs = limit_goods.get(v).split(";");
	    	    			try {
								Entity bomlimit_entity = ds.get(KeyFactory.createKey(xBomLimit.class.getSimpleName(), lgs[0]));
								long qty = Long.valueOf(lgs[1]);
								xBom bom = new xBom(bomlimit_entity);
								long sub = bom.getQty()-qty;
								if(sub >= 0){
									bom.setQty(sub);
									put_array.add(bomlimit_entity);
									try {
										JSONObject jso = new JSONObject();
										jso.put("key",lgs[0]);
										jso.put("qty",sub);
										list.put(jso);
									} catch (JSONException e){}
								}else{
									valid = false;
									why = "很抱歉!\n限量商品 : "+bom.getName()+" 剩餘數量 : "+bom.getQty()+"\n無法符合您的採購量 : "+qty+".\n請選購其他商品"+((bom.getQty() >0)?",或降低採購量.":".");
								}
							} catch (EntityNotFoundException e) {
								valid = false;
								why = "product not found";
							}
	    	    		}
	    	    	}
	    	    	try {
	    	    		json = new JSONObject();
						json.put("update_bom_qty", list);
					} catch (JSONException e1) {
					}
	    	    	Entity mall_entity=null;
	    	    	if(valid && order.getBonusM2C() > 0){
		    	    	try{
		    	    		mall_entity = ds.get(mall_key);
		    	    		xMall mall = new xMall(mall_entity);
		    	    		long off_bonus = mall.getBonus()-order.getBonusM2C();
							if(off_bonus < 0) {
								valid = false;
								why = "很抱歉!\n本市集 Mall, 可贈送的紅利點數已用馨 , 暫時無法分派紅利.\n請選購未贈紅利的商品.";
							}else{
								mall.setBonus(off_bonus);
								put_array.add(mall_entity);
							}
		    	    	}catch(EntityNotFoundException e){
		    	    		valid = false;
		    	    		why = "mall not found";
		    	    	}
	    	    	}
	    	    	if(valid){
	    	    		ds.put(put_array);
	    	    		txn.commit();
	    	    		xMemCache.iMallService().increment(xGetMethod.orderCount, Long.valueOf(1));
	    	    	}
	    	        break;
	    	    } catch (java.util.ConcurrentModificationException  e) {
	    	        if(retries == 0) throw e;
	    	        --retries;
	    	    } finally {
	    	        if(txn.isActive())txn.rollback();
	    	    }
	    	} 
	    	//
		}
		if(valid){
			this.setReturnArguments("order.key",KeyFactory.keyToString(order.entity.getKey()));
			this.setReturnArguments("order.id", order.getId());
			this.setReturnArguments("order.mall-fid", order.getMallFid());
			this.setReturnArguments("order.customer-fid", order.getCustomerFid());
			this.setReturnArguments("order.total", String.valueOf(order.getTotal()));
			this.setReturnArguments("order.bonus-m2c", String.valueOf(order.getBonusM2C()));
			this.setReturnArguments("order.bonus-c2m", String.valueOf(order.getBonusC2M()));
			this.setReturnArguments("order.note", ""+order.getNote());
			if(json != null) this.setReturnArguments("order.update-qty", json.toString());
			if(!update_normal && (order.getBonusC2M() != 0 || order.getBonusM2C() !=0)){
				Queue queue = QueueFactory.getDefaultQueue();
				TaskOptions to = TaskOptions.Builder.withUrl("/web/rst/statistic");
				to.param("method", "log-customer-bonus");
				to.param("fid", cust_fid);
				queue.add(to);
			}
			if(create_new){
				Queue queue = QueueFactory.getDefaultQueue();
				TaskOptions to = TaskOptions.Builder.withUrl("/web/rst/statistic");
				to.param("method", "log-order-goods");
				to.param("mall-fid", order.getMallFid());
				to.param("order-id", order.getId());
				queue.add(to);
				TaskOptions to2 = TaskOptions.Builder.withUrl("/web/rst/order");
				to2.param("method", "mail-order");
				to2.param("type", "1");
				to2.param("mall-fid", order.getMallFid());
				to2.param("order-id", order.getId());
				queue.add(to2);
			}
			if(update_cancel){
				Queue queue = QueueFactory.getDefaultQueue();			
				TaskOptions to = TaskOptions.Builder.withUrl("/web/rst/order");
				to.param("method", "mail-order");
				if(order.getAccept()==1)
					to.param("type", "-1");
				else
					to.param("type", "0");
				to.param("mall-fid", order.getMallFid());
				to.param("order-id", order.getId());
				queue.add(to);
			}
			if(mail_flag && (update_normal || update_commit)){
				Queue queue = QueueFactory.getDefaultQueue();			
				TaskOptions to = TaskOptions.Builder.withUrl("/web/rst/order");
				to.param("method", "mail-order");
				if(update_commit)
					to.param("type", "3");
				else
					to.param("type", "2");
				to.param("mall-fid", order.getMallFid());
				to.param("order-id", order.getId());
				to.param("remark", order.getStatus());
				queue.add(to);
			}
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		//
		return this.getServiceContext().doNextProcess();
	}
	
	private xOrder createOrderEntity(String mall_fid, ArrayList<Entity> put_array, Date current,String tz_offset) throws xMethodException {
		long tzoffset=-480;
		try{
			tzoffset=Long.valueOf(tz_offset);
		}catch(Exception e){}
		Date date=new Date(System.currentTimeMillis()-tzoffset*60*1000); 
		String id=new SimpleDateFormat("yyMMdd").format(date);
		String idno="0000"+String.valueOf(new xLong(mall_fid+"/"+id).increase());
		id = id+idno.substring(idno.length()-4);
		Key order_key = xOrder.generateKey(mall_fid,id);
		Entity entity = new Entity(order_key);
		put_array.add(entity);
		xOrder order = new xOrder(entity);
		
		order.setId(id);
		order.setDate(current);
		order.setMallFid(mall_fid);
		order.setName(this.getArguments("name"));
		order.setPhone(this.getArguments("phone"));
		order.setAddr(this.getArguments("addr"));
		order.setMail(this.getArguments("mail"));
		order.setDeliver(this.getArguments("deliver"));
		order.setDate2(this.getArguments("date2"));
		order.setTime2(this.getArguments("time2"));
		order.setTotal(this.getArguments("total"));
		order.setDiscount(this.getArguments("discount"));
		order.setPayment(this.getArguments("payment"));
		order.setOperator(this.getArguments("operator"));
		order.setProcessed(this.getArguments("processed"));
		order.setNote(this.getArguments("note"));
		order.setStatus(this.getArguments("status"));
		order.setBounsM2C(this.getArguments("bonus-m2c"));
		order.setBounsC2M(this.getArguments("bonus-c2m"));
		order.setCustomerFid(this.getArguments("customer-fid"));
		order.setFareDelivery(this.getArguments("fare-delivery"));
		order.setSummary(this.getArguments("summary"));
		order.setAccept(this.getArguments("accept"));
		order.setLast(new Date());
		//create from products
		long count=0;
		Element products=(Element)this.getArguments("products", XPathConstants.NODE);
		if(products != null){
			NodeList node_list=products.getElementsByTagName("bom");
			for(int i = 0; i < node_list.getLength();++i){
				xGoods goods=this.createGoodsFromBom(order, (Element)node_list.item(i), order_key,current);
				put_array.add(goods.entity);
				count += goods.getQty();
				//
				Element buy=(Element)(((Element)node_list.item(i)).getElementsByTagName("limit-buy").item(0));
				if(buy !=null && "1".equals(buy.getTextContent())){
					xBomBuy bombuy = new xBomBuy(new Entity(xBomBuy.class.getSimpleName(),goods.getBomKey()+order.getCustomerFid()));
					bombuy.setQty(goods.getQty());
					bombuy.setDate(current);
					put_array.add(bombuy.entity);
				}
				
			}
		}
		//
		order.setGoods(count);
		return order;
	}
	public xGoods createGoodsFromBom(xOrder order, Element bom, Key order_key,Date current){
		Entity entity = new Entity(xGoods.class.getSimpleName(),order_key);
		xGoods goods = new xGoods(entity);
		Node node;
		node=bom.getElementsByTagName("id").item(0);
		if(node !=null)goods.setId(node.getTextContent());
		
		node=bom.getElementsByTagName("name").item(0);
		if(node !=null)goods.setName(node.getTextContent());
		
		node=bom.getElementsByTagName("barcode").item(0);
		if(node !=null)goods.setBarcode(node.getTextContent());
		
		node=bom.getElementsByTagName("price").item(0);
		if(node !=null)goods.setPrice(node.getTextContent());
		
		node=bom.getElementsByTagName("icon").item(0);
		if(node !=null)goods.setIcon(node.getTextContent());
		
		node=bom.getElementsByTagName("option-xml").item(0);
		if(node !=null)goods.setOptionXml(node.getTextContent().getBytes());
		
		node=bom.getElementsByTagName("addon-xml").item(0);
		if(node !=null)goods.setAddonXml(node.getTextContent().getBytes());
		
		//node=bom.getElementsByTagName("options").item(0);
		node=bom.getElementsByTagName("ostr").item(0);
		if(node !=null)goods.setOptions(node.getTextContent());
		
		//node=bom.getElementsByTagName("addons").item(0);
		node=bom.getElementsByTagName("astr").item(0);
		if(node !=null)goods.setAddons(node.getTextContent());
		
		node=bom.getElementsByTagName("qty").item(0);
		if(node !=null)goods.setQty(node.getTextContent());
		
		node=bom.getElementsByTagName("optval").item(0);
		if(node !=null)goods.setOptval(node.getTextContent());
		
		node=bom.getElementsByTagName("bonus").item(0);
		if(node !=null)goods.setBonus(node.getTextContent());
		
		node=bom.getElementsByTagName("key").item(0);
		if(node !=null)goods.setBomKey(node.getTextContent());
		
		goods.setDate(current);
		
		node=bom.getElementsByTagName("limit-qty").item(0);
		if(node !=null){
			String limit_qty=node.getTextContent();
			if("1".equals(limit_qty)){
				order.addLimitGoodsQty(goods.getBomKey(),goods.getQty());
			}
		}
		
		return goods;
	}
}
