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
	private Map<String, Map<Integer, ADCondition>> hashConditions;//Class-Method, HashMap <pos,ADContidion>
	private Map<String, Map<Integer, ADNode>> hashNodes;//Class-Method, HashMap <pos,ADNode>
	private Map<String, Map<Integer, ADInstruction>> hashInstructionsList;
	
	private List<TypeDeclaration> listClasses;
	private MethodDeclaration entryMethodObj;
	private String keyEntryPoint = "";
	
	public ActivityDiagramAst(HashMap<String, ADMethodInvocation> listInvocationMethods, 
			Map<String, Map<Integer, ADCondition>> hashConditions, 
			List<TypeDeclaration> listClasses, 
			String entryClass, 
			String entryMethod) 
	{
		this.entryClass = entryClass;
		this.entryMethod = entryMethod;
		this.hashInvocationMethods = listInvocationMethods;
		this.hashConditions = hashConditions;
		this.listClasses = listClasses;
		this.entryMethodObj = getEntryPoint();
		this.hashNodes = new HashMap<String, Map<Integer, ADNode>>();
		mergeInvocationsAndConditions();
		//this.hashConditions = new HashMap<String, HashMap<Integer, ADCondition>>();
		//setMethodHashMap();
	}
	
	public Map<String, Map<Integer, ADNode>> getHashNodes() {
		return hashNodes;
	}

	public Map<String, Map<Integer, ADInstruction>> getHashInstructionsList() {
		return hashInstructionsList;
	}

	public String getKeyEntryPoint() {
		return keyEntryPoint;
	}

	public void setEntryClass(String entryClass) {
		this.entryClass = entryClass;
	}

	public void setEntryMethod(String entryMethod) {
		this.entryMethod = entryMethod;
	}

	public void setEntryValidated(boolean entryValidated) {
		this.entryValidated = entryValidated;
	}

	public void setHashClassesMethods(HashMap<String, MethodDeclaration[]> hashClassesMethods) {
		this.hashClassesMethods = hashClassesMethods;
	}

	public void setHashInvocationMethods(HashMap<String, ADMethodInvocation> hashInvocationMethods) {
		this.hashInvocationMethods = hashInvocationMethods;
	}

	public void setHashNodes(Map<String, Map<Integer, ADNode>> hashNodes) {
		this.hashNodes = hashNodes;
	}

	public void setHashInstructionsList(Map<String, Map<Integer, ADInstruction>> hashInstructionsList) {
		this.hashInstructionsList = hashInstructionsList;
	}

	public void setListClasses(List<TypeDeclaration> listClasses) {
		this.listClasses = listClasses;
	}

	public void setEntryMethodObj(MethodDeclaration entryMethodObj) {
		this.entryMethodObj = entryMethodObj;
	}

	public void setKeyEntryPoint(String keyEntryPoint) {
		this.keyEntryPoint = keyEntryPoint;
	}


	private void mergeInvocationsAndConditions() {
		hashInstructionsList = new TreeMap<String, Map<Integer, ADInstruction>>();
		
		for( String key : this.hashInvocationMethods.keySet()){
			ADMethodInvocation methodinv = this.hashInvocationMethods.get(key);
			HashMap<Integer, ADNode> tmphash = new HashMap<Integer, ADNode>();
			tmphash.put(methodinv.getStartposition(), methodinv);
			
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
			TreeMap<Integer, ADCondition> condition = new TreeMap<Integer, ADCondition>( hashConditions.get(key) );
			Map<Integer, ADInstruction> tmphashInsObj = new TreeMap<Integer, ADInstruction>();
			for( Integer keypos : condition.keySet()){
				ADCondition adCondition = condition.get(keypos);
				Map<Integer, ADNode> tmphash = hashNodes.get(key);//get Exsisteing Hash
				
				//System.out.println("	--  key :" + key + " key2:" + key2 + " Condition -> "  + adCondition.getConditionExpression() + " parent:" + adCondition.getStartParentPosition());
				int posIf = condition.get(keypos).getStartPosition();
				tmphash.put(posIf, adCondition);
				
				//
				//System.out.println("	- ifcond: (" + adCondition.getConditionExpression() + ") pos:" + keypos);
				ADInstructionIf tmpInstruction = new ADInstructionIf(adCondition.getConditionExpression(), adCondition.getConditionExpression(), "if", keypos, -1);
			    
				int iThen = 0;
				int lastThenPos = 0;
				int firstThenPos = 0;
				TreeMap<Integer, String> colThenStatements = new TreeMap<Integer, String>(adCondition.getThenStatements());//order
				for(Integer posThen : colThenStatements.keySet()){
					iThen++;
					String thenInstruction = adCondition.getThenStatements().get(posThen);
					ADInstruction tmpInstruction2 = new ADInstruction(thenInstruction, adCondition.getThenStatementsWithVars().get(posThen), "call", posThen, -1);
					tmpInstruction2.setFlotType("then");
					//System.out.println("	- varscond: " + thenInstruction + " pos:" + posThen + "	thenInstruction - parentif: " + adCondition.getPosParent() );
					if(iThen == 1){
						firstThenPos = posThen;
						tmpInstruction2.setPosParent(posIf);
						tmpInstruction2.setFirstThen(true);
						tmpInstruction.setLastPosThen(tmpInstruction2.getPosition());
					}
					else if(iThen >= adCondition.getElseStatements().size()){
						tmpInstruction2.setLastThen(true);
					}
					lastThenPos = posThen;
					tmphashInsObj.put(posThen, tmpInstruction2);
				}
				int iElse = 0;
				int lastElsePos = 0;
				int firstElsePos = 0;
				TreeMap<Integer, String> colElseStatements = new TreeMap<Integer, String>(adCondition.getElseStatements());//order
				for(Integer posElse : colElseStatements.keySet()){
					iElse++;
					String elseInstruction = colElseStatements.get(posElse);
					ADInstruction tmpInstruction2 = new ADInstruction(elseInstruction, adCondition.getElseStatementsWithVars().get(posElse), "call", posElse, -1);
					tmpInstruction2.setFlotType("else");
					if(iElse == 1){
						firstElsePos = posElse;
						tmpInstruction2.setPosParent(posIf);
						tmpInstruction2.setFirstElse(true);
						tmpInstruction.setLastPosElse(tmpInstruction2.getPosition());
					}
					if(iElse >= adCondition.getElseStatements().size()){
						tmpInstruction2.setLastElse(true);
					}
					lastElsePos = posElse;
					tmphashInsObj.put(posElse, tmpInstruction2);
				}
				tmpInstruction.setFirstPosThen(firstThenPos);
				tmpInstruction.setLastPosThen(lastThenPos);
				tmpInstruction.setFirstPosThen(firstElsePos);
				tmpInstruction.setLastPosElse(lastElsePos);
				tmpInstruction.setMergePos(adCondition.getEndPosition());
				
				ADInstruction tmpInstruction3 = new ADInstruction("mergeNode ("+ adCondition.getConditionExpression() + ") ", "mergeNode ("+ adCondition.getConditionExpression() + ") ", "merge", adCondition.getEndPosition(), adCondition.getStartPosition());
				tmphashInsObj.put(adCondition.getEndPosition(), tmpInstruction3);
				tmphashInsObj.put(keypos, tmpInstruction);
				hashInstructionsList.put(key, tmphashInsObj);
				hashNodes.put(key, tmphash);
			}
		}
		
		addSourcesToHashInstructions();
		
	}
	
	public ADMethodInvocation getMethodInvocationByHashInstruction(ADInstruction hashInstruction){
		return hashInvocationMethods.get(hashInstruction.getInstructionKey());
	}

	private void addSourcesToHashInstructions() {
		//Add sources
		for( String keyclassmethod : this.hashInstructionsList.keySet()){
			System.out.println(" * " + keyclassmethod);
			Map<Integer, ADInstruction> hashInstructionsObjOrdered = new TreeMap<Integer, ADInstruction>(hashInstructionsList.get(keyclassmethod));
			//get Existing Hash
			int lastThenPos = 0;
			int lastElsePos = 0;
			Integer lastsource = 0;
			ADInstruction lastInstruction = null;
			for( Integer pos : hashInstructionsObjOrdered.keySet()){
				ADInstruction instruction = hashInstructionsObjOrdered.get(pos);
				instruction.setSource(lastsource);
				System.out.println(" 	  instruction :" + instruction.getDisplayInstruction() + " " + instruction.getPosition() + " - " + lastsource);
				
				if( lastInstruction != null ){//First then
					if( lastInstruction.getTypeNode().equals("if") ){
						ADInstructionIf ifParent = (ADInstructionIf) lastInstruction;
						instruction.setFirstThen(true);
						ifParent.setFirstPosThen(instruction.getPosParent());
					}
				}
				if( instruction.isFirstElse ){//First then
					instruction.setSource(instruction.getPosParent());
					
					//ADInstructionIf ifParent = (ADInstructionIf) hashInstructionsObjOrdered.get(instruction.getPosParent());
					//ifParent.setFirstPosElse(instruction.getPosition());
				}
				if( instruction.isLastThen ){//last then
					lastThenPos = instruction.getPosition();
					
					//ADInstructionIf ifParent = (ADInstructionIf) hashInstructionsObjOrdered.get(instruction.getPosParent());
					//ifParent.setLastPosThen(instruction.getPosition());
				}
				if( instruction.isLastElse ){//last then
					lastElsePos = instruction.getPosition();
					
					//ADInstructionIf ifParent = (ADInstructionIf) hashInstructionsObjOrdered.get(instruction.getPosParent());
					//ifParent.setLastPosElse(instruction.getPosition());
				}
				System.out.println("	- (" + instruction.getFlotType() + ") pos:" + instruction.getPosition());
				
				//Important to set last then
				if( instruction.getFlotType().equals("else") ){
					ADInstructionIf lastelseParent = (ADInstructionIf) hashInstructionsObjOrdered.get(instruction.getPosParent());
					System.out.println("		- (" + lastelseParent.getPosition() + " = "+ lastsource);
					lastelseParent.setLastPosThen(lastsource);
				}
				else if( instruction.getTypeNode().equals("merge") ){
					//ADInstructionIf lastifParent = (ADInstructionIf) hashInstructionsObjOrdered.get(hashInstructionsObjOrdered.get(lastsource).getPosParent());
					//lastifParent.setFirstPosElse(lastsource);
					
					ADInstructionIf ifParent = (ADInstructionIf) hashInstructionsObjOrdered.get(instruction.getPosParent());
					instruction.setSource(0);//Source
					
					if(lastElsePos != 0){// Else
						instruction.addListSources(ifParent.getLastPosThen());
						instruction.addListSources(ifParent.getLastPosElse());
					}
					else{//Not Else
						instruction.addListSources(ifParent.getLastPosThen());
						instruction.addListSources(instruction.getPosParent());
					}
					
					//Restart vars
					//lastThenPos = 0;
					lastElsePos = 0;
				}
				
				if(instruction != null)
					lastsource = instruction.getPosition();
				
				
				
				lastInstruction = instruction;
				
			}
		}
	}
	
	private void printToHashInstructions() {
		//Add sources
		for( String keyclassmethod : this.hashInstructionsList.keySet()){
			Map<Integer, ADInstruction> hashInstructionsObjOrdered = new TreeMap<Integer, ADInstruction>(hashInstructionsList.get(keyclassmethod));
			//get Existing Hash
			for( Integer pos : hashInstructionsObjOrdered.keySet()){
				ADInstruction instruction = hashInstructionsObjOrdered.get(pos);
				System.out.println("	+pos:" + pos + " " + instruction.getTypeNode() + " " + instruction.getFlotType() + " - " + instruction.getDisplayInstruction() + " source: " + instruction.getSource() + " parent:" + instruction.getPosParent() );
				if( instruction.getTypeNode().equals("merge") ){
					System.out.println(" 	  sourceList :" + instruction.getListsources().toString());
				}
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
		printToHashInstructions();
		//printListInvocationMethods(hashInvocationMethods);
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
	
	public Map<Integer, ADInstruction> getMainHashActivityInstructions(){
		//keyEntryPoint
		return getMainHashInstructions(keyEntryPoint);
	}
	
	public Map<Integer, ADInstruction> getMainHashInstructions(String key){
		// Map<String, Map<Integer, ADInstruction>> hashInstructionsList
		return hashInstructionsList.get(key);
	}
	
	private void processEntryMethod()
	{	
		String content = entryMethodObj.getBody().toString();
		System.out.println("Body " + content);
		//CompilationUnit parse = parse(content.toCharArray());
		//printMethodInvocationInfo(parse);
		
	}

	public Map<String, Map<Integer, ADCondition>> getHashConditions() {
		return hashConditions;
	}

	public void setHashConditions(Map<String, Map<Integer, ADCondition>> hashConditions) {
		this.hashConditions = hashConditions;
	}
	
}
