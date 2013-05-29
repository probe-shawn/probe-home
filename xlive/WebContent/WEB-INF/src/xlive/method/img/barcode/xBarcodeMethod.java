package xlive.method.img.barcode;

import xlive.method.*;

import java.awt.Font;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

public class xBarcodeMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		extendDefaultPropertiesToArguments();
		String type="",data="",label="",gif_jpeg_png="png";
		int bar_width=0,bar_height=0,resolution=0;
		boolean require_checksum=false,drawing_text=true,drawing_quiet_section=false,check_digit=false;
		String font_name=null;
		int font_style=-1,font_size=-1;
		String tmp;
		tmp=getArguments("type");
		type=(tmp!=null && tmp.trim().length()>0) ? tmp:type;
		tmp=getArguments("data");
		data=(tmp!=null && tmp.trim().length()>0) ? tmp:data;
		tmp=getArguments("label");
		label=(tmp!=null && tmp.trim().length()>0) ? tmp:label;
		tmp=getArguments("gif-jpeg-png");
		gif_jpeg_png=(tmp!=null && tmp.trim().length()>0) ? tmp:gif_jpeg_png;
		try{
			tmp=getArguments("bar-width");
			bar_width=(tmp!=null && tmp.trim().length()>0) ?Integer.parseInt(tmp) : bar_width;
		}catch(Exception e){}
		try{
			tmp=getArguments("bar-height");
			bar_height=(tmp!=null && tmp.trim().length()>0)?Integer.parseInt(tmp) : bar_height;
		}catch(Exception e){}
		try{
			tmp=getArguments("resolution");
			resolution=(tmp!=null && tmp.trim().length()>0)?Integer.parseInt(tmp) : resolution;
		}catch(Exception e){}
		tmp=getArguments("require-checksum");
		if(tmp!=null && tmp.trim().length()>0) require_checksum=("true".equalsIgnoreCase(tmp));
		tmp=getArguments("check-digit");
		if(tmp!=null && tmp.trim().length()>0) check_digit=("true".equalsIgnoreCase(tmp));
		tmp=getArguments("drawing-text");
		if(tmp!=null && tmp.trim().length()>0) drawing_text=("true".equalsIgnoreCase(tmp));
		tmp=getArguments("drawing-quiet-section");
		if(tmp!=null && tmp.trim().length()>0) drawing_quiet_section=("true".equalsIgnoreCase(tmp));
		tmp=getArguments("font-name");
		if(tmp!=null && tmp.trim().length()>0) font_name=tmp;
		try{
			tmp=getArguments("font-size");
			font_size=(tmp!=null && tmp.trim().length()>0)?Integer.parseInt(tmp) : font_size;
		}catch(Exception e){}
		try{
			tmp=getArguments("font-style");
			font_style=(tmp!=null && tmp.trim().length()>0)?Integer.parseInt(tmp) : font_style;
		}catch(Exception e){}
		//
		net.sourceforge.barbecue.Barcode barcode=null;
		try{
			if("2of7".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.create2of7(data);
			else if("3of9".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.create3of9(data,require_checksum);
			else if("bookland".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createBookland(data);
			else if("codabar".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createCodabar(data);
			else if("code128".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createCode128(data);
			else if("code128a".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createCode128A(data);
			else if("code128b".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createCode128B(data);
			else if("code128c".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createCode128C(data);
			else if("code39".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createCode39(data,require_checksum);
			else if("ean128".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createEAN128(data);
			else if("ean13".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createEAN13(data);
			else if("GlobalTradeItemNumber".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createGlobalTradeItemNumber(data);
			else if("int2of5".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createInt2of5(data,check_digit);
			else if("monarch".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createMonarch(data);
			else if("nw7".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createNW7(data);
			else if("pdf417".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createPDF417(data);
			else if("postnet".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createPostNet(data);
			else if("RandomWeightUPCA".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createRandomWeightUPCA(data);
			else if("SCC14ShippingCode".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createSCC14ShippingCode(data);
			else if("ShipmentIdentificationNumber".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createShipmentIdentificationNumber(data);
			else if("SSCC18".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createSSCC18(data);
			else if("Std2of5".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createStd2of5(data);
			else if("UCC128".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createUCC128("apid", data);
			else if("UPCA".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createUPCA(data);
			else if("USD3".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createUSD3(data,require_checksum);
			else if("USD4".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createUSD4(data);
			else if("USPS".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.createUSPS(data);
			else if("parseEAN128".equalsIgnoreCase(type))barcode=net.sourceforge.barbecue.BarcodeFactory.parseEAN128(data);
		}catch(net.sourceforge.barbecue.BarcodeException be){
			be.printStackTrace();
			throw createMethodException("BarcodeException", be.getLocalizedMessage());
		}
		if(barcode==null){
			throw createMethodException("BarcodeException", "barcode type :"+type+" not found");
		}
		//
		if(font_name!=null||font_size>0||font_style>=0){
			font_style=(font_style>=0) ? font_style : 0;
			font_size=(font_size>0)? font_size : 12;
			try{
				barcode.setFont(new Font(font_name,font_style,font_size));
			}catch(Exception e){}
		}
		//
		if(bar_height>0)barcode.setBarHeight(bar_height);
		if(bar_width>0)barcode.setBarWidth(bar_width);
		barcode.setDrawingText(drawing_text);
		barcode.setDrawingQuietSection(drawing_quiet_section);
		if(label!=null && label.trim().length()>0)barcode.setLabel(label);
		if(resolution>0)barcode.setResolution(resolution);
		/*
		try{
			BufferedImage image = new BufferedImage(bar_width,bar_height,BufferedImage.TYPE_BYTE_GRAY);
			Graphics2D g=(Graphics2D)image.getGraphics();
			barcode.draw(g, 0, 0);
		}catch(net.sourceforge.barbecue.output.OutputException e){
			e.printStackTrace();
		}
		*/
        HttpServletResponse response = getServiceContext().customizeResponse();
		response.setHeader("Cache-Control", "no-cache");
		try{
			if("gif".equalsIgnoreCase(gif_jpeg_png)){
				response.setContentType("image/gif");
				net.sourceforge.barbecue.BarcodeImageHandler.writeGIF(barcode, response.getOutputStream());
			}else if("jpeg".equalsIgnoreCase(gif_jpeg_png)){
				response.setContentType("image/jpeg");
				net.sourceforge.barbecue.BarcodeImageHandler.writeJPEG(barcode, response.getOutputStream());
			}else if("png".equalsIgnoreCase(gif_jpeg_png)){
				response.setContentType("image/png");
				net.sourceforge.barbecue.BarcodeImageHandler.writePNG(barcode, response.getOutputStream());
			}
		}catch(net.sourceforge.barbecue.output.OutputException ot){
			ot.printStackTrace();
			throw createMethodException("BarcodeException", ot.getLocalizedMessage());
		}catch(IOException ioe){
			ioe.printStackTrace();
			throw createMethodException("BarcodeException", ioe.getLocalizedMessage());
		}
        return getServiceContext().doNextProcess();
	}
}
