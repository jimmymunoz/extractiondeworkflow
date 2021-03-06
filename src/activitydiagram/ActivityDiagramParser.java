package activitydiagram;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;

import visitors.IfStatementVisitor;
import visitors.MethodDeclarationVisitor;
import visitors.MethodInvocationVisitor;
import visitors.SingleVariableDeclarationVisitor;
import visitors.StructuralPropertyVisitor;
import visitors.TypeDeclarationVisitor;
import visitors.VariableDeclarationFragmentVisitor;

public class ActivityDiagramParser 
{
	private String projectPath = "";
	private String projectSourcePath = projectPath + "";
	private String jrePath = "";
	private String entryClass = "";
	private String entryMethod = "";
	private ActivityDiagramAst activityDiagram;
	
	public ActivityDiagramParser(String projectPath, String projectSourcePath, String jrePath, String entryClass, String entryMethod) 
	{
		this.projectPath = projectPath;
		this.projectSourcePath = projectSourcePath;
		this.jrePath = jrePath;
		this.entryClass = entryClass;
		this.entryMethod = entryMethod;
	}

	
	public ActivityDiagramAst parseActivityDiagram() throws IOException
	{
		CompilationUnit parse = getCompilationUnitByDir();
		List<TypeDeclaration> listClasses = getClassList(parse);
		
		HashMap<String, ADMethodInvocation> listInvocationMethods = getListInvocationMethods(parse);
		Map<String, Map<Integer, ADCondition>> hashConditions = getHashConditions(parse);
		//printHashCondition(hashConditions);
		activityDiagram = new ActivityDiagramAst(listInvocationMethods, hashConditions, listClasses, entryClass, entryMethod);
		activityDiagram.testClassDiagram();
		return activityDiagram;
	}
	
	private Map<String, Map<Integer, ADCondition>> getHashConditions(CompilationUnit parse) {
		Map<String, Map<Integer, ADCondition>> hashConditions = new HashMap<String, Map<Integer, ADCondition>>();
		List<TypeDeclaration> listClasses = getClassList(parse);// ?
		for (TypeDeclaration classOb : listClasses) {
			for (MethodDeclaration method : classOb.getMethods()) {
				String className = classOb.getName().toString();
				IfStatementVisitor visitor = new IfStatementVisitor();
				visitor.setClassName(className);
				method.accept(visitor);
				HashMap<Integer, ADCondition> tmpHashCondition = visitor.getHashConditionsClass();
				String methodKey = getActMethodName(method, className, method.getName().toString());
				hashConditions.put(methodKey, tmpHashCondition);
			}
		}
		return hashConditions;
	}

	public void printHashCondition(Map<String, Map<Integer, ADCondition>> hashConditions)
	{
		for(String key : hashConditions.keySet()){
			HashMap<Integer, ADCondition> tmpHash = (HashMap<Integer, ADCondition>) hashConditions.get(key);
			for(Integer key2 : tmpHash.keySet()){
				ADCondition adCondition = tmpHash.get(key2);
				System.out.println("	--  key :" + key + " key2:" + key2 + " Condition -> "  + adCondition.getConditionExpression() + " parent:" + adCondition.getStartParentPosition());
			}
		}
	}

	public static List<TypeDeclaration> getClassList(CompilationUnit parse)
	{
		TypeDeclarationVisitor visitor = new TypeDeclarationVisitor();
		parse.accept(visitor);
		return visitor.getListTypeDeclaration();
	}
	
	public MethodDeclaration[] getMethodsByClass(TypeDeclaration classOb)
	{
		return classOb.getMethods();
	}
	
	public CompilationUnit getCompilationUnitByDir() throws IOException
	{
		// read java files
		final File folder = new File(projectSourcePath);
		ArrayList<File> javaFiles = listJavaFilesForFolder(folder);
		StringBuilder sb = new StringBuilder();
		for (File fileEntry : javaFiles) {
			String content = FileUtils.readFileToString(fileEntry);
			sb.append(content); //
		}
		CompilationUnit parse = parse(sb.toString().toCharArray());
		return parse;
	}
	
	

