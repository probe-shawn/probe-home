package xlive.method.rst.finance;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Element;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

import xlive.google.xMemCache;
import xlive.method.*;
import xlive.method.img.barcode.*;
import xlive.method.rst.finance.xBillDetail;
import xlive.method.rst.mall.xMallDetail;
import xlive.google.ds.xLong;

public class xSetBillMethod extends xDefaultMethod{
		String fee_amount=null;
		String fee_fid=null;
		String fee_link=null;
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String bid="";
		String keystr="";
		String fid="";
		String link="";
		String type="";
		String amount="";
		String qty="";
		String remark="";
		String writeoff="";
		String doneflag="";	
		if(this.fee_fid==null){
			 fid=this.getArguments("fid");
			 link=this.getArguments("link");   //服務費相關連結用
			 type=this.getArguments("type");   //1.紅利2.服務費....		
			 amount=this.getArguments("amount");
			 keystr=this.getArguments("key");
			 qty=this.getArguments("qty");
			 remark=this.getArguments("remark");
			 writeoff=this.getArguments("writeoff");
			 doneflag=this.getArguments("doneflag");
		}else{
			 fid=this.fee_fid;
			 link=this.fee_link;   //服務費相關連結用
			 type="2";   //1.紅利2.服務費....		
			 amount=this.fee_amount;
		}
		
		String atmcode="";
		String barcode1="";
		String barcode2="";
		String barcode3="";		
		
		
		if(type.equals("1")){
			int a=Integer.parseInt(qty)+Integer.parseInt(qty.substring(0, qty.length()-1));
			a= (int) (a*(1.05));//加上營業稅，無條件捨去
			amount=String.valueOf(a);			
		}else{
			int a=(amount.length()>0)?Integer.parseInt(amount):0;
			a= (int) (a*(1.05));//加上營業稅，無條件捨去
			amount=String.valueOf(a);
		}
		Entity entity =null;
		xBillDetail bill_detail=null;
		
		Element bill_element =null;
		if(this.fee_fid==null)
			bill_element = this.setReturnArguments("bill", "");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		if(keystr != null && keystr.trim().length()>0){
			try{
				entity = ds.get(KeyFactory.stringToKey(keystr));
			}catch(EntityNotFoundException e){entity=null;}
		}
		
		if(entity == null){
			xLong xl=new xLong("BILL95720");
		    bid=String.valueOf(xl.increase()+10000000);
			entity = new Entity(xBillDetail.generateKey(bid));
			bill_detail = new xBillDetail(entity);
			bill_detail.setCreateDate(new Date());
			bill_detail.setBid(bid);
			bill_detail.setFid(fid);
			bill_detail.setLink(link);
			bill_detail.setType(type); 
			bill_detail.setQTY(qty);			
			bill_detail.setAmount(amount);
			bill_detail.setRemark(remark);
			atmcode=xBarcodeDataUtil.getATMcode("95720", bid, amount);
			bill_detail.setATMcode(atmcode);
			barcode1="1111116NP";
			
			bill_detail.setBarcode1(barcode1);
			barcode2="JLIV0000"+bid;
			
			bill_detail.setBarcode2(barcode2);
			String s=bill_detail.getCreateDateString().substring(2,4)+bill_detail.getCreateDateString().substring(5,7);
			
			
			String a=String.valueOf(bill_detail.getAmount()+1000000000).substring(1,10);
			
			String c=xBarcodeDataUtil.getBarcodeCheckcode(barcode1,barcode2, s+"**"+a);
			barcode3=s+c+a;
			bill_detail.setBarcode3(barcode3);
			bill_detail.setWriteOff(0);
			bill_detail.setDoneFlag("0");
			bill_detail.setLast("");
		}else{
			bill_detail = new xBillDetail(entity);
			bill_detail.setLink(link);
			bill_detail.setRemark(remark);
			bill_detail.setWriteOff(writeoff);
			bill_detail.setDoneFlag(doneflag);
			bill_detail.setLast(new Date());
		}
		
		if(type.equals("2"))
			return bill_detail.entity;
		
		ds.put(bill_detail.entity);
	
        bill_element.appendChild(this.createElement("bid")).setTextContent(bid);
        bill_element.appendChild(this.createElement("amount")).setTextContent(amount);
        bill_element.appendChild(this.createElement("atmcode")).setTextContent(atmcode);
        bill_element.appendChild(this.createElement("barcode1")).setTextContent(barcode1);
        bill_element.appendChild(this.createElement("barcode2")).setTextContent(barcode2);
        bill_element.appendChild(this.createElement("barcode3")).setTextContent(barcode3);
		
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		
		return this.getServiceContext().doNextProcess();
	}
	
	//for service fee用
	public Entity getBillEntity(String fid,String link,String amount)throws xMethodException{
		this.fee_fid=fid;
		this.fee_amount=amount;
		this.fee_link=link;
		return (Entity)this.process();
	}
}
