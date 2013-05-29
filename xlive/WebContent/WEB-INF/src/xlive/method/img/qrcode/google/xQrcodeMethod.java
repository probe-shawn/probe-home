package xlive.method.img.qrcode.google;

import xlive.method.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletResponse;


public class xQrcodeMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		extendDefaultPropertiesToArguments();
		String data="",gif_jpeg_png="png",error_correct="M",encode_mode="B",charset="UTF-8";
		int version=7,matrix_dot=3;
		String tmp;
		tmp=getArguments("data");
		data=(tmp!=null && tmp.trim().length()>0) ? tmp:data;
		tmp=getArguments("error-correct");
		error_correct=(tmp!=null && tmp.trim().length()>0) ? tmp:error_correct;
		tmp=getArguments("encode-mode");
		encode_mode=(tmp!=null && tmp.trim().length()>0) ? tmp:encode_mode;
		tmp=getArguments("gif-jpeg-png");
		gif_jpeg_png=(tmp!=null && tmp.trim().length()>0) ? tmp:gif_jpeg_png;
		try{
			tmp=getArguments("version");
			version=(tmp!=null && tmp.trim().length()>0) ?Integer.parseInt(tmp) : version;
			version=(version<=0)? 7 : version;
		}catch(Exception e){}
		try{
			tmp=getArguments("matrix-dot");
			matrix_dot=(tmp!=null && tmp.trim().length()>0) ?Integer.parseInt(tmp) : matrix_dot;
			matrix_dot=(matrix_dot<=0) ? 3 : matrix_dot;
		}catch(Exception e){}
		com.swetake.util.Qrcode qrcode=new com.swetake.util.Qrcode();
		qrcode.setQrcodeEncodeMode(encode_mode.charAt(0));
		qrcode.setQrcodeErrorCorrect(error_correct.charAt(0));
		qrcode.setQrcodeVersion(version);
		byte[] data_bytes=null;
		try{
			data_bytes=data.getBytes(charset);
		}catch(UnsupportedEncodingException use){
			use.printStackTrace();
			throw createMethodException("QrcodeException", use.getLocalizedMessage());
		}
		int width=((version-1)*4+21)*matrix_dot+5;
        HttpServletResponse response = getServiceContext().customizeResponse();
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("image/png");
		try{
			byte[] pngbytes=null;
			if(data_bytes.length > 0){
				this.logMessage("databytes :"+data_bytes.length);
				boolean[][] black=qrcode.calQrcode(data_bytes);
				this.logMessage("matrix :"+black.length);
				qrPixelSource ps = new qrPixelSource(width,width,black,matrix_dot);
				PngWriter pw = new PngWriter();
				pngbytes= pw.generateImage(ps);
			}
			response.getOutputStream().write(pngbytes);
		}catch(IOException ioe){
			ioe.printStackTrace();
			throw createMethodException("QrcodeException", ioe.getLocalizedMessage());
		}
        return getServiceContext().doNextProcess();
	}
	class qrPixelSource implements PixelSource{
		private int width;
		private int height;
		private boolean[][] blackBits;
		private int matrixDots;
		public qrPixelSource(int width,int height, boolean[][] black_bits, int matrix_dots){
			this.width=width;
			this.height=height;
			this.blackBits=black_bits;
			this.matrixDots=matrix_dots;
		}
		public int getWidth(){
			return width;
		}
		public int getHeight(){
			return height;
		}
		public int getPixel(int x, int y){
			int black_color = ColorUtil.create(0,0,0);
			int white_color = ColorUtil.create(255,255,255);
			boolean black=false;
			int max_x=2+blackBits.length*matrixDots;
			int max_y=2+blackBits.length*matrixDots;
			if(x<=2||y<=2||x>max_x||y>max_y) black=false;
			else{
				x=((x-2)-1)/matrixDots;
				y=((y-2)-1)/matrixDots;
				black=blackBits[x][y];
			}
			/*
			for(int i=0;i<black.length;++i){
				for(int j=0;j<black.length;++j){
					if(black[j][i]) g2d.fillRect(j*matrix_dot+2, i*matrix_dot+2, matrix_dot, matrix_dot);
				}
			}
			*/
			return (black) ? black_color:white_color;
		}
	}
}
