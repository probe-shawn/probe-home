package xlive.method.img;

import xlive.google.xMemCache;
import xlive.method.*;
import xlive.method.img.qrcode.google.PixelSource;
import xlive.method.img.qrcode.google.PngWriter;

import java.io.IOException;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Blob;

public class xRandomMethod extends xDefaultMethod{

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
		float opaque=1f;
		try{
			opaque=Float.parseFloat(getArguments("o"));
		}catch(Exception e){}
		int rand=10;
		try{
			rand=Integer.parseInt(getArguments("r"));
		}catch(Exception e){}
		byte[] pngbytes=null;
		StringBuffer memkey=new StringBuffer();
		memkey.append(width).append(height).append(color).append(opaque).append(rand);
		Blob blob = (Blob)xMemCache.pngService().get(memkey);
		if(blob != null) pngbytes=blob.getBytes();
		if(pngbytes==null||pngbytes.length==0){
			pngPixelSource ps = new pngPixelSource(width,height,color,opaque,rand);
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
		private int rand;
		private float opague=1;
		private int alpha=255;
		private int transparency=0;
		private Random random;
		public pngPixelSource(int width,int height, int color,float opague,int rand){
			this.width=width;
			this.height=height;
			this.opague=opague;
			this.rand=rand;
			this.alpha=Math.round((255*this.opague));
			this.color=color;//this.setAlpha(color, this.alpha);
			random = new Random(System.currentTimeMillis());
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
			int ret_color = this.setAlpha(color,Math.round((255*this.opague)+(this.rand*random.nextFloat())));
			return ret_color;	
		}
	}

}
