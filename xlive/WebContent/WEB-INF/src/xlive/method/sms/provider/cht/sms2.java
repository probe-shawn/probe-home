package xlive.method.sms.provider.cht;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;


public class sms2 {
private Socket sock ;
private DataInputStream  din ;
private DataOutputStream dout ;
private String ret_message = "" ;
private String ret_msisdn = "" ;


  public sms2() {} ;

  // 十六進位轉十進位
  public int HexToDec(String input){
     int sum=0;
     for (int i=0;i<input.length() ;i++ ){
        if (input.charAt(i)>='0' && input.charAt(i)<='9')
           sum=sum*16+input.charAt(i)-48;
        else if (input.charAt(i)>='A' && input.charAt(i)<='F')
           sum=sum*16+input.charAt(i)-55;
     }
     return sum;
  }


  //建立Socket連線，並做帳號密碼檢查
  public int create_conn(String host, int port, String user, String passwd) {

    //---設定送出訊息訊息的buffer
    byte out_buffer[] = new byte[266]; //傳送長度為266

    //---設定接收訊息的buffer
    byte ret_code = 99;
    byte ret_coding = 0;
    byte ret_set_len = 0;
    byte ret_content_len = 0;
    byte ret_set[] = new byte[80];
    byte ret_content[] = new byte[160];

     try {
         //---建 socket
         this.sock = new Socket(host , port);

         this.din  = new DataInputStream(this.sock.getInputStream());
         this.dout = new DataOutputStream(this.sock.getOutputStream());

        //---開始帳號密碼檢查
        int i;
        //----清除 buffer
        for( i=0 ; i < 266 ; i++ ) out_buffer[i] = 0 ;
        for( i=0 ; i < 80 ; i++ ) ret_set[i] = 0 ;
        for( i=0 ; i < 160 ; i++ ) ret_content[i] = 0 ;

        //---設定帳號與密碼
        String acc_pwd_str = user.trim() + "\0" + passwd.trim() + "\0" ;
        byte   acc_pwd_byte[] = acc_pwd_str.getBytes();
        byte   acc_pwd_size = (byte)acc_pwd_byte.length ;
 
        out_buffer[0] = 0; //檢查密碼
        out_buffer[1] = 1; //big編碼
        out_buffer[2] = 0; //priority
        out_buffer[3] = 0; //國碼 0:台灣
        out_buffer[4] = acc_pwd_size ; //msg_set_len
        out_buffer[5] = 0; //msg_content_len, 驗證密碼時不需msg_content
        //設定msg_set 內容 "帳號"+"密碼"
        for( i = 0; i < acc_pwd_size ; i++ )
              out_buffer[i + 6] = acc_pwd_byte[i] ;
        
        //----送出訊息
        //this.dout.write(out_buffer , 0 , acc_pwd_size + 3 );
        this.dout.write(out_buffer );
        //---讀 return code
        ret_code = this.din.readByte();
	      ret_coding = this.din.readByte();
        ret_set_len = this.din.readByte();
        ret_content_len = this.din.readByte();
        
        //---讀 return message
        this.din.read(ret_set,0,80);
        this.din.read(ret_content,0,160);
        this.ret_message = new String(ret_content);
        return ret_code ;

     } catch( UnknownHostException e) {
          this.ret_message = "Cannot find the host!";
          return 70 ;
     } catch( IOException ex) {
          this.ret_message = "Socket Error: " + ex.getMessage();
          return 71 ;
     }

  }//end of function

  //結束Socket連線
  public void close_conn() {
     try {
         if( this.din  != null) this.din.close();
         if( this.dout != null) this.dout.close();
         if( this.sock != null) this.sock.close();

         this.din = null ;
         this.dout = null;
         this.sock = null ;

     } catch( UnknownHostException e) {
          this.ret_message = "Cannot find the host!";
     } catch( IOException ex) {
          this.ret_message = "Socket Error: " + ex.getMessage();
     }

  }//end of function


