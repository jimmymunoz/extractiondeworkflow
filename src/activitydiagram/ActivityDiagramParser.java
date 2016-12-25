package activitydiagram;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.internal.utils.FileUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
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

import visitors.MethodDeclarationVisitor;
import visitors.MethodInvocationVisitor;
import visitors.SingleVariableDeclarationVisitor;
import visitors.TypeDeclarationVisitor;
import visitors.VariableDeclarationFragmentVisitor;



public class ActivityDiagramParser 
{
	
	private String projectPath = "";
	private String projectSourcePath = projectPath + "";
	private String jrePath = "";
	private String entryClass = "";
	private String entryMethod = "";
	private ActivityDiagram activityDiagram;
	
	public ActivityDiagramParser(String projectPath, String projectSourcePath, String jrePath, String entryClass, String entryMethod) 
	{
		this.projectPath = projectPath;
		this.projectSourcePath = projectSourcePath;
		this.jrePath = jrePath;
		this.entryClass = entryClass;
		this.entryMethod = entryMethod;
	}

	
	public ActivityDiagram parseActivityDiagram() throws IOException
	{
		CompilationUnit parse = getCompilationUnitByDir();
		// print methods info
		//printMethodInfo(parse);
		//setMethodHashMap(parse);
		List<TypeDeclaration> listClasses = getClassList(parse);
		
		HashMap<String, ADMethodInvocation> listInvocationMethods = getListInvocationMethods(parse);
		activityDiagram = new ActivityDiagram(listInvocationMethods, listClasses, entryClass, entryMethod);
		activityDiagram.testClassDiagram();
		return activityDiagram;
	}
	
	public List<TypeDeclaration> getClassList(CompilationUnit parse)
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
			// System.out.println(content);
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
			} else if (fileEntry.getName().contains(".java")) {
				// System.out.println(fileEntry.getName());
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
		
		return (CompilationUnit) parser.createAST(null); // create and parse
	}
	
	
	
	// navigate method information
	public void printMethodInfo(CompilationUnit parse) 
	{
		MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
		parse.accept(visitor);

		for (MethodDeclaration method : visitor.getMethods()) {
			System.out.println("Method name: " + method.getName()
					+ " Return type: " + method.getReturnType2());
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
	
	
	public ArrayList<String> getVariableDeclaration(MethodDeclaration method)
	{
		ArrayList<String> varDecList = new ArrayList<String>();
		VariableDeclarationFragmentVisitor visitor2 = new VariableDeclarationFragmentVisitor();
		method.accept(visitor2);

		for (VariableDeclarationFragment variableDeclarationFragment : visitor2.getVariables()) {
			String tmp = "" //+ variableDeclarationFragment.getInitializer().get + " "
					+ variableDeclarationFragment.getName()
					+ " = " + variableDeclarationFragment.getInitializer();
			System.out.println("		variable name: " + tmp);
			varDecList.add(tmp);
		}
		return varDecList;
	}
	
	
	public ArrayList<String> getSingleVariableDeclarationType(MethodDeclaration method)
	{
		ArrayList<String> paramTypeList = new ArrayList<String>();
		SingleVariableDeclarationVisitor visitor2 = new SingleVariableDeclarationVisitor();
		method.accept(visitor2);

		for (SingleVariableDeclaration singleVariableDeclaration : visitor2.getPrameters()) {
			String tmp = singleVariableDeclaration.getType() + "";
			System.out.println("	param type: " + tmp); 
			paramTypeList.add(tmp);
		}
		return paramTypeList;
	}
	
	public ArrayList<String> getSingleVariableDeclaration(MethodDeclaration method)
	{
		ArrayList<String> paramList = new ArrayList<String>();
		SingleVariableDeclarationVisitor visitor2 = new SingleVariableDeclarationVisitor();
		method.accept(visitor2);

		for (SingleVariableDeclaration singleVariableDeclaration : visitor2.getPrameters()) {
			String tmp = singleVariableDeclaration.getType() + " " + singleVariableDeclaration.getName();
			//System.out.println("	param name: " + tmp); 
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

			getMethodInvocationList(method);

		}
	}
	
	
	public ArrayList<String> getMethodInvocationList(MethodDeclaration method) 
	{
		ArrayList<String> invocationMethodList = new ArrayList<String>();
		MethodInvocationVisitor visitor2 = new MethodInvocationVisitor();
		method.accept(visitor2);
		
		for (MethodInvocation node : visitor2.getMethods()) {
			String methodInvData = method.getName() + "(";
			
			//System.out.println("method "  + method.getName() + " invoc method " + node.getName());
			
		    Expression expression = node.getExpression();
		    if (expression != null) {
		        //System.out.println("Expr: " + expression.toString());
		        ITypeBinding typeBinding = expression.resolveTypeBinding();
		        if (typeBinding != null) {
		            //System.out.println("Type: " + typeBinding.getName());
		            //methodInvData += typeBinding.getName() + " ";
		        }
		        //methodInvData += typeBinding.getName() + " " + expression.toString();
		    }
		    methodInvData += ")::";
		    
		    IMethodBinding binding = node.resolveMethodBinding();
		    if (binding != null) {
		        ITypeBinding type = binding.getDeclaringClass();
		        if (type != null) {
		            //System.out.println("Call: " + type.getName() + " -> " + node.getName());
		        	methodInvData += "" + type.getName();
		        }
		    }
		    
		    methodInvData += "->" + node.getName() + "()";
		    //System.out.println("methodInv: " + methodInvData);
		    invocationMethodList.add(methodInvData);
		}
		return invocationMethodList;
	}
	
	
	
	public HashMap<String, ADMethodInvocation> getListInvocationMethods(CompilationUnit parse)
	{
		List<TypeDeclaration> listClasses = getClassList(parse);
		HashMap<String, ADMethodInvocation> listInvocationMethods = new HashMap<String, ADMethodInvocation>();
		for (TypeDeclaration classOb : listClasses) {
			for (MethodDeclaration method : classOb.getMethods()) {
				
				/*
				String methodInvData = method.getName() + "(";
				
				Expression expression = node.getExpression();
			    if (expression != null) {
			        //System.out.println("Expr: " + expression.toString());
			        ITypeBinding typeBinding = expression.resolveTypeBinding();
			        if (typeBinding != null) {
			            //System.out.println("Type: " + typeBinding.getName());
			            //methodInvData += typeBinding.getName() + " ";
			        }
			        methodInvData += typeBinding.getName() + " " + expression.toString();
			    }
			    methodInvData += ")::";
			    */
				
				String key = "" + classOb.getName().toString() + "-" + method.getName();
				ArrayList<String> paramList = getSingleVariableDeclaration(method);
				List<String> paramTypeList = getSingleVariableDeclarationType(method);
				ArrayList<String> invocationMethodList = getMethodInvocationList(method);
				ArrayList<String> varDecList = getVariableDeclaration(method);
				String methodName = "" + method.getName() + "(" + String.join(",",paramTypeList) + ")";
				System.out.println("Method name: " + classOb.getName() + "." + methodName + ": Return type: " + method.getReturnType2());
				
				
				//String returnType = "";
				String returnType = method.getReturnType2() + "";
				ADMethodInvocation  objMethInv = new ADMethodInvocation(classOb.getName().toString(), methodName, paramList, varDecList, invocationMethodList, returnType);
				
				listInvocationMethods.put(key, objMethInv);
			}
		}
		return listInvocationMethods;
	}

}


