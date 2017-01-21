package activitydiagram;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
	
	private StringBuilder responseInstructions = new StringBuilder( "" );
	private StringBuilder responseValidate = new StringBuilder( "" );

	
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
			Map<Integer, ADInstruction> tmphashInsObj;
			if( hashInstructionsList.containsKey(key) ){
				tmphashInsObj = hashInstructionsList.get(key);
			}
			else{
				tmphashInsObj = new TreeMap<Integer, ADInstruction>();
			}
			
			for( Integer keypos : condition.keySet()){
				ADCondition adCondition = condition.get(keypos);
				Map<Integer, ADNode> tmphash = hashNodes.get(key);//get Exsisteing Hash
				
				int posIf = condition.get(keypos).getStartPosition();
				tmphash.put(posIf, adCondition);
				
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
		
		//Add Init and End
		addInitAndEndNodes();
	    addSourcesToHashInstructions();
	}

	private void addInitAndEndNodes() {
		for( String key : hashInstructionsList.keySet()){
			TreeMap<Integer, ADInstruction> tmphashInsObj = new TreeMap<Integer, ADInstruction>( hashInstructionsList.get(key) );
			Integer lastpos = 0;
			if( tmphashInsObj.size() > 0 ){
				ADInstruction tmpInstructionInit = new ADInstruction("init-"+ key, "init-"+ key, "init", 1, -1);
				tmphashInsObj.put(1, tmpInstructionInit);//Init Node
			    hashNodes.get(key).put(1, tmpInstructionInit);
			    hashInstructionsList.put(key, tmphashInsObj);
			    
			    lastpos = 0;
			    for( Integer keypos : tmphashInsObj.keySet()){
			    	lastpos = keypos;
			    }
			    
			    ADInstruction lastInstruction = hashInstructionsList.get(key).get(lastpos);
			    ADInstruction tmpInstructionFinal = new ADInstruction("final-" + key, "final" + key, "final", lastpos + 1, -1);
			    tmpInstructionFinal.setSource(lastInstruction.getPosition());
			    tmphashInsObj.put(lastpos + 1, tmpInstructionFinal);//Final Node
			    
			    hashNodes.get(key).put(lastpos + 1, tmpInstructionFinal);
			    hashInstructionsList.put(key, tmphashInsObj);
			}
		}
	}
	
	public ADMethodInvocation getMethodInvocationByHashInstruction(ADInstruction hashInstruction){
		return hashInvocationMethods.get(hashInstruction.getInstructionKey());
	}

	private void addSourcesToHashInstructions() {
		//Add sources
		for( String keyclassmethod : this.hashInstructionsList.keySet()){
			Map<Integer, ADInstruction> hashInstructionsObjOrdered = new TreeMap<Integer, ADInstruction>(hashInstructionsList.get(keyclassmethod));
			//get Existing Hash
			int lastElsePos = 0;
			Integer lastsource = -1;
			ADInstruction lastInstruction = null;
			
			
			int i = 0;
			for( Integer pos : hashInstructionsObjOrdered.keySet()){
				ADInstruction instruction = hashInstructionsObjOrdered.get(pos);
				if(i == 1) 
					lastsource = 1;//Set position Initial Node
				
				instruction.setSource(lastsource);
				
				if( lastInstruction != null ){//First then
					if( lastInstruction.getTypeNode().equals("if") ){
						ADInstructionIf ifParent = (ADInstructionIf) lastInstruction;
						instruction.setFirstThen(true);
						ifParent.setFirstPosThen(instruction.getPosParent());
					}
				}
				if( instruction.isFirstElse ){//First then
					instruction.setSource(instruction.getPosParent());
				}
				if( instruction.isLastElse ){//last then
					lastElsePos = instruction.getPosition();
				}
				//Important to set last then
				if( instruction.getFlotType().equals("else") ){
					ADInstructionIf lastelseParent = (ADInstructionIf) hashInstructionsObjOrdered.get(instruction.getPosParent());
					if( lastelseParent != null){
						lastelseParent.setLastPosThen(lastsource);
					}
				}
				else if( instruction.getTypeNode().equals("merge") ){
					ADInstructionIf ifParent = (ADInstructionIf) hashInstructionsObjOrdered.get(instruction.getPosParent());
					instruction.setSource(-1);//Source
					
					if( ifParent.getLastPosThen() == lastsource ){
						instruction.addListSources(ifParent.getLastPosThen());
						instruction.addListSources(instruction.getPosParent());
					}
					else if(  ifParent.getLastPosElse() != -1 ){// Else
						instruction.addListSources(ifParent.getLastPosThen());
						instruction.addListSources(lastsource);
						//instruction.addListSources(ifParent.getLastPosElse());
					}
					else{//Not Else
						instruction.addListSources(ifParent.getLastPosThen());
						instruction.addListSources(lastsource);
					}
					//Restart vars
					//lastThenPos = 0;
					lastElsePos = 0;
				}
				if(instruction != null)
					lastsource = instruction.getPosition();
				
				lastInstruction = instruction;
				i++;
			}
		}
	}
	
	private void printToHashInstructions() {
		responseInstructions.append("---------- printToHashInstructions() -----------------\n");
		System.out.println("---------- printToHashInstructions() -----------------");
		for( String keyclassmethod : this.hashInstructionsList.keySet()){
			Map<Integer, ADInstruction> hashInstructionsObjOrdered = new TreeMap<Integer, ADInstruction>(hashInstructionsList.get(keyclassmethod));
			//get Existing Hash
			System.out.println(" - " + keyclassmethod);
			responseInstructions.append(" - " + keyclassmethod + "\n");
			for( Integer pos : hashInstructionsObjOrdered.keySet()){
				ADInstruction instruction = hashInstructionsObjOrdered.get(pos);
				System.out.println("   - [" + pos + "] " + instruction.getTypeNode() + " " + instruction.getFlotType() + " - " + instruction.getDisplayInstruction() + " source: " + instruction.getSource() + " parent:" + instruction.getPosParent() );
				responseInstructions.append("   - [" + pos + "] " + instruction.getTypeNode() + " " + instruction.getFlotType() + " - " + instruction.getDisplayInstruction() + " source: " + instruction.getSource() + " parent:" + instruction.getPosParent() + "\n");
				if( instruction.getTypeNode().equals("merge") ){
					System.out.print(" 	  sourceList :" + instruction.getListsources().toString());
					responseInstructions.append(" 	  sourceList :" + instruction.getListsources().toString() + "\n");
				}
			}
		}
		System.out.println("---------- end printToHashInstructions() -----------------"+ "\n");
		responseInstructions.append("---------- end printToHashInstructions() -----------------"+ "\n");
	}	
	
	public boolean validateClassDiagram()
	{
		boolean diagramok = false;
		if(entryValidated){
			diagramok = true;
			System.out.println("Activity Diagram Validated");
			responseValidate.append("Activity Diagram Validated"+ "\n");
		}
		else{
			System.out.println("Sorry, Entry point (" + entryClass + ":" + entryMethod + "()" +  ")  not found!");
			responseValidate.append("Sorry, Entry point (" + entryClass + ":" + entryMethod + "()" +  ")  not found!");
			//System.exit(0);
		}
		return diagramok;
	}
	
	public void testClassDiagram()
	{
		validateClassDiagram();
		if(entryValidated){
			printToHashInstructions();
		}
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
		    System.out.println("     - params: " +  daMethodInvOb.getMethodNameWithVars());
		    
		    for(String param : daMethodInvOb.getParamList()){
		    	System.out.println("   - param: " + param);
		    }
		    
		    for(String methodInvData : daMethodInvOb.getInvocationMethodList()){
		    	System.out.println("	 - " + methodInvData);
		    }
		    for(int i = 0; i< daMethodInvOb.getInvocationMethodListWithVars().size(); i++){
		    	String methodInvData = daMethodInvOb.getInvocationMethodListWithVars().get(i);
		    	Integer pos = daMethodInvOb.getInvocationMethodStartPosition().get(i);
		    	System.out.println("	   - vars: " + methodInvData + " pos: " + pos);
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
	
	public Map<Integer, ADInstruction> getHashInstructions(String key){
		return hashInstructionsList.get(key);
	}
	
	public Map<String, Map<Integer, ADCondition>> getHashConditions() {
		return hashConditions;
	}

	public void setHashConditions(Map<String, Map<Integer, ADCondition>> hashConditions) {
		this.hashConditions = hashConditions;
	}

	public String getResponseInstructions() {
		return this.responseInstructions.toString();
	}

	public void setResponseInstructions(StringBuilder responseInstructions) {
		this.responseInstructions = responseInstructions;
	}

	public String getResponseValidate() {
		return responseValidate.toString();
	}

	public void setResponseValidate(StringBuilder responseValidate) {
		this.responseValidate = responseValidate;
	}
	
}
