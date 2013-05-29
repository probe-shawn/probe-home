package xlive.method.sys;
import org.w3c.dom.Element;
import xlive.xWebInformation;
import xlive.google.ds.xFile;
import xlive.method.*;

public class xDumpContextMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		boolean valid=true;
		String why="";
		Element data=this.setReturnArguments("data","");
		System.gc();
		StringBuffer buf = new StringBuffer();
		long free = Runtime.getRuntime().freeMemory();
		long total = Runtime.getRuntime().totalMemory();
		long max = Runtime.getRuntime().maxMemory();
		float f1 = (float) ((total-free)/100000l) / 10;
		float f2 = (float) (total/100000l) / 10;
		float f3 = (float) (max/100000l) / 10;
		buf.append(f1).append("MB used, ").append(f2).append("MB allocated, ").append(f3).append("MB max");
		data.appendChild(this.createElement("memory", buf.toString()));
		//Node node=xWebInformation.getContextSessionNode(this.getServiceContext().getSessionId());
		//node=node.getParentNode().cloneNode(true);
		//data.setAttribute("count", String.valueOf(node.getChildNodes().getLength()));
		//data.appendChild(node);
		setReturnArguments("session-id", this.getServiceContext().getSessionId());
		setReturnArguments("valid", String.valueOf(valid));
		setReturnArguments("why", why);
		
		//setReturnArguments("test", this.test());
		
		return getServiceContext().doNextProcess();
	}
	private String test(){
		xFile file;
		file = new xFile("/");
		System.out.println("name :"+ file.getFileName()+" exist :"+file.exists());
		
		
		//BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

		//String uploadURL = blobstoreService.createUploadUrl("/upload");
		//System.out.println("uploadURL :"+ uploadURL);
		
		//System.out.println("bytes :"+ new String(file.getBytes()));
		/*
		file = new xFile("/a1/a3/abcd.text");
		file.make();
		
		file = new xFile("/a1/abcd.text");
		file.setBytes("TEST".getBytes());
		
		file = new xFile("/a1/efgh.text");
		file.setBytes("TEST".getBytes());
		
		file = new xFile("/a1/1234.text");
		file.setBytes("TEST".getBytes());
		
		file = new xFile("/a2/1234.text");
		file.setBytes("TEST".getBytes());
		*/
		//xFile root = new xFile("/");
		//System.out.println("root :"+root.getName());
		
		//xFile file = new xFile("/a1/a6");
		//System.out.println("exists :"+file.exists()+" name :"+file.getFileName()+" bytes:"+((file.getBytes()==null)? "NULL" :new String(file.getBytes())));
		
		
		//file.setBytes("Good morning 4".getBytes());
		//System.out.println("exists :"+file.exists()+" name :"+file.getFileName()+" bytes:"+((file.getBytes()==null)? "NULL" :new String(file.getBytes())));

		
		//file = new xFile("/a1/a6");
		//System.out.println("exists :"+file.exists()+" name :"+file.getFileName()+" bytes:"+((file.getBytes()==null)? "NULL" :new String(file.getBytes())));

		//xFile file=new xFile("/a1/a7/a8/a9");
		//file.setBytes("HI 4".getBytes());
		//System.out.println("exists :"+file.exists()+" name :"+file.getName()+" bytes:"+((file.getBytes()==null)? "NULL" :new String(file.getBytes())));

		//file.delete();
		
		//file = new xFile("/a1/a7/a8/a9");
		
		//System.out.println("exists :"+file.exists()+" name :"+file.getName()+" bytes:"+((file.getBytes()==null)? "NULL" :new String(file.getBytes())));
		/*
		file = new xFile("/a1");
		if(file.exists()){
			System.out.println("exists :"+file.getName()+" bytes:"+((file.getBytes()==null)? "NULL" :new String(file.getBytes())));
		}
		file = new xFile("/");
		if(file.exists()){
			System.out.println("exists :"+file.getName()+" bytes:"+((file.getBytes()==null)? "NULL" :new String(file.getBytes())));
		}

		
		file.setBytes("hello".getBytes());
		byte[] bytes=file.getBytes();
		if(bytes == null) bytes="not found error".getBytes();
		System.out.println("test1 :"+ new String(bytes)+"   name :"+file.getName()+"  length :"+file.getFileObject().getLength());

		
		xFile file2 = new xFile("/a1/a6");
		bytes=file2.getBytes();
		if(bytes == null) bytes="not found error".getBytes();
		System.out.println("test :"+ new String(bytes)+"   name :"+file2.getName()+"  length :"+file2.getFileObject().getLength());
		*/
		return "OK";	
	}
}
