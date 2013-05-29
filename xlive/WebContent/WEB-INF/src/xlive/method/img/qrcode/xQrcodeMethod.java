package xlive.method.img.qrcode;

import xlive.xProbeServlet;
import xlive.method.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;


public class xQrcodeMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		extendDefaultPropertiesToArguments();
		if(xProbeServlet.isGAE()){
			return this.processWebObjectMethod( getObjectPath()+".google", getMethodName());
		}
		
		
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
		BufferedImage bi=new BufferedImage(width,width,BufferedImage.TYPE_INT_BGR);
		Graphics2D g2d=(Graphics2D)bi.createGraphics();
		g2d.setBackground(Color.WHITE);
		g2d.clearRect(0, 0, width, width);
		g2d.setColor(Color.BLACK);
		if(data_bytes.length > 0){
			this.logMessage("databytes :"+data_bytes.length);
			boolean[][] black=qrcode.calQrcode(data_bytes);
			this.logMessage("matrix :"+black.length);
			for(int i=0;i<black.length;++i){
				for(int j=0;j<black.length;++j){
					if(black[j][i]) g2d.fillRect(j*matrix_dot+2, i*matrix_dot+2, matrix_dot, matrix_dot);
				}
			}
		}
		g2d.dispose();
		bi.flush();
		///
        HttpServletResponse response = getServiceContext().customizeResponse();
		response.setHeader("Cache-Control", "no-cache");
		try{
			if("gif".equalsIgnoreCase(gif_jpeg_png)){
				response.setContentType("image/gif");
			}else if("jpeg".equalsIgnoreCase(gif_jpeg_png)){
				response.setContentType("image/jpeg");
				ImageIO.write(bi, "jpg", response.getOutputStream());
			}else if("png".equalsIgnoreCase(gif_jpeg_png)){
				response.setContentType("image/png");
				ImageIO.write(bi, "png", response.getOutputStream());
			}
		}catch(IOException ioe){
			ioe.printStackTrace();
			throw createMethodException("QrcodeException", ioe.getLocalizedMessage());
		}
        return getServiceContext().doNextProcess();
	}
}