  //傳送文字簡訊 (即時傳送)
  public int send_text_message( String sms_tel, String message) {

    //---設定送出訊息訊息的buffer
    byte out_buffer[] = new byte[266]; //傳送長度為266

    //----設定接收的buffer
    byte ret_code = 99;
    byte ret_coding = 0;
    byte ret_set_len = 0;
    byte ret_content_len = 0;
    byte ret_set[] = new byte[80];
    byte ret_content[] = new byte[160];

    try {
        int i ;
        //----清除 buffer
        for( i=0 ; i < 266 ; i++ ) out_buffer[i] = 0 ;
        for( i=0 ; i < 80 ; i++ ) ret_set[i] = 0 ;
        for( i=0 ; i < 160 ; i++ ) ret_content[i] = 0 ;
      
        //---設定傳送訊息的內容 01:即時傳送
        String msg_set = sms_tel.trim() + "\0" + "01" + "\0" ;
        byte msg_set_byte[] = msg_set.getBytes();
        int msg_set_size = msg_set_byte.length ;

        String msg_content = message.trim() + "\0" ;
        byte msg_content_byte[] = msg_content.getBytes("big5"); //需指定轉碼為Big5，不然會印出??
        int msg_content_size = msg_content_byte.length - 1 ; //send_type=1時,長度不包含'\0'

      	if(msg_set_size > 80){
                this.ret_message = "msg_set > max limit!";
                return 80 ;
      	}
      	if(msg_content_size > 159){
                this.ret_message = "msg_content > max limit!";
                return 81 ;
      	}
	
        //---設定送出訊息的 buffer
        if(sms_tel.startsWith("+"))
           out_buffer[0] = 15; //send text 國際簡訊
        else
           out_buffer[0] = 1; //send text 國內簡訊
        out_buffer[1] = 1; //big5編碼
        out_buffer[2] = 0; //priority
        out_buffer[3] = 0; //國碼 0:台灣
        out_buffer[4] = (byte)msg_set_size ; //msg_set_len
        out_buffer[5] = (byte)msg_content_size; //msg_content_len

        //設定msg_set 內容 "手機號碼"+"傳送形式"
        for( i = 0; i < msg_set_size ; i++ )
              out_buffer[i+6] = msg_set_byte[i] ;

        //設定msg_content 內容 "訊息內容"
        for( i = 0; i < msg_content_size ; i++ )
              out_buffer[i+106] = msg_content_byte[i] ;

        //----送出訊息
        this.dout.write(out_buffer);

        //---讀 return code
        ret_code = this.din.readByte();
  	    ret_coding = this.din.readByte();
        ret_set_len = this.din.readByte();
        ret_content_len = this.din.readByte();
        
        //---讀 return message
        this.din.read(ret_set,0,80);
        this.din.read(ret_content,0,160);
        this.ret_message = new String(ret_content);
        this.ret_message = this.ret_message.trim();
        return ret_code ;

    } catch( UnknownHostException eu) {
         this.ret_message = "Cannot find the host!";
         return 70 ;
    } catch( IOException ex) {
         this.ret_message = " Socket Error: " + ex.getMessage();
         return 71 ;
    }
  }//end of function


  //傳送文字簡訊 (預約傳送)
  public int send_text_message( String sms_tel, String message, String order_time) {

    //---設定送出訊息訊息的buffer
    byte out_buffer[] = new byte[266]; //傳送長度為266

    //----設定接收的buffer
    byte ret_code = 99;
    byte ret_coding = 0;
    byte ret_set_len = 0;
    byte ret_content_len = 0;
    byte ret_set[] = new byte[80];
    byte ret_content[] = new byte[160];

    try {
        int i ;
        //----清除 buffer
        for( i=0 ; i < 266 ; i++ ) out_buffer[i] = 0 ;
        for( i=0 ; i < 80 ; i++ ) ret_set[i] = 0 ;
        for( i=0 ; i < 160 ; i++ ) ret_content[i] = 0 ;
      
        //---設定傳送訊息的內容 03:預約傳送
        String msg_set = sms_tel.trim() + "\0" + "03" + "\0" + order_time.trim();
        byte msg_set_byte[] = msg_set.getBytes();
        int msg_set_size = msg_set_byte.length ;

        String msg_content = message.trim() + "\0" ;
        byte msg_content_byte[] = msg_content.getBytes("Big5"); //需指定轉碼為Big5，不然會印出??
        int msg_content_size = msg_content_byte.length - 1 ; //send_type=1時,長度不包含'\0'

        if(msg_set_size > 80){
                 this.ret_message = "msg_set > max limit!";
                 return 80 ;
        }
        if(msg_content_size > 159){
                 this.ret_message = "msg_content > max limit!";
                 return 81 ;
        }
	
        //---設定送出訊息的 buffer
        if(sms_tel.startsWith("+"))
           out_buffer[0] = 15; //send text 國際簡訊
        else
           out_buffer[0] = 1; //send text 國內簡訊
        out_buffer[1] = 1; //big5編碼
        out_buffer[2] = 0; //priority
        out_buffer[3] = 0; //國碼 0:台灣
        out_buffer[4] = (byte)msg_set_size ; //msg_set_len
        out_buffer[5] = (byte)msg_content_size; //msg_content_len

        //設定msg_set 內容 "手機號碼"+"傳送形式"+"預約時間"
        for( i = 0; i < msg_set_size ; i++ )
              out_buffer[i+6] = msg_set_byte[i] ;

        //設定msg_content 內容 "訊息內容"
        for( i = 0; i < msg_content_size ; i++ )
              out_buffer[i+106] = msg_content_byte[i] ;

        //----送出訊息
        this.dout.write(out_buffer);

        //---讀 return code
        ret_code = this.din.readByte();
	      ret_coding = this.din.readByte();
        ret_set_len = this.din.readByte();
        ret_content_len = this.din.readByte();
        
        //---讀 return message
        this.din.read(ret_set,0,80);
        this.din.read(ret_content,0,160);
        this.ret_message = new String(ret_content);
        this.ret_message = this.ret_message.trim();
        return ret_code ;

    } catch( UnknownHostException eu) {
         this.ret_message = "Cannot find the host!";
         return 70 ;
    } catch( IOException ex) {
         this.ret_message = " Socket Error: " + ex.getMessage();
         return 71 ;
    }
  }//end of function


