package xlive.method.img;

import xlive.google.xMemCache;
import xlive.method.*;
import xlive.method.img.qrcode.google.PixelSource;
import xlive.method.img.qrcode.google.PngWriter;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Blob;


public class xGradientMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		int width=1,height=1;
		try{
			width=Integer.parseInt(getArguments("w"));
		}catch(Exception e){}
		try{
			height=Integer.parseInt(getArguments("h"));
		}catch(Exception e){}
		int color=0xffffff;
		try{
			color=Integer.parseInt(getArguments("c"), 16);
		}catch(Exception e){}
		float fo=1f,to=1f;
		try{
			fo=Float.parseFloat(getArguments("fo"));
		}catch(Exception e){}
		try{
			to=Float.parseFloat(getArguments("to"));
		}catch(Exception e){}
		int sect=1;
		try{
			sect=Integer.parseInt(getArguments("s"));
		}catch(Exception e){}
		//
		byte[] pngbytes=null;
		StringBuffer memkey=new StringBuffer();
		memkey.append(width).append(height).append(color).append(fo).append(to).append(sect);
		Blob blob = (Blob)xMemCache.pngService().get(memkey);
		if(blob != null) pngbytes=blob.getBytes();
		if(pngbytes==null||pngbytes.length==0){
			pngPixelSource ps = new pngPixelSource(width,height,color,fo,to,sect);
			try {
				pngbytes = new PngWriter().generateImage(ps);
				xMemCache.pngService().put(memkey,new Blob(pngbytes));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//
        HttpServletResponse response = getServiceContext().customizeResponse();
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("image/png");
		try{
			response.getOutputStream().write(pngbytes);
		}catch(IOException ioe){
			ioe.printStackTrace();
			throw createMethodException("QrcodeException", ioe.getLocalizedMessage());
		}
		//
		
        return getServiceContext().doNextProcess();
	}
	static class pngPixelSource implements PixelSource{
		private int width;
		private int height;
		private int xalphaColor;
		//private int transparency=0;
		private int fromAlpha=255;
		private int toAlpha=255;
		private int offAlpha=0;
		private int section=1;
		private int sectLength=1;
		
		private boolean vertical=true;
		public pngPixelSource(int width,int height,int color,float fo,float to,int sect){
			this.width=width;
			this.height=height;
			this.section=sect;
			
			this.fromAlpha=Math.round((255*fo));
			this.toAlpha=Math.round((255*to));
			this.offAlpha=this.toAlpha-this.fromAlpha;
			
			this.vertical=(height > width);
			this.xalphaColor=color;
			this.sectLength=(vertical)?Math.round(this.height/sect+0.5f):Math.round(this.width/sect+0.5f);
		}
		public int getWidth(){
			return this.width;
		}
		public int getHeight(){
			return this.height;
		}
		public int setAlpha(int color, int alpha255){
			return (color | (alpha255 << 24));
		}
		public int getPixel(int x, int y){
			if(section > 1){
				if(vertical){
					return ((y/sectLength)%2==0) ?
							this.setAlpha(xalphaColor, this.fromAlpha+Math.round((this.offAlpha*(y % sectLength)*100f/sectLength)/100)):
							this.setAlpha(xalphaColor, this.fromAlpha+Math.round((this.offAlpha*(sectLength-(y % sectLength))*100f/sectLength)/100));
				}else{
					return ((x/sectLength)%2==0) ?
							this.setAlpha(xalphaColor, this.fromAlpha+Math.round((this.offAlpha*(x % sectLength)*100f/sectLength)/100)):
							this.setAlpha(xalphaColor, this.fromAlpha+Math.round((this.offAlpha*(sectLength-(x % sectLength))*100f/sectLength)/100));
				}
			}else{
				return (vertical)?this.setAlpha(xalphaColor, this.fromAlpha+Math.round(this.offAlpha*(y*100f/this.height)/100f)):
					              this.setAlpha(xalphaColor,  this.fromAlpha+Math.round(this.offAlpha*(x*100f/this.width)/100f));
			}
		}
	}

}
