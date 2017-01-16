package activitydiagram;

import java.util.HashMap;
import java.util.Map;

public class ADCondition extends ADNode{
	private Integer startPosition;
	private Integer endPosition;
	private String conditionExpression;
	private Map<Integer, String> thenStatements;
	private Map<Integer, String> elseStatements ;
	private Integer startParentPosition = -1;
	private Integer startConditionPosition;
	private Integer startThenPosition;
	private Integer startElsePosition;
	

	public ADCondition(Integer startPosition, Integer endPosition, String conditionExpression,
			Map<Integer, String> thenStatements, Map<Integer, String> elseStatements, Integer startConditionPosition,
			Integer startThenPosition, Integer startElsePosition, Integer startParentPosition) {
		super();
		this.setTypeNode("ADCondition");
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.conditionExpression = conditionExpression;
		this.thenStatements = thenStatements;
		this.elseStatements = elseStatements;
		this.startConditionPosition = startConditionPosition;
		this.startThenPosition = startThenPosition;
		this.startElsePosition = startElsePosition;
		this.startParentPosition = startParentPosition;
		this.displayInstruction  = conditionExpression;
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
		this.displayInstruction  = conditionExpression;
		return conditionExpression;
	}
	public void setConditionExpression(String conditionExpression) {
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
	
}