  //查詢文字簡訊的傳送結果
  //type -> 2:text ,6:logo, 8:ringtone, 10:picmsg, 14:wappush
  public int query_message(int type, String messageid) {

    //---設定送出訊息的buffer
    byte out_buffer[] = new byte[266]; //傳送長度為266
    //----設定接收的buffer
    byte ret_code = 99;
    byte ret_coding = 0;
    byte ret_set_len = 0;
    byte ret_content_len = 0;
    byte ret_set[] = new byte[80];
    byte ret_content[] = new byte[160];

    try {
        int i ;
        //----清除 buffer
        for( i=0 ; i < 266 ; i++ ) out_buffer[i] = 0 ;
        for( i=0 ; i < 80 ; i++ ) ret_set[i] = 0 ;
        for( i=0 ; i < 160 ; i++ ) ret_content[i] = 0 ;
        
        //---設定message id
        String msg_set = messageid.trim() + "\0";
        byte msg_set_byte[] = msg_set.getBytes();
        int msg_set_size = msg_set_byte.length ;

        if(msg_set_size > 80){
                 this.ret_message = "msg_set > max limit!";
                 return 80 ;
        }

        //---設定送出訊息的 buffer
        out_buffer[0] = (byte)type; //query type  02:text ,06:logo, 08 ringtone, 10:picmsg, 14:wappush
        out_buffer[1] = 1; //big5編碼
        out_buffer[2] = 0; //priority
        out_buffer[3] = 0; //國碼 0:台灣
        out_buffer[4] = (byte)msg_set_size ; //msg_set_len
        out_buffer[5] = 0;  //msg_content_len

        //設定messageid
        for( i = 0; i < msg_set_size ; i++ )
              out_buffer[i+6] = msg_set_byte[i] ;

        //----送出訊息
        this.dout.write(out_buffer);

        //---讀 return code
        ret_code = this.din.readByte();
	      ret_coding = this.din.readByte();
        ret_set_len = this.din.readByte();
        ret_content_len = this.din.readByte();
        
        //---讀 return message
        this.din.read(ret_set,0,80);
        this.din.read(ret_content,0,160);
        this.ret_message = new String(ret_content);
        this.ret_message = this.ret_message.trim();
        return ret_code ;

    } catch( UnknownHostException eu) {
         this.ret_message = "Cannot find the host!";
         return 70 ;
    } catch( IOException ex) {
         this.ret_message = " Socket Error: " + ex.getMessage();
         return 71 ;
    }
  }//end of function


