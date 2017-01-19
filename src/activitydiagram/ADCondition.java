package activitydiagram;

import java.util.HashMap;
import java.util.Map;

public class ADCondition extends ADNode{
	private Integer startPosition;
	private Integer endPosition;
	private String conditionExpression;
	private Map<Integer, String> thenStatements;
	private Map<Integer, String> elseStatements ;
	private Map<Integer, String> thenStatementsWithVars;
	private Map<Integer, String> elseStatementsWithVars;
	private Integer startParentPosition = -1;
	private Integer startConditionPosition;
	private Integer startThenPosition;
	private Integer startElsePosition;
	
	

	public ADCondition(Integer startPosition, Integer endPosition, String instructionKey, String conditionExpression,
			Map<Integer, String> thenStatements, Map<Integer, String> elseStatements, Integer startConditionPosition,
			Integer startThenPosition, Integer startElsePosition, Integer startParentPosition) {
		super();
		this.instructionKey = instructionKey;
		this.setTypeNode("ADCondition");
		this.setStartPosition(startPosition);
		this.endPosition = endPosition;
		setConditionExpression(conditionExpression);
		this.thenStatements = thenStatements;
		this.elseStatements = elseStatements;
		this.startConditionPosition = startConditionPosition;
		this.startThenPosition = startThenPosition;
		this.startElsePosition = startElsePosition;
		this.startParentPosition = startParentPosition;
	}
	
	
	
	public Integer getStartPosition() {
		return startPosition;
	}
	public void setStartPosition(Integer startPosition) {
		this.startPosition = startPosition;
	}
	public Integer getEndPosition() {
		return endPosition;
	}
	public void setEndPosition(Integer endPosition) {
		this.endPosition = endPosition;
	}
	public String getConditionExpression() {
		return conditionExpression;
	}
	public void setConditionExpression(String conditionExpression) {
		this.displayInstruction  = conditionExpression;
		this.conditionExpression = conditionExpression;
	}
	public Map<Integer, String> getThenStatements() {
		return thenStatements;
	}
	public void setThenStatements(Map<Integer, String> thenStatements) {
		this.thenStatements = thenStatements;
	}
	public Map<Integer, String> getElseStatements() {
		return elseStatements;
	}
	public void setElseStatements(Map<Integer, String> elseStatements) {
		this.elseStatements = elseStatements;
	}
	public Integer getStartConditionPosition() {
		return startConditionPosition;
	}
	public void setStartConditionPosition(Integer startConditionPosition) {
		this.startConditionPosition = startConditionPosition;
	}
	public Integer getStartThenPosition() {
		return startThenPosition;
	}
	public void setStartThenPosition(Integer startThenPosition) {
		this.startThenPosition = startThenPosition;
	}
	public Integer getStartElsePosition() {
		return startElsePosition;
	}
	public void setStartElsePosition(Integer startElsePosition) {
		this.startElsePosition = startElsePosition;
	}
	public Integer getStartParentPosition() {
		return startParentPosition;
	}
	public void setStartParentPosition(Integer startParentPosition) {
		this.startParentPosition = startParentPosition;
	}



	public Map<Integer, String> getThenStatementsWithVars() {
		return thenStatementsWithVars;
	}



	public void setThenStatementsWithVars(Map<Integer, String> thenStatementsWithVars) {
		this.thenStatementsWithVars = thenStatementsWithVars;
	}



	public Map<Integer, String> getElseStatementsWithVars() {
		return elseStatementsWithVars;
	}



	public void setElseStatementsWithVars(Map<Integer, String> elseStatementsWithVars) {
		this.elseStatementsWithVars = elseStatementsWithVars;
	}
	
}
