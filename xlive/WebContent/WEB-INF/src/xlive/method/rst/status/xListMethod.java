package xlive.method.rst.status;


import xlive.xWebInformation;
import xlive.google.ds.xHash;
import xlive.method.*;
import xlive.xml.xXmlDocument;

import org.w3c.dom.*;

public class xListMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		Element data= this.setReturnArguments("data", "");
		String mall_fid=this.getArguments("mall-fid");
		Element statusxml,pxml,inode;
		byte[] bytexml = new xHash("order_status").get(mall_fid);
		if(bytexml == null) bytexml = new xHash().get("order_status");
		
		if(bytexml != null) {
			statusxml =(Element) data.getOwnerDocument().adoptNode(xXmlDocument.bytesToNode(bytexml));
			data.appendChild(statusxml);
		}else {
	    	data.appendChild(pxml=xWebInformation.createElement("pxml"));
	    	pxml.setAttribute("pxml", "pxml");
	    	pxml.setAttribute("name", "���A�]�w");
	    	//
	    	pxml.appendChild(statusxml = xWebInformation.createElement("status"));
	    	statusxml.setAttribute("pxml", "g");
	    	statusxml.setAttribute("addOption", "false");
	    	statusxml.setAttribute("writable", "true");
	    	statusxml.setAttribute("showValue","false");
	    	statusxml.appendChild(inode = xWebInformation.createElement("i"));
	    	inode.setAttribute("pxml","select");
	    	inode.setAttribute("name","�q�檬�A");
	    	inode.setAttribute("multiple","true");
	    	inode.setAttribute("prolog","");
	    	inode.setAttribute("epilog","");
	    	inode.setAttribute("showValue","true");
	    	inode.setAttribute("options","�T�{,�Ʈ�,�s�@,�ưe,�b�~,����");
	    	//inode.setAttribute("values","�T�{,�Ʈ�,�s�@,�ưe,�b�~,����");
	    	inode.setAttribute("removable","false");
	    	inode.setAttribute("nameReadOnly","true");
	    }
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