  //接收文字簡訊
  public int recv_text_message() {

    //---設定送出訊息訊息的buffer
    byte out_buffer[] = new byte[266]; //傳送長度為266

    //----設定接收的buffer
    byte ret_code = 99;
    byte ret_coding = 0;
    byte ret_set_len = 0;
    byte ret_content_len = 0;
    byte ret_set[] = new byte[80];
    byte ret_content[] = new byte[160];

    try {
        int i ;
        //----清除 buffer
        for( i=0 ; i < 266 ; i++ ) out_buffer[i] = 0 ;
        for( i=0 ; i < 80 ; i++ ) ret_set[i] = 0 ;
        for( i=0 ; i < 160 ; i++ ) ret_content[i] = 0 ;
      
        //---設定送出訊息的 buffer
        out_buffer[0] = 3; //recv text message
        out_buffer[1] = 1; //big5編碼
        out_buffer[2] = 0; //priority
        out_buffer[3] = 0; //國碼 0:台灣
        out_buffer[4] = 0; //msg_set_len
        out_buffer[5] = 0; //msg_content_len

        //----送出訊息
        this.dout.write(out_buffer);

        //---讀 return code
        ret_code = this.din.readByte();
        ret_coding = this.din.readByte();
        ret_set_len = this.din.readByte();
        ret_content_len = this.din.readByte();

        //---讀 return message
        this.din.read(ret_set,0,80);
        this.din.read(ret_content,0,160);
        this.ret_message = new String(ret_content,"big5");
        this.ret_message = this.ret_message.trim();

        this.ret_msisdn="";
        //ret_code==0 表示有資料，則取出傳送端的手機號碼
        if(ret_code==0){
           String ret_set_msg = new String(ret_set);
           //將string用'\0'分開，
           StringTokenizer tok = new StringTokenizer(ret_set_msg,"\0");
           if(tok.hasMoreTokens()){
              this.ret_msisdn=tok.nextToken();
           }
        }

        return ret_code ;

    } catch( UnknownHostException eu) {
         this.ret_message = "Cannot find the host!";
         return 70 ;
    } catch( IOException ex) {
         this.ret_message = " Socket Error: " + ex.getMessage();
         return 71 ;
    }
  }//end of function


  //取消預約文字簡訊
  public int cancel_text_message(String messageid) {

    //---設定送出訊息的buffer
    byte out_buffer[] = new byte[266]; //傳送長度為266
    //----設定接收的buffer
    byte ret_code = 99;
    byte ret_coding = 0;
    byte ret_set_len = 0;
    byte ret_content_len = 0;
    byte ret_set[] = new byte[80];
    byte ret_content[] = new byte[160];

    try {
        int i ;
        //----清除 buffer
        for( i=0 ; i < 266 ; i++ ) out_buffer[i] = 0 ;
        for( i=0 ; i < 80 ; i++ ) ret_set[i] = 0 ;
        for( i=0 ; i < 160 ; i++ ) ret_content[i] = 0 ;
        
        //---設定message id
        String msg_set = messageid.trim() + "\0";
        byte msg_set_byte[] = msg_set.getBytes();
        int msg_set_size = msg_set_byte.length ;

        if(msg_set_size > 80){
                 this.ret_message = "msg_set > max limit!";
                 return 80 ;
        }

        //---設定送出訊息的 buffer
        out_buffer[0] = 16; //取消預約簡訊
        out_buffer[1] = 1; //big5編碼
        out_buffer[2] = 0; //priority
        out_buffer[3] = 0; //國碼 0:台灣
        out_buffer[4] = (byte)msg_set_size ; //msg_set_len
        out_buffer[5] = 0;  //msg_content_len

        //設定messageid
        for( i = 0; i < msg_set_size ; i++ )
              out_buffer[i+6] = msg_set_byte[i] ;

        //----送出訊息
        this.dout.write(out_buffer);

        //---讀 return code
        ret_code = this.din.readByte();
	      ret_coding = this.din.readByte();
        ret_set_len = this.din.readByte();
        ret_content_len = this.din.readByte();
        
        //---讀 return message
        this.din.read(ret_set,0,80);
        this.din.read(ret_content,0,160);
        this.ret_message = new String(ret_content);
        this.ret_message = this.ret_message.trim();
        return ret_code ;

    } catch( UnknownHostException eu) {
         this.ret_message = "Cannot find the host!";
         return 70 ;
    } catch( IOException ex) {
         this.ret_message = " Socket Error: " + ex.getMessage();
         return 71 ;
    }
  }//end of function


