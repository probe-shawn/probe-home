package xlive.method.user;

import xlive.method.*;

public class xLoginMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		String authorized_object_name=getProperties("authorized-object-name");
		String user_id=getArguments("user-id");
		if(user_id==null || user_id.trim().length()==0){
			setReturnArguments("valid", "false");
			setReturnArguments("why", "user id blank");
			return getServiceContext().doNextProcess(false);
		}
		String hash_code=getProperties("login.hash");
		setArguments("hash", hash_code);
		if(authorized_object_name==null||authorized_object_name.trim().length()==0){
			String[] names=user_id.split("@"); 
			if(names.length>=2)	authorized_object_name="authorized."+names[1];
		}
		if(authorized_object_name==null||authorized_object_name.trim().length()==0){
			authorized_object_name="authorized";
		}
		if(isWebObjectExisted(authorized_object_name)){
			processWebObjectMethod(authorized_object_name, "login");
		}else{
			setReturnArguments("valid", "false");
			setReturnArguments("why", "user id authorized object not found");
			return getServiceContext().doNextProcess(false);
		}
		return getServiceContext().doNextProcess();
	}
}
