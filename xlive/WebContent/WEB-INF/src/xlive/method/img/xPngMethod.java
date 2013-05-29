package xlive.method.img;

import xlive.google.xMemCache;
import xlive.method.*;
import xlive.method.img.qrcode.google.PixelSource;
import xlive.method.img.qrcode.google.PngWriter;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Blob;

public class xPngMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		int width=2,height=2;
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
		int border_color=color;
		try{
			border_color=Integer.parseInt(getArguments("bc"), 16);
		}catch(Exception e){}
		float opaque=1f;
		try{
			opaque=Float.parseFloat(getArguments("o"));
		}catch(Exception e){}
		int rc=-1;
		try{
			rc=Integer.parseInt(getArguments("rc"));
		}catch(Exception e){}
		
		byte[] pngbytes=null;
		StringBuffer memkey=new StringBuffer();
		memkey.append(width).append(height).append(color).append(border_color).append(rc).append(opaque);
		Blob blob = (Blob)xMemCache.pngService().get(memkey);
		if(blob != null) pngbytes=blob.getBytes();
		if(pngbytes==null||pngbytes.length==0){
			pngPixelSource ps = new pngPixelSource(width,height,color,border_color,rc,opaque);
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
		private int color;
		private int borderColor;
		private int round;
		private float opague=1;
		private int alpha=255;
		private int transparency=0;
		public pngPixelSource(int width,int height, int color,int border_color,int round,float opague){
			this.width=width;
			this.height=height;
			this.opague=opague;
			this.alpha=Math.round((255*this.opague));
			this.color=this.setAlpha(color, this.alpha);
			this.borderColor=this.setAlpha(border_color, this.alpha);
			this.round=round;
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
			if(round >=0){
				if(y==0) return ((x < 5)|| x > this.width-6)?this.transparency:((x == 5)|| x == this.width-6)? this.borderColor:this.borderColor;
				if(y==1) return ((x < 3)|| x > this.width-4)?this.transparency:(x==3||x==4||x==this.width-5||x==this.width-4)?this.borderColor:this.color;
				if(y==2) return ((x < 2)|| x > this.width-3)?this.transparency:(x==2||x==this.width-3)?this.borderColor:this.color;
				if(y==3||y==4) return ((x < 1)|| x > this.width-2)?this.transparency:(x==1||x==this.width-2)?this.borderColor:this.color;
			
				if(y==this.height-1) return ((x < 5)|| x > this.width-6)?this.transparency:((x == 5)|| x == this.width-6)? this.borderColor:this.borderColor;
				if(y==this.height-2) return ((x < 3)|| x > this.width-4)?this.transparency:(x==3||x==4||x==this.width-5||x==this.width-4)?this.borderColor:this.color;
				if(y==this.height-3) return ((x < 2)|| x > this.width-3)?this.transparency:(x==2||x==this.width-3)?this.borderColor:this.color;
				if(y==this.height-4||y==this.height-5) return ((x < 1)|| x > this.width-2)?this.transparency:(x==1||x==this.width-2)?this.borderColor:this.color;
			}
			return (x==0||x==this.width-1||y==0||y==this.height-1)? this.borderColor:this.color;
		}
	}

}
