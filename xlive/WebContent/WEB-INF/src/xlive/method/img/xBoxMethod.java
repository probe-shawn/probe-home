package xlive.method.img;

import xlive.google.xMemCache;
import xlive.method.*;
import xlive.method.img.qrcode.google.PixelSource;
import xlive.method.img.qrcode.google.PngWriter;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Blob;


public class xBoxMethod extends xDefaultMethod{

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
		int border=4;
		try{
			border=Integer.parseInt(getArguments("b"));
		}catch(Exception e){}

		byte[] pngbytes=null;
		StringBuffer memkey=new StringBuffer();
		memkey.append(width).append(height).append(border).append(color).append(fo).append(to);
		Blob blob = (Blob)xMemCache.pngService().get(memkey);
		if(blob != null) pngbytes=blob.getBytes();
		if(pngbytes==null || pngbytes.length==0){
			pngPixelSource ps = new pngPixelSource(width,height,border,color,fo,to);
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
		private int border;
		private int xalphaColor;
		private int transparency=0;
		private int fromAlpha=255;
		private int toAlpha=255;
		private int offAlpha=0;
		
		public pngPixelSource(int width,int height,int border,int color,float fo,float to){
			this.width=width;
			this.height=height;
			this.border=border;
			
			this.fromAlpha=Math.round((255*fo));
			this.toAlpha=Math.round((255*to));
			this.offAlpha=this.toAlpha-this.fromAlpha;
			
			this.xalphaColor=color;
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
			int color= this.setAlpha(xalphaColor, this.toAlpha);
			/*
			if(x > this.border && x < this.width-this.border && y > this.border && y < this.height-this.border) 
				return this.setAlpha(xalphaColor, this.toAlpha);
			*/
			if(x>border && x<this.width-border && y < this.border){
				return this.setAlpha(xalphaColor, this.fromAlpha+Math.round(offAlpha*(y*100/border)/100));
			}
			if(x>border && x<this.width-border && y > this.height-this.border){
				return this.setAlpha(xalphaColor, this.fromAlpha+Math.round(offAlpha*((this.height-y)*100/border)/100));
			}
			if(y>border && y<this.height-border && x < this.border){
				return this.setAlpha(xalphaColor, this.fromAlpha+Math.round(offAlpha*(x*100/border)/100));
			}
			if(y>border && y<this.height-border && x > this.width-this.border){
				return this.setAlpha(xalphaColor, this.fromAlpha+Math.round(offAlpha*((this.width-x)*100/border)/100));
			}
			if(x<=border && y<=border){
				double len=this.border-Math.sqrt((this.border-x)*(this.border-x)+(this.border-y)*(this.border-y));
				if(len < 0) return this.transparency;
				return this.setAlpha(xalphaColor, this.fromAlpha+(int)Math.round(offAlpha*(len*100/border)/100));
			}
			if(x>=this.width-border && y<=border){
				double len=this.border-Math.sqrt((x-this.width+border)*(x-this.width+border)+(y-this.border)*(y-this.border));
				if(len < 0) return this.transparency;
				return this.setAlpha(xalphaColor, this.fromAlpha+(int)Math.round(offAlpha*(len*100/border)/100));
			}
			if(x<=border && y>=this.height-border){
				double len=this.border-Math.sqrt((x-border)*(x-border)+(y-this.height+border)*(y-this.height+border));
				if(len < 0) return this.transparency;
				return this.setAlpha(xalphaColor, this.fromAlpha+(int)Math.round(offAlpha*(len*100/border)/100));
			}
			if(x>=this.width-border && y>=this.height-border){
				double len=this.border-Math.sqrt((x-this.width+border)*(x-this.width+border)+(y-this.height+border)*(y-this.height+border));
				if(len < 0) return this.transparency;
				return this.setAlpha(xalphaColor, this.fromAlpha+(int)Math.round(offAlpha*(len*100/border)/100));
			}
			return color;
		}
	}

}
