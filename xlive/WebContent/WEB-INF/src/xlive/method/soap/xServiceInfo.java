package xlive.method.soap;

import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

public class xServiceInfo {
		private String name="";
		private String targetNamespace="";
		private Map<String,String> namespaceMap;
		private List<xOperationInfo> operations = new ArrayList<xOperationInfo>();
		public xServiceInfo(){}
		public void setName(String value){
			name = value;
		}
		public String getName(){
			return name;
		}
		public String setTargetNamespace(){
			return targetNamespace;
		}
		public void getTargetNamespace(String value){
			targetNamespace = value;
		}
		public void setNamespaceMap(Map<String,String> value){
			namespaceMap=value;
		}
		public Map<String,String> getNamespaceMap(){
			return namespaceMap;
		}
		public void addOperation(xOperationInfo operation){
			operations.add(operation);
		}
		public xOperationInfo getOperation(String operation_name){
			Iterator<xOperationInfo> operations_iterator=operations.iterator();
			while(operations_iterator.hasNext()){
				xOperationInfo operation_info=operations_iterator.next();
				if(operation_name.equals(operation_info.getOperationName()))
					return operation_info;
			}
			return null;
		}
		public List<xOperationInfo> getOperations(){
			return operations;
		}
		public String toString(){
			String string="Service : "+name+"\n";
			string +="targetNamespace : "+targetNamespace+"\n";
			Iterator<xOperationInfo> operations_iterator=operations.iterator();
			while(operations_iterator.hasNext()){
				xOperationInfo operation_info=operations_iterator.next();
				string+=operation_info.toString();
			}
			return string;
		}
}