	// read all java files from specific folder
	public static ArrayList<File> listJavaFilesForFolder(final File folder)
	{
		ArrayList<File> javaFiles = new ArrayList<File>();
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				javaFiles.addAll(listJavaFilesForFolder(fileEntry));
			} 
			else if (fileEntry.getName().contains(".java")) {
				System.out.println("		File added: " + fileEntry.getName());
				javaFiles.add(fileEntry);
			}
		}
		return javaFiles;
	}

	// create AST
	private CompilationUnit parse(char[] classSource) 
	{
		ASTParser parser = ASTParser.newParser(AST.JLS4); // java +1.6
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
 
		parser.setBindingsRecovery(true);
 
		Map options = JavaCore.getOptions();
		parser.setCompilerOptions(options);
 
		parser.setUnitName("");
 
		String[] sources = { projectSourcePath }; 
		String[] classpath = {jrePath};
 
		parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true);
		parser.setSource(classSource);
		
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		 
		if (cu.getAST().hasBindingsRecovery()) {
			//System.out.println("Binding activated.");
		}
		return cu; // create and parse
	}
	
	
	
	// navigate method information
	public void printMethodInfo(CompilationUnit parse) 
	{
		MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
		parse.accept(visitor);

		for (MethodDeclaration method : visitor.getMethods()) {
			System.out.println("Method name: " + method.getName() + " Return type: " + method.getReturnType2());
			printIfStatement(method);
		}
	}

	// navigate variables inside method
	public void printVariableInfo(CompilationUnit parse) 
	{
		
		MethodDeclarationVisitor visitor1 = new MethodDeclarationVisitor();
		parse.accept(visitor1);
		for (MethodDeclaration method : visitor1.getMethods()) {

			VariableDeclarationFragmentVisitor visitor2 = new VariableDeclarationFragmentVisitor();
			method.accept(visitor2);

			for (VariableDeclarationFragment variableDeclarationFragment : visitor2
					.getVariables()) {
				System.out.println("variable name: "
						+ variableDeclarationFragment.getName()
						+ " variable =: " + variableDeclarationFragment.getInitializer());
			}

		}
	}
	
	
	public static ArrayList<String> getVariableDeclaration(ASTNode method)
	{
		ArrayList<String> varDecList = new ArrayList<String>();
		VariableDeclarationFragmentVisitor visitor2 = new VariableDeclarationFragmentVisitor();
		method.accept(visitor2);

		for (VariableDeclarationFragment variableDeclarationFragment : visitor2.getVariables()) {
			String tmp = "" //+ variableDeclarationFragment.getInitializer().get + " "
					+ variableDeclarationFragment.getInitializer()
					+ " ->" +  variableDeclarationFragment.getName();
			varDecList.add(tmp);
		}
		return varDecList;
	}
	
	
	public static ArrayList<String> getSingleVariableDeclarationType(ASTNode method)
	{
		ArrayList<String> paramTypeList = new ArrayList<String>();
		SingleVariableDeclarationVisitor visitor2 = new SingleVariableDeclarationVisitor();
		method.accept(visitor2);

		for (SingleVariableDeclaration singleVariableDeclaration : visitor2.getPrameters()) {
			String tmp = singleVariableDeclaration.getType() + "";
			paramTypeList.add(tmp);
		}
		return paramTypeList;
	}
	
	public static ArrayList<String> getSingleVariableDeclaration(ASTNode method)
	{
		ArrayList<String> paramList = new ArrayList<String>();
		SingleVariableDeclarationVisitor visitor2 = new SingleVariableDeclarationVisitor();
		method.accept(visitor2);

		for (SingleVariableDeclaration singleVariableDeclaration : visitor2.getPrameters()) {
			String tmp = singleVariableDeclaration.getType() + " " + singleVariableDeclaration.getName();
			paramList.add(tmp);
		}
		return paramList;
	}
	
	
	
	// navigate method invocations inside method
	public void printMethodInvocationInfo(CompilationUnit parse) 
	{
		MethodDeclarationVisitor visitor1 = new MethodDeclarationVisitor();
		parse.accept(visitor1);
		for (MethodDeclaration method : visitor1.getMethods()) {
			getMethodInvocationList(method, "");
		}
	}
	
	public static ArrayList<String> getArgumentsByMethodInvocation(MethodInvocation node)
	{
		ArrayList<String> argumentsList = new ArrayList<String>();
		List<Expression> listExpr = node.arguments();
		for(Expression expression : listExpr){
			ITypeBinding typeBinding = expression.resolveTypeBinding();
	        String typeBindingName = "";
			if (typeBinding != null) {
				typeBindingName = typeBinding.getName() + " ";
	        }
			String tmp = typeBindingName + "" + expression.toString();
    		argumentsList.add(tmp);
    	}
		return argumentsList;
	}
	
	public static ArrayList<String> getArgumentsTypeByMethodInvocation(MethodInvocation node)
	{
		ArrayList<String> argumentsListType = new ArrayList<String>();
		List<Expression> listExpr = node.arguments();
		for(Expression expression : listExpr){
			ITypeBinding typeBinding = expression.resolveTypeBinding();
	        String typeBindingName = "";
			if (typeBinding != null) {
				typeBindingName = typeBinding.getName();
	        }
			String tmp = typeBindingName; //+ "" + expression.toString();
    		argumentsListType.add(tmp);
    	}
		return argumentsListType;
	}
	
	public static ArrayList<String> getMethodInvocationList(MethodDeclaration method, String className) 
	{
		ArrayList<String> invocationMethodList = new ArrayList<String>();
		MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
		method.accept(visitor2);
		
		for (MethodInvocation node : visitor2.getMethods()) {
			String methodNameWithVars = getMethodNameByMethodInvocation(node);
			invocationMethodList.add(methodNameWithVars);
		}
		return invocationMethodList;
	}


	public static String getMethodNameByMethodInvocation(MethodInvocation node) {
		String expresion = "";
		Expression expression = node.getExpression();
		
		if (expression != null) {
			expresion = expression.toString();//Var name
			ITypeBinding typeBinding = expression.resolveTypeBinding();
		    if (typeBinding != null) {
		    
		    }
		}
		String typeName = "";
		IMethodBinding binding = node.resolveMethodBinding();
		ITypeBinding returnType = null;
		if (binding != null) {
			returnType = binding.getReturnType();
			ITypeBinding type = binding.getDeclaringClass();
		    if (type != null) {
		    	typeName = "" + type.getName();
		    }
		}
		String returnTypeStr= "";
		if(returnType != null){
			returnTypeStr = returnType.getName();
		}
		String methodNameWithVars = getActMethodName(node, typeName, node.getName().toString(), returnTypeStr);//expression -> s.xxxx()
		return methodNameWithVars;
	}
	
	public static ArrayList<String> getMethodInvocationListWithVars(MethodDeclaration method, String className) 
	{
		ArrayList<String> invocationMethodList = new ArrayList<String>();
		MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
		method.accept(visitor2);
		for (MethodInvocation node : visitor2.getMethods()) {
			String methodNameWithVars = getMethodNameWithVarsByMethodInvocation(node);
			invocationMethodList.add(methodNameWithVars);
		}
		return invocationMethodList;
	}


	public static String getMethodNameWithVarsByMethodInvocation(MethodInvocation node) {
		String expresion = "";
		Expression expression = node.getExpression();
		//node.
		if (expression != null) {
			expresion = expression.toString();//Var name
			//System.out.println("		+expresion " + expresion);
			ITypeBinding typeBinding = expression.resolveTypeBinding();
		    if (typeBinding != null) {
		    	//System.out.println("	^^^" + className + "." + method.getName() + "() -> " + node.getName().toString() + "():  Type: " + typeBinding.getName());
		    	/*
		    	 ITypeBinding[] variableBindingList = typeBinding.getTypeArguments();
		    	for(ITypeBinding var : variableBindingList){
		    		System.out.println("		-----  " + var.getName() );
		    	}
		    	*/
		    	//System.out.println("		+++ " + typeBinding.getBinaryName() );
		    	/*
		    	ITypeBinding[] params = typeBinding.getTypeParameters();
		    	for(ITypeBinding param : params){
		    		System.out.println("		+++ " + param.getDeclaredModifiers() );
		    	}
		    	*/
		        //methodInvData += typeBinding.getName() + " ";
		    }
		    //methodInvData += typeBinding.getName() + " " + expression.toString();
		}
		
		String typeName = "";
		IMethodBinding binding = node.resolveMethodBinding();
		ITypeBinding returnType = null;
		if (binding != null) {
			returnType = binding.getReturnType();
			ITypeBinding type = binding.getDeclaringClass();
		    if (type != null) {
		    	typeName = "" + type.getName();
		    }
		}
		String returnTypeStr= "";
		if(returnType != null){
			returnTypeStr = returnType.getName();
		}
		
		
		String methodNameWithVars = getActMethodNameWithVars(node, expresion, node.getName().toString(), returnTypeStr);//expression -> s.xxxx()
		return methodNameWithVars;
	}
	
	public static HashMap<String, ADMethodInvocation> getListInvocationMethods(CompilationUnit parse)
	{
		List<TypeDeclaration> listClasses = getClassList(parse);
		HashMap<String, ADMethodInvocation> listInvocationMethods = new HashMap<String, ADMethodInvocation>();
		for (TypeDeclaration classOb : listClasses) {
			for (MethodDeclaration method : classOb.getMethods()) {
				
				String className = classOb.getName().toString();
				List<String> paramList = getSingleVariableDeclaration(method);
				List<String> paramTypeList = getSingleVariableDeclarationType(method);
				List<String> invocationMethodList = getMethodInvocationList(method, className);
				List<String> invocationMethodListWithVars = getMethodInvocationListWithVars(method, className);
				List<Integer> invocationMethodStartPosition = getPositionsMethodInvocationList(method);
				List<String> varDeclarationList = getVariableDeclaration(method);
				String methodName = getActMethodName(method, className, method.getName().toString());
				String methodNameWithVars = getActMethodNameWithVars(method, className, method.getName().toString());
				String returnType = method.getReturnType2() + "";
		
				ADMethodInvocation  objMethInv = new ADMethodInvocation();
				objMethInv.setInvocationMethodList(invocationMethodList);
				objMethInv.setInvocationMethodListWithVars(invocationMethodListWithVars);
				objMethInv.setMethodName(methodName);
				objMethInv.setMethodNameWithVars(methodNameWithVars);
				objMethInv.setParamList(paramList);
				objMethInv.setParamTypeList(paramTypeList);
				objMethInv.setParentClass(classOb.getName().toString());
				objMethInv.setReturnType(returnType);
				objMethInv.setVarDeclarationList(varDeclarationList);
				objMethInv.setStartposition(method.getStartPosition());
				objMethInv.setInvocationMethodStartPosition(invocationMethodStartPosition);
				
				String key = methodName;
				listInvocationMethods.put(key, objMethInv);
			}
		}
		return listInvocationMethods;
	}
	
	private static List<Integer> getPositionsMethodInvocationList(MethodDeclaration method) {
		List<Integer> listInvPos = new ArrayList<Integer>();
		
		MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
		method.accept(visitor2);
		
		for (MethodInvocation node : visitor2.getMethods()) {
			listInvPos.add(node.getStartPosition());
		}
		return listInvPos;
	}


	public static String getActMethodName(MethodDeclaration method, String className, String methodName )
	{
		List<String> paramTypeList = getSingleVariableDeclarationType(method);
		methodName = "" + className + "." +  methodName + "(" + String.join(",",paramTypeList) + ")" + ":" + method.getReturnType2();
		return methodName;
	}
	
	public static String getActMethodName(MethodInvocation node, String className, String methodName, String returnType )
	{
		List<String> argsList = getArgumentsTypeByMethodInvocation(node);
		
		String resultmethodName = (className.isEmpty())? "" : "" + className + "." ;
		resultmethodName +=  methodName + "(" + String.join(",",argsList) + ")" + ":" + returnType;
		return resultmethodName;
	}
	
	public static String getActMethodNameWithVars(MethodDeclaration method, String className, String methodName )
	{
		List<String> paramList = getSingleVariableDeclaration(method);
		methodName = "" + className + "." +  methodName + "(" + String.join(",",paramList) + ")" + ":" + method.getReturnType2();
		return methodName;
	}
	
	public static String getActMethodNameWithVars(MethodInvocation node, String className, String methodName, String returnType )
	{
		List<String> argsList = getArgumentsByMethodInvocation(node);
		
		String resultmethodName = (className.isEmpty())? "" : "" + className + "." ;
		resultmethodName +=  methodName + "(" + String.join(",",argsList) + ")" + ":" + returnType;
		return resultmethodName;
	}
	
	public void printIfStatement(MethodDeclaration methoddeclaration) 
	{
		IfStatementVisitor visitor = new IfStatementVisitor();
		methoddeclaration.accept(visitor);
	}

}