  //傳送wappush
  public int send_wappush_message( String sms_tel, String sms_url, String message) {

    //---設定送出訊息訊息的buffer
    byte out_buffer[] = new byte[266]; //傳送長度為266

    //----設定接收的buffer
    byte ret_code = 99;
    byte ret_coding = 0;
    byte ret_set_len = 0;
    byte ret_content_len = 0;
    byte ret_set[] = new byte[80];
    byte ret_content[] = new byte[160];

    try {
        int i ;
        //----清除 buffer
        for( i=0 ; i < 266 ; i++ ) out_buffer[i] = 0 ;
        for( i=0 ; i < 80 ; i++ ) ret_set[i] = 0 ;
        for( i=0 ; i < 160 ; i++ ) ret_content[i] = 0 ;
      
        //---設定傳送訊息的內容 01:SI
        String msg_set = sms_tel.trim() + "\0" + "01" + "\0" ;
        byte msg_set_byte[] = msg_set.getBytes();
        int msg_set_size = msg_set_byte.length ;

        String msg_content = sms_url.trim() + "\0" + message.trim() + "\0" ;
        byte msg_content_byte[] = msg_content.getBytes("Big5"); //需指定轉碼為Big5，不然會印出??
        int msg_content_size = msg_content_byte.length ;

        //---設定送出訊息的 buffer
        out_buffer[0] = 13; //send wappush
        out_buffer[1] = 1; //big編碼
        out_buffer[2] = 0; //priority
        out_buffer[3] = 0; //國碼 0:台灣
        out_buffer[4] = (byte)msg_set_size ; //msg_set_len
        out_buffer[5] = (byte)msg_content_size; //msg_content_len

        //設定msg_set 內容 "手機號碼"+"傳送形式"
        for( i = 0; i < msg_set_size ; i++ )
              out_buffer[i+6] = msg_set_byte[i] ;

        //設定msg_content 內容 "url"+"訊息內容"
        for( i = 0; i < msg_content_size ; i++ )
              out_buffer[i+106] = msg_content_byte[i] ;

        //----送出訊息
        this.dout.write(out_buffer);

        //---讀 return code
        ret_code = this.din.readByte();
        ret_coding = this.din.readByte();
        ret_set_len = this.din.readByte();
        ret_content_len = this.din.readByte();
        
        //---讀 return message
        this.din.read(ret_set,0,80);
        this.din.read(ret_content,0,160);
        this.ret_message = new String(ret_content);
        this.ret_message = this.ret_message.trim();
        return ret_code ;

    } catch( UnknownHostException eu) {
         System.out.println(" Cannot find the host ");
         return 70 ;
    } catch( IOException ex) {
         System.out.println(" Socket Error: " + ex.getMessage());
         return 71 ;
    }
  }//end of function


  public String get_message() {

     return ret_message;
  }


  public String get_msisdn() {

     return ret_msisdn;
  }

  //主函式 - 使用文字簡訊範例
  public static void main(String[] args) throws Exception {

  try {
      String server  = "202.39.54.130"; //hiAirV2 Gateway IP
      int port	     = 8000;            //Socket to Air Gateway Port

      if(args.length<4){
         System.out.println("Use: java sms2 id passwd tel message");
         System.out.println(" Ex: java sms2 test test123 0910123xxx HiNet簡訊!");
         return;
      }
      String user    = args[0]; //帳號
      String passwd  = args[1]; //密碼
      String tel     = args[2]; //手機號碼
      String message = new String(args[3].getBytes(),"big5"); //簡訊內容

      //----建立連線 and 檢查帳號密碼是否錯誤
      sms2 mysms = new sms2();
      int k = mysms.create_conn(server,port,user,passwd) ;
      if( k == 0 ) {
           System.out.println("帳號密碼check ok!");
      } else {
           System.out.println(mysms.get_message());
           //結束連線
           mysms.close_conn();
           return ;
      }

      k=mysms.send_text_message(tel,message);
      if( k == 0 ) {
           System.out.println("簡訊已送到簡訊中心!");
           System.out.println("MessageID="+mysms.get_message());
      } else {
           System.out.println("簡訊傳送發生錯誤!");
           System.out.print("ret_code="+k+",");
           System.out.println("ret_content="+mysms.get_message());
           //結束連線
           mysms.close_conn();
           return ;
      }

      //結束連線
      mysms.close_conn();

  }catch (Exception e)  {

      System.out.println("I/O Exception : " + e);
   }
 }

}//end of class
