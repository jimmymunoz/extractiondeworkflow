package visitors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;

import activitydiagram.ADCondition;
import activitydiagram.ActivityDiagramParser;

import org.eclipse.jdt.core.dom.MethodInvocation;


public class IfStatementVisitor  extends ASTVisitor{
	private HashMap<Integer, ADCondition> hashConditionsClass = new HashMap<Integer, ADCondition>();
	private String className = "";
	
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean visit(IfStatement node)
	{
		MethodInvocationVisitor visitorThen = new MethodInvocationVisitor();
		MethodInvocationVisitor visitorElse = new MethodInvocationVisitor();
		
		node.getThenStatement().accept(visitorThen);
		List<MethodInvocation> methodInvocationThenList = visitorThen.getMethods();
		
		
		Map<Integer, String> thenStatements = new HashMap<Integer, String>();
		for (MethodInvocation method : methodInvocationThenList){
			String methodName = ActivityDiagramParser.getActMethodName(null, method, className, method.getName().toString(), "" );
			thenStatements.put(method.getStartPosition(), methodName);
		}
		
		Integer startThenPosition = node.getThenStatement().getStartPosition();
		Integer startElsePosition = -1;
		
		Map<Integer, String> elseStatements = new HashMap<Integer, String>();
		if (node.getElseStatement() != null)
		{
			startElsePosition = node.getElseStatement().getStartPosition();
			node.getElseStatement().accept(visitorElse);
			List<MethodInvocation> methodInvocationElseList = visitorElse.getMethods();
			for (MethodInvocation method : methodInvocationElseList){
				String methodName = ActivityDiagramParser.getActMethodName(null, method, className, method.getName().toString(), "" );
				elseStatements.put(method.getStartPosition(), methodName);
			}
			
		}
		
		
		ADCondition adCondition = new ADCondition(node.getStartPosition(), node.getStartPosition() + node.getLength(), node.getExpression().toString(), thenStatements, elseStatements, node.getExpression().getStartPosition(),
				startThenPosition, startElsePosition, node.getParent().getStartPosition());
		this.hashConditionsClass.put(node.getStartPosition(), adCondition);
		
		return super.visit(node);
	}

	public HashMap<Integer, ADCondition> getHashConditionsClass() {
		return this.hashConditionsClass;
	}
}
