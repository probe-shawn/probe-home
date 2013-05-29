package xlive;

import java.io.*;
import java.util.Properties;

import javax.servlet.ServletContext;

public class xMakeJsCss {
	  public xMakeJsCss(){}
	  public void makeCSS(ServletContext context){
			String dir = context.getRealPath("/");
			File css_dir=new File(dir,"css");
			boolean makecss=false;
			File xlvall_css=new File(css_dir,"xlvall.css");
			if(!xlvall_css.exists()) makecss=true;
			else{
				long xlvall_css_last=xlvall_css.lastModified();
				File[] csslist=css_dir.listFiles();
				for(int i=0;i<csslist.length;++i){
					if(!csslist[i].getName().startsWith("class_")) continue;
					if(csslist[i].lastModified()>xlvall_css_last){
						makecss=true;
						break;
					}
				}
			}
			if(makecss){
				try{
					FileOutputStream fos=new FileOutputStream(xlvall_css);
					File[] csslist=css_dir.listFiles();
					for(int i=0;i<csslist.length;++i){
						if(!csslist[i].getName().startsWith("class_")) continue;
						if(!csslist[i].isFile()) continue;
						if(csslist[i].equals(xlvall_css))continue;
						xUtility.copyStream(new FileInputStream(csslist[i]), fos);
					}
					fos.close();
				}catch(IOException ioe){
					System.out.println(ioe);
				}
			}
	  }
	  public void makeJS(ServletContext context){
			String dir = context.getRealPath("/");
			File js_dir=new File(dir,"script");
			boolean makejs=false;
			//
			Properties prop = new Properties();
			try {
				prop.load(new FileInputStream(new File(js_dir,"xlv.properties")));
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
			//
			File xlvall_js=new File(js_dir,"xlvall.js");
			if(!xlvall_js.exists()) makejs=true;
			else{
				long xlvall_js_last=xlvall_js.lastModified();
				File[] js_xlv_list=new File(js_dir,"xlv").listFiles();
				for(int i=0;i<js_xlv_list.length;++i){
					if(js_xlv_list[i].lastModified()>xlvall_js_last){
						makejs=true;
						break;
					}
				}
				if(!makejs){
					File[] js_imp_list=new File(js_dir,"xlv/imp").listFiles();
					for(int i=0;i<js_imp_list.length;++i){
						if(js_imp_list[i].lastModified()>xlvall_js_last){
							makejs=true;
							break;
						}
					}
				}
				if(!makejs){
					File[] js_obj_list=new File(js_dir,"xlv/obj").listFiles();
					for(int i=0;i<js_obj_list.length;++i){
						if(js_obj_list[i].lastModified()>xlvall_js_last){
							makejs=true;
							break;
						}
					}
				}
			}
			if(makejs){
				try{
					FileOutputStream fos=new FileOutputStream(xlvall_js);
					File[] js_xlv_list=new File(js_dir,"xlv").listFiles();
					for(int i=0;i<js_xlv_list.length;++i){
						if(!js_xlv_list[i].isFile()) continue;
						xUtility.copyStream(new FileInputStream(js_xlv_list[i]), fos);
					}
					File[] js_imp_list=new File(js_dir,"xlv/imp").listFiles();
					for(int i=0;i<js_imp_list.length;++i){
						if(!js_imp_list[i].isFile()) continue;
						xUtility.copyStream(new FileInputStream(js_imp_list[i]), fos);
					}
					File[] js_obj_list=new File(js_dir,"xlv/obj").listFiles();
					for(int i=0;i<js_obj_list.length;++i){
						if("exclude".equals(prop.get(js_obj_list[i].getName()))) continue;
						if(!js_obj_list[i].isFile()) continue;
						xUtility.copyStream(new FileInputStream(js_obj_list[i]), fos);
					}
					fos.close();
				}catch(IOException ioe){
					System.out.println(ioe);
				}
			}
	  }
}
