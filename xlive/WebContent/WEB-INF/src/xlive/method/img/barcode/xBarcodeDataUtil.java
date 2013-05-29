package xlive.method.img.barcode;

import xlive.method.xDefaultMethod;

public class xBarcodeDataUtil extends xDefaultMethod{
	public static String getBarcodeCheckcode(String str1,String str2,String str3){		
		if(str1.length()!=9 || str2.length()!=16 || str3.length()!=15)
			return null;
		String tmpstr=str1+"0"+str2+str3;
		int strlen=0,sum1=0,sum2=0;
		while(strlen<tmpstr.length()){
			int i=(int)tmpstr.charAt(strlen);
			
			int j=0;
			if(i>=48 && i<=57)j=i-48;
			if(i>=65 && i<=73)j=i-64;
			if(i>=74 && i<=82)j=i-73;
			if(i>=83 && i<=90)j=i-81;	
			
			if(strlen%2==0)
				sum1+=j;			
			else
				sum2+=j;			
			strlen++;		
		}
		
		int r1=sum1%11;
		int r2=sum2%11;
		String s1=String.valueOf(r1);
		if(r1==0)s1="A";
		if(r1==10)s1="B";
		String s2=String.valueOf(r2);
		if(r2==0)s2="X";
		if(r2==10)s2="Y";		
		return s1+s2;
	}
	public static String getATMcode(String str1,String str2,String str3){	
		if(str1.length()!=5 || str2.length()!=8 )
			return null;
		String tmpstr=str1+str2;
		int sum1=0,sum2=0;
		for(int i=0;i<tmpstr.length();i++){
			if(i%2==0)
				sum1+=Integer.parseInt(tmpstr.substring(i, i+1));			
			else
				sum2+=Integer.parseInt(tmpstr.substring(i,i+1));		
			
		}
		int sum=0;
		if(str3.length()>=1)
			sum+=Integer.parseInt(str3.substring(str3.length()-1,str3.length()))*5;
		if(str3.length()>=2)
			sum+=Integer.parseInt(str3.substring(str3.length()-2,str3.length()-1))*4;
		if(str3.length()>=3)
			sum+=Integer.parseInt(str3.substring(str3.length()-3,str3.length()-2))*3;
		if(str3.length()>=4)
			sum+=Integer.parseInt(str3.substring(str3.length()-4,str3.length()-3))*2;
		if(str3.length()>=5)
			sum+=Integer.parseInt(str3.substring(str3.length()-5,str3.length()-4))*3;
		if(str3.length()>=6)
			sum+=Integer.parseInt(str3.substring(str3.length()-6,str3.length()-5))*4;
		if(str3.length()>=7)
			sum+=Integer.parseInt(str3.substring(str3.length()-7,str3.length()-6))*5;
		int r=10-((sum+sum1*3+sum2)%10);
		if(r==10)r=0;
		return str1+str2+String.valueOf(r);
	}
	
	
}
