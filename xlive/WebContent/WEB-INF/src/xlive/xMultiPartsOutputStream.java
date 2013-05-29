package xlive;

import java.io.*;

public class xMultiPartsOutputStream {
	private OutputStream outputStream;
	private boolean endedLastPart = true;
	public static final String boundary="86719712";
	public static final String charset="utf-8";
	
	public xMultiPartsOutputStream(OutputStream output)throws IOException {
		outputStream=output;
		outputStream.write(("--"+boundary).getBytes());
		outputStream.write("\r\n".getBytes());
		writeParamPart("_charset_", charset);
	}
	public void writeParamPart(String name, String value)throws IOException{
		if(!endedLastPart) endPart();
		outputStream.write(("Content-Disposition: form-data; name=\"" + name+"\"").getBytes());
		outputStream.write("\r\n".getBytes());
		outputStream.write("\r\n".getBytes());
		outputStream.write(value.getBytes(charset));
		endPart();
	}
	public void startPart(String contentType) throws IOException {
		if(!endedLastPart) endPart();
		outputStream.write(("Content-type: " + contentType).getBytes());
		outputStream.write("\r\n".getBytes());
		endedLastPart = false;
	}
	public void writeFileParam(String name, String file_name)throws IOException{
		outputStream.write(("Content-Disposition: form-data; name=\"" + name+"\"; filename=\""+file_name+"\"").getBytes(charset));
		outputStream.write("\r\n".getBytes());
		outputStream.write("\r\n".getBytes());
	}
	public void endPart() throws IOException {
		outputStream.write("\r\n".getBytes());
		outputStream.write(("--"+boundary).getBytes());
		outputStream.write("\r\n".getBytes());
		outputStream.flush();
		endedLastPart = true;
	}
	public void finish() throws IOException {
		outputStream.write("\r\n".getBytes());
		outputStream.write(("--"+boundary+"--").getBytes());
		outputStream.write("\r\n".getBytes());
	}

}
