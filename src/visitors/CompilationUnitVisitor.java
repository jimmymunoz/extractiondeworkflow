package visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class CompilationUnitVisitor extends ASTVisitor {
	List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
	
	@Override
	public boolean visit(CompilationUnit node) {
	    System.out.println("Compilation unit: " + node.getPackage().getName());
	    System.out.println("Imports: " + node.imports());
	    //System.out.println("Class Name: " + node.); //How to get this?
	    return super.visit(node);
	}
	
	public List<MethodDeclaration> getMethods() {
		return methods;
	}
}
