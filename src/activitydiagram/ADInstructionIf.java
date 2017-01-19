package activitydiagram;

public class ADInstructionIf extends ADInstruction {
	protected int firstPosThen = -1;
	protected int lastPosThen = -1;
	protected int firstPosElse = -1;
	protected int lastPosElse = -1;
	protected int mergePos = -1;
	
	public int getFirstPosThen() {
		return firstPosThen;
	}

	public int getLastPosThen() {
		return lastPosThen;
	}

	public int getFirstPosElse() {
		return firstPosElse;
	}

	public int getLastPosElse() {
		return lastPosElse;
	}

	public int getMergePos() {
		return mergePos;
	}

	public void setFirstPosThen(int firstPosThen) {
		this.firstPosThen = firstPosThen;
	}

	public void setLastPosThen(int lastPosThen) {
		this.lastPosThen = lastPosThen;
	}

	public void setFirstPosElse(int firstPosElse) {
		this.firstPosElse = firstPosElse;
	}

	public void setLastPosElse(int lastPosElse) {
		this.lastPosElse = lastPosElse;
	}

	public void setMergePos(int mergePos) {
		this.mergePos = mergePos;
	}

	public ADInstructionIf(String instructionKey, String displayInstruction, String typeNode, Integer position,
			Integer posParent) {
		super(instructionKey, displayInstruction, typeNode, position, posParent);
		// TODO Auto-generated constructor stub
	}

}
