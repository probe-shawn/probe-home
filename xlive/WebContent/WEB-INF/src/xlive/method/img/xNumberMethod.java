package xlive.method.img;
import xlive.xResourceManager;
import xlive.method.*;
import xlive.method.img.qrcode.google.PngWriter;
import xlive.method.img.xPngMethod.pngPixelSource;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.images.*;
public class xNumberMethod extends xDefaultMethod{
	public Object process() throws xMethodException{		
		String num=getArguments("num");
		try{
			int n=Integer.parseInt(num);
		}catch(Exception e){num="0";}	
		List<Composite> clist=new ArrayList<Composite>();
		ImagesService imagesService = ImagesServiceFactory.getImagesService();		
		BufferedInputStream input=new BufferedInputStream(xResourceManager.getResourceAsStream("/images/number.png"));
		ByteArrayOutputStream output=new ByteArrayOutputStream();
		xlive.xUtility.copyStream(input, output);
		int off_x=0;
		if(num.length()==1) off_x=23;
		if(num.length()==2) off_x=15;
		if(num.length()==3) off_x=7;		
		for(int i=0;i<num.length();i++){			
			int n=Integer.parseInt(num.substring(i,i+1));
			Image png= ImagesServiceFactory.makeImage(output.toByteArray());
			Composite cp=ImagesServiceFactory.makeComposite(cropImage(n,png,imagesService),off_x+i*15, 0, 1f, Composite.Anchor.TOP_LEFT);
			clist.add(cp);			
		}	
		Image newImage = imagesService.composite(clist, (num.length()<4)?60:(num.length()*15),20,Long.parseLong("ffffffff", 16),ImagesService.OutputEncoding.PNG);
		
        Transform resize = ImagesServiceFactory.makeResize(60, 20);
        newImage = imagesService.applyTransform(resize, newImage); 
        byte[] newImageData = newImage.getImageData();		
        HttpServletResponse response = getServiceContext().customizeResponse();
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("image/png");
		try{
			response.getOutputStream().write(newImageData);
		}catch(IOException ioe){
			ioe.printStackTrace();
			throw createMethodException("QrcodeException", ioe.getLocalizedMessage());
		}
        return getServiceContext().doNextProcess();
	}	
	public Image cropImage(int n,Image png,ImagesService is) {	
		Transform crop = ImagesServiceFactory.makeCrop(0f, (0.1f)*n, 1f, 0.1f+(0.1f)*n);
		Image newImage =is.applyTransform(crop, png);
		return newImage;
	}
	
}
