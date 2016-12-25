package visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

public class SingleVariableDeclarationVisitor extends ASTVisitor {
	private List<SingleVariableDeclaration> listParameters = new ArrayList<SingleVariableDeclaration>();
	
	public boolean visit(SingleVariableDeclaration node) {
		listParameters.add(node);
		return super.visit(node);
	}
	
	public List<SingleVariableDeclaration> getPrameters() {
		return listParameters;
	}
}
