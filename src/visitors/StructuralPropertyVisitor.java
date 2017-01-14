package visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

public class StructuralPropertyVisitor extends ASTVisitor  {
	List<String> parameters = new ArrayList<String>();
	
	public boolean visit(MethodDeclaration node) {
		for (Object parameter : node.parameters()) {
            VariableDeclaration variableDeclaration = (VariableDeclaration) parameter;
            String type = variableDeclaration.getStructuralProperty(SingleVariableDeclaration.TYPE_PROPERTY)
                    .toString();
            for (int i = 0; i < variableDeclaration.getExtraDimensions(); i++) {
                type += "[]";
            }
            parameters.add(type);
        }
		System.out.println(parameters);
		return true;
	}
	
	public List<String> getParameters() {
		return parameters;
	}
}

