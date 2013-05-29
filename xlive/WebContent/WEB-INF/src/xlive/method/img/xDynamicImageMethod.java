package xlive.method.img;

import xlive.method.*;
import java.awt.image.*;
import java.awt.*;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.servlet.http.HttpServletResponse;

public class xDynamicImageMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		String type=getArguments("type");
		int width=100,height=100;
		try{
			width=Integer.parseInt(getArguments("width"));
		}catch(Exception e){}
		try{
			height=Integer.parseInt(getArguments("height"));
		}catch(Exception e){}
		String text_to_image=getArguments("text-to-image");
		this.logMessage("text_to_image :"+text_to_image);
		//
		width=200;
		height=100;
		//text_to_image="12345";
		//
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.setColor(Color.BLUE);
        g2d.setBackground(Color.red);
        //g2d.fillRect(0, 0, 100, 100);
        g2d.drawString(text_to_image, 40, 40);
        //
        HttpServletResponse response = getServiceContext().customizeResponse();
		response.setContentType("image/png");
		response.setHeader("Cache-Control", "no-cache");
        try {
            Iterator iter = ImageIO.getImageWritersByFormatName("PNG");
            if(iter.hasNext()) {
               ImageWriter writer = (ImageWriter)iter.next();
               MemoryCacheImageOutputStream output = new MemoryCacheImageOutputStream(response.getOutputStream());
               writer.setOutput(output);
               IIOImage image =  new IIOImage(bi, null, null);
               writer.write(null, image, null);
               output.close();
               }
        }catch(java.io.IOException ioe){
        	  ioe.printStackTrace();
        }catch(Exception e) {
        	  e.printStackTrace();
        }
        return getServiceContext().doNextProcess();
	}
}
