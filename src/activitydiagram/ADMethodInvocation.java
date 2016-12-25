package activitydiagram;
import java.util.ArrayList;

public class ADMethodInvocation {
	private String parentClass;
	private String methodName;
	private ArrayList<String> paramList;
	private ArrayList<String> invocationMethodList; 
	private ArrayList<String> varDeclarationList; 
	private String returnType;
	
	
	public ADMethodInvocation(String parentClass, String methodName, ArrayList<String> paramList, ArrayList<String> varDeclarationList, ArrayList<String> invocationMethodList,
			String returnType) {
		this.methodName = methodName;
		this.parentClass = parentClass;
		this.paramList = paramList;
		this.varDeclarationList = varDeclarationList;
		this.invocationMethodList = invocationMethodList;
		this.returnType = returnType;
	}
	
	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getParentClass() {
		return parentClass;
	}
	public void setParentClass(String parentClass) {
		this.parentClass = parentClass;
	}
	public ArrayList<String> getParamList() {
		return paramList;
	}
	public void setParamList(ArrayList<String> paramList) {
		this.paramList = paramList;
	}
	public ArrayList<String> getInvocationMethodList() {
		return invocationMethodList;
	}
	public void setInvocationMethodList(ArrayList<String> invocationMethodList) {
		this.invocationMethodList = invocationMethodList;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
		
}
