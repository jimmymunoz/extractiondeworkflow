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
		
		Integer startThenPosition = node.getThenStatement().getStartPosition();
		Integer endThenPosition = node.getThenStatement().getStartPosition() + node.getThenStatement().getLength();
		Map<Integer, String> thenStatements = new HashMap<Integer, String>();
		Map<Integer, String> thenStatementsWithVars = new HashMap<Integer, String>();
		for (MethodInvocation method : methodInvocationThenList){
			Integer methodposition = method.getStartPosition();
			//System.out.println("--> methodposition:" + methodposition + " startThenPosition: " + startThenPosition +  " endThen:" + endThenPosition + " parentpos:" + method.getParent().getStartPosition());
			if( methodposition > startThenPosition &&  methodposition < endThenPosition ){
				String methodName = ActivityDiagramParser.getMethodNameByMethodInvocation(method);
				thenStatements.put(methodposition, methodName);
				String methodNameWithVars = ActivityDiagramParser.getMethodNameWithVarsByMethodInvocation(method);
				thenStatementsWithVars.put(methodposition, methodNameWithVars);
			}
			
		}
		
		
		Integer startElsePosition = -1;
		Integer endElsePosition = -1;
		
		Map<Integer, String> elseStatements = new HashMap<Integer, String>();
		Map<Integer, String> elseStatementsWithVars = new HashMap<Integer, String>();
		if (node.getElseStatement() != null)
		{
			startElsePosition = node.getElseStatement().getStartPosition();
			endElsePosition = node.getElseStatement().getStartPosition() + node.getElseStatement().getLength();
			
			node.getElseStatement().accept(visitorElse);
			List<MethodInvocation> methodInvocationElseList = visitorElse.getMethods();
			for (MethodInvocation method : methodInvocationElseList){
				Integer methodposition = method.getStartPosition();
				//System.out.println("--> methodposition:" + methodposition + " startElsePosition: " + startElsePosition +  " endElse: " + endElsePosition+ " parentpos:" + method.getParent().getStartPosition());
				
				String methodName = ActivityDiagramParser.getMethodNameByMethodInvocation(method);
				elseStatements.put(method.getStartPosition(), methodName);
				String methodNameWithVars = ActivityDiagramParser.getMethodNameWithVarsByMethodInvocation(method);
				elseStatementsWithVars.put(methodposition, methodNameWithVars);
			}
			
		}
		
		
		ADCondition adCondition = new ADCondition(node.getStartPosition(), node.getStartPosition() + node.getLength(), node.getExpression().toString(), node.getExpression().toString(), thenStatements, elseStatements, node.getExpression().getStartPosition(),
				startThenPosition, startElsePosition, node.getParent().getStartPosition());
		adCondition.setThenStatementsWithVars(thenStatementsWithVars);
		adCondition.setElseStatementsWithVars(elseStatementsWithVars);
		this.hashConditionsClass.put(node.getStartPosition(), adCondition);
		
		return super.visit(node);
	}

	public HashMap<Integer, ADCondition> getHashConditionsClass() {
		return this.hashConditionsClass;
	}
}
