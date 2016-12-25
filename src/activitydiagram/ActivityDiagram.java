package activitydiagram;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ActivityDiagram 
{
	private String entryClass;
	private String entryMethod;
	private boolean entryValidated = false;
	
	private HashMap<String, MethodDeclaration[]> hashClassesMethods;////Method, List Methods
	private HashMap<String, ADMethodInvocation> hashInvocationMethods;//Class-Method, List DAMethodInvocation
	private List<TypeDeclaration> listClasses;
	private MethodDeclaration entryMethodObj;
	
	public ActivityDiagram(HashMap<String, ADMethodInvocation> listInvocationMethods, List<TypeDeclaration> listClasses, String entryClass, String entryMethod) 
	{
		this.entryClass = entryClass;
		this.entryMethod = entryMethod;
		this.hashInvocationMethods = listInvocationMethods;
		this.listClasses = listClasses;
		this.entryMethodObj = getEntryPoint();
		//setMethodHashMap();
	}
	
	public void validateClassDiagram()
	{
		if(entryValidated){
			System.out.println("Activity Diagram Validated");
		}
		else{
			System.out.println("Sorry, Entry point (" + entryClass + ":" + entryMethod + "()" +  ")  not found!");
		}
	}
	
	public void testClassDiagram()
	{
		validateClassDiagram();
		printListInvocationMethods(hashInvocationMethods);
		//MethodDeclaration entryMethod = getEntryPoint(hashClassesMethods);
		
	}
	
	private MethodDeclaration getEntryPoint() {
		for (TypeDeclaration classOb : listClasses) {
			for (MethodDeclaration method : classOb.getMethods()) {
				if( entryClass.equals(classOb.getName().toString()) && entryMethod.equals(method.getName().toString()) ){
					System.out.println("Entry Method name: " + method.getName() + " Return type: " + method.getReturnType2());
					entryValidated = true;
					entryMethodObj = method;
					break;
				}
			}
		}
		return entryMethodObj;
	}
	
	
	private void setMethodHashMap()
	{
		//List<TypeDeclaration> listClasses = getClassList(parse);
		for (TypeDeclaration classOb : listClasses) {
			String key = "" + classOb.getName().toString();
			hashClassesMethods.put(key, classOb.getMethods() );
		}
	}
	
	private void printListInvocationMethods(HashMap<String, ADMethodInvocation> listInvocationMethods)
	{
		System.out.println("printListInvocationMethods ");
		for (Map.Entry<String, ADMethodInvocation> entry : listInvocationMethods.entrySet()) {
		    String key = entry.getKey();
		    ADMethodInvocation daMethodInvOb = entry.getValue();
		    System.out.println("	key: " + key);
		    for(String methodInvData : daMethodInvOb.getInvocationMethodList()){
		    	System.out.println("		" + methodInvData);
		    }
		}
		System.out.println("end printListInvocationMethods ");
	}
	
	private void processEntryMethod()
	{	
		String content = entryMethodObj.getBody().toString();
		System.out.println("Body " + content);
		/*
		CompilationUnit parse = parse(content.toCharArray());
		printMethodInvocationInfo(parse);
		*/
	}
	
}
