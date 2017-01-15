package activitydiagram;
import java.util.ArrayList;
import java.util.List;

public class ADMethodInvocation extends ADNode {
	private String parentClass;
	private String methodName;
	private String methodNameWithVars;
	private List<String> paramList;
	private List<String> invocationMethodList; 
	private List<String> varDeclarationList;
	private List<String> paramTypeList;
	private List<String> invocationMethodListWithVars;
	private String returnType;
	private int endposition ; 
	private int startposition;
	private List<Integer> invocationMethodStartPosition;
	
	
	public ADMethodInvocation() {
		super();
		this.setTypeNode("ADMethodInvocation");
		// TODO Auto-generated constructor stub
	}
	public int getEndposition() {
		return endposition;
	}
	public void setEndposition(int endposition) {
		this.endposition = endposition;
	}
	public int getStartposition() {
		return startposition;
	}
	public void setStartposition(int startposition) {
		this.startposition = startposition;
	}
	public String getParentClass() {
		return parentClass;
	}
	public void setParentClass(String parentClass) {
		this.parentClass = parentClass;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getMethodNameWithVars() {
		return methodNameWithVars;
	}
	public void setMethodNameWithVars(String methodNameWithVars) {
		this.methodNameWithVars = methodNameWithVars;
	}
	public List<String> getParamList() {
		return paramList;
	}
	public void setParamList(List<String> paramList) {
		this.paramList = paramList;
	}
	public List<String> getInvocationMethodList() {
		return invocationMethodList;
	}
	public void setInvocationMethodList(List<String> invocationMethodList) {
		this.invocationMethodList = invocationMethodList;
	}
	public List<String> getVarDeclarationList() {
		return varDeclarationList;
	}
	public void setVarDeclarationList(List<String> varDeclarationList) {
		this.varDeclarationList = varDeclarationList;
	}
	public List<String> getParamTypeList() {
		return paramTypeList;
	}
	public void setParamTypeList(List<String> paramTypeList) {
		this.paramTypeList = paramTypeList;
	}
	public List<String> getInvocationMethodListWithVars() {
		return invocationMethodListWithVars;
	}
	public void setInvocationMethodListWithVars(List<String> invocationMethodListWithVars) {
		this.invocationMethodListWithVars = invocationMethodListWithVars;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public List<Integer> getInvocationMethodStartPosition() {
		return invocationMethodStartPosition;
	}
	public void setInvocationMethodStartPosition(List<Integer> invocationMethodStartPosition) {
		this.invocationMethodStartPosition = invocationMethodStartPosition;
	}
		
}
