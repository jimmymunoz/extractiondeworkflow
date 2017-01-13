package activitydiagram;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.*;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLMapImpl;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.TypedElement;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.VisibilityKind;
import org.eclipse.uml2.uml.internal.impl.OperationImpl;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.DataType;

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
	
	public static Class getClassByName(String className, Package packagesrc){
		Class resultClass = null;
		EList<PackageableElement> packageList = packagesrc.getPackagedElements();
		
		for(PackageableElement tmpElement : packageList ){
			//System.out.println("tmpElement: " + tmpElement.getName());
			if( tmpElement instanceof Class ){
				//System.out.println("tmpElement is class");
				if(tmpElement.getName().equals(className) ){
					System.out.println("Class: " + tmpElement.getName()); 
					resultClass = (Class) tmpElement;
					
				}
			}
			if( resultClass != null ){
				return resultClass;
			}
			else if( tmpElement instanceof Package ){
				//System.out.println("tmpElement is Package");
				resultClass = getClassByName(className, (Package) tmpElement);
			}
		}
		return resultClass;
		
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
		    System.out.println("  key: " + key);
		    System.out.println("     - params: " +  daMethodInvOb.getMethodNameWithVars());
		    /*
		    for(String param : daMethodInvOb.getParamList()){
		    	System.out.println("   - param: " + param);
		    }
		    */
		    for(String methodInvData : daMethodInvOb.getInvocationMethodList()){
		    	System.out.println("	 - " + methodInvData);
		    }
		    for(String methodInvData : daMethodInvOb.getInvocationMethodListWithVars()){
		    	System.out.println("	   - vars: " + methodInvData);
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
