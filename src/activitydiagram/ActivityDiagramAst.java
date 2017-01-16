package activitydiagram;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.emf.*;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLMapImpl;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class ActivityDiagramAst 
{
	private String entryClass;
	private String entryMethod;
	private boolean entryValidated = false;
	
	private HashMap<String, MethodDeclaration[]> hashClassesMethods;////Method, List Methods
	private HashMap<String, ADMethodInvocation> hashInvocationMethods;//Class-Method, List DAMethodInvocation
	private HashMap<String, HashMap<Integer, ADCondition>> hashConditions;//Class-Method, HashMap <pos,ADContidion>
	private HashMap<String, HashMap<Integer, ADNode>> hashNodes;//Class-Method, HashMap <pos,ADNode>
	private Map<String, Map<Integer, ADInstruction>> hashInstructionsList;
	
	private List<TypeDeclaration> listClasses;
	private MethodDeclaration entryMethodObj;
	private String keyEntryPoint = "";
	
	
	public ActivityDiagramAst(HashMap<String, ADMethodInvocation> listInvocationMethods,HashMap<String, HashMap<Integer, ADCondition>> hashConditions, List<TypeDeclaration> listClasses, String entryClass, String entryMethod) 
	{
		this.entryClass = entryClass;
		this.entryMethod = entryMethod;
		this.hashInvocationMethods = listInvocationMethods;
		this.hashConditions = hashConditions;
		this.listClasses = listClasses;
		this.entryMethodObj = getEntryPoint();
		this.hashNodes = new HashMap<String, HashMap<Integer, ADNode>>();
		mergeInvocationsAndConditions();
		//this.hashConditions = new HashMap<String, HashMap<Integer, ADCondition>>();
		//setMethodHashMap();
	}

	private void mergeInvocationsAndConditions() {
		hashInstructionsList = new TreeMap<String, Map<Integer, ADInstruction>>();
		
		for( String key : this.hashInvocationMethods.keySet()){
			ADMethodInvocation methodinv = this.hashInvocationMethods.get(key);
			HashMap<Integer, ADNode> tmphash = new HashMap<Integer, ADNode>();
			tmphash.put(methodinv.getStartposition(), methodinv);
			
			//System.out.println(" * " + key);
		    //impresion
			Map<Integer, ADInstruction> tmphashInsObj = new TreeMap<Integer, ADInstruction>();
		    for(int i = 0; i< methodinv.getInvocationMethodListWithVars().size(); i++){
		    	String methodInvData = methodinv.getInvocationMethodList().get(i);
		    	String methodInvDataVars = methodinv.getInvocationMethodListWithVars().get(i);
		    	Integer pos = methodinv.getInvocationMethodStartPosition().get(i);
		    	
		    	ADInstruction tmpInstruction = new ADInstruction(methodInvData, methodInvDataVars, "call", pos, -1);
			    tmphashInsObj.put(pos, tmpInstruction);
			}
		    hashInstructionsList.put(key, tmphashInsObj);
		    this.hashNodes.put(key, tmphash);
			
		}
		for( String key : hashConditions.keySet()){
			HashMap<Integer, ADCondition> condition = hashConditions.get(key);
			Map<Integer, ADInstruction> tmphashInsObj = new TreeMap<Integer, ADInstruction>();
			for( Integer keypos : condition.keySet()){
				if( condition.get(keypos) != null ){//If not contains the key
					ADCondition adCondition = condition.get(keypos);
					HashMap<Integer, ADNode> tmphash = hashNodes.get(key);//get Exsisteing Hash
					
					//System.out.println("	--  key :" + key + " key2:" + key2 + " Condition -> "  + adCondition.getConditionExpression() + " parent:" + adCondition.getStartParentPosition());
					tmphash.put(condition.get(keypos).getStartPosition(), adCondition);
					
					//
					//System.out.println("	- ifcond: (" + adCondition.getConditionExpression() + ") pos:" + keypos);
					ADInstruction tmpInstruction = new ADInstruction(adCondition.getConditionExpression(), adCondition.getConditionExpression(), "if", keypos, -1);
				    tmphashInsObj.put(keypos, tmpInstruction);
					
					for(Integer posThen : adCondition.getThenStatements().keySet()){
						String thenInstruction = adCondition.getThenStatements().get(posThen);
						ADInstruction tmpInstruction2 = new ADInstruction(thenInstruction, adCondition.getThenStatementsWithVars().get(posThen), "call", posThen, -1);
						tmpInstruction2.setFlotType("then");
						tmphashInsObj.put(posThen, tmpInstruction2);
						//System.out.println("	- varscond: " + thenInstruction + " pos:" + posThen + "	thenInstruction - parentif: " + adCondition.getPosParent() );
					}
					for(Integer posElse : adCondition.getElseStatements().keySet()){
						String elseInstruction = adCondition.getElseStatements().get(posElse);
						ADInstruction tmpInstruction2 = new ADInstruction(elseInstruction, adCondition.getElseStatementsWithVars().get(posElse), "call", posElse, -1);
						tmpInstruction2.setFlotType("else");
						tmphashInsObj.put(posElse, tmpInstruction2);
						//System.out.println("	- varscond: " + elseInstruction + " pos:" + posElse + " elseInstruction");
					}
					ADInstruction tmpInstruction3 = new ADInstruction("mergeNode ("+ adCondition.getConditionExpression() + ") ", "mergeNode ("+ adCondition.getConditionExpression() + ") ", "merge", adCondition.getEndPosition(), adCondition.getStartPosition());
					tmphashInsObj.put(adCondition.getEndPosition(), tmpInstruction3);
					hashInstructionsList.put(key, tmphashInsObj);
					hashNodes.put(key, tmphash);
				}
			}
		}
		
		//
		
		for( String keyclassmethod : this.hashNodes.keySet()){
			HashMap<Integer, ADNode> node = this.hashNodes.get(keyclassmethod);
			System.out.println(" * " + keyclassmethod);
			Map<Integer, ADInstruction> hashInstructionsObjOrdered = new TreeMap<Integer, ADInstruction>(hashInstructionsList.get(keyclassmethod));
			//get Exsisteing Hash
			Integer lastsource = -1;
			for( Integer pos : hashInstructionsObjOrdered.keySet()){
				ADInstruction instruction = hashInstructionsObjOrdered.get(pos);
				instruction.setSource(lastsource);
				System.out.println("	+pos:" + pos + " " + instruction.getTypeNode() + " " + instruction.getFlotType() + " - " + instruction.getDisplayInstruction() + " source: " + instruction.getSource() + " parent:" + instruction.getPosParent() );
				/*
				ADNode node2 = node.get(pos);
				
				if( node2 instanceof ADCondition ){
					
				}
				*/
				lastsource = instruction.getPosition();
				
			}
		}
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
	
	public String getEntryClass() {
		return entryClass;
	}

	public String getEntryMethod() {
		return entryMethod;
	}

	public boolean isEntryValidated() {
		return entryValidated;
	}

	public HashMap<String, MethodDeclaration[]> getHashClassesMethods() {
		return hashClassesMethods;
	}

	public HashMap<String, ADMethodInvocation> getHashInvocationMethods() {
		return hashInvocationMethods;
	}

	public List<TypeDeclaration> getListClasses() {
		return listClasses;
	}

	public MethodDeclaration getEntryMethodObj() {
		return entryMethodObj;
	}

	private MethodDeclaration getEntryPoint() {
		for (TypeDeclaration classOb : listClasses) {
			for (MethodDeclaration method : classOb.getMethods()) {
				if( entryClass.equals(classOb.getName().toString()) && entryMethod.equals(method.getName().toString()) ){
					System.out.println("Entry Method name: " + method.getName() + " Return type: " + method.getReturnType2());
					entryValidated = true;
					entryMethodObj = method;
					keyEntryPoint = ActivityDiagramParser.getActMethodName(method, classOb.getName().toString(), method.getName().toString());
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
		    //System.out.println("     - params: " +  daMethodInvOb.getMethodNameWithVars());
		    
		    for(String param : daMethodInvOb.getParamList()){
		    	//System.out.println("   - param: " + param);
		    }
		    
		    for(String methodInvData : daMethodInvOb.getInvocationMethodList()){
		    	//System.out.println("	 - " + methodInvData);
		    }
		    for(int i = 0; i< daMethodInvOb.getInvocationMethodListWithVars().size(); i++){
		    	String methodInvData = daMethodInvOb.getInvocationMethodListWithVars().get(i);
		    	Integer pos = daMethodInvOb.getInvocationMethodStartPosition().get(i);
		    	System.out.println("	   - vars: " + methodInvData + " pos: " + pos);
			}
		    for(String methodInvData : daMethodInvOb.getInvocationMethodListWithVars()){
		    }
		}
		System.out.println("end printListInvocationMethods ");
	}
	
	public ADMethodInvocation getMainActivityInstructions(){
		//keyEntryPoint
		return getActivityInstructions(keyEntryPoint);
	}
	
	public ADMethodInvocation getActivityInstructions(String key){
		return hashInvocationMethods.get(key);
	}
	
	private void processEntryMethod()
	{	
		String content = entryMethodObj.getBody().toString();
		System.out.println("Body " + content);
		//CompilationUnit parse = parse(content.toCharArray());
		//printMethodInvocationInfo(parse);
		
	}

	public HashMap<String, HashMap<Integer, ADCondition>> getHashConditions() {
		return hashConditions;
	}

	public void setHashConditions(HashMap<String, HashMap<Integer, ADCondition>> hashConditions) {
		this.hashConditions = hashConditions;
	}
	
}
