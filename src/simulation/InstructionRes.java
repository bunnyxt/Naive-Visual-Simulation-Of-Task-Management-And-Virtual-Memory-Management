package simulation;

class InstructionRes extends Instruction {
	
	public final int releaseFlag;
	public final int resourceId;

	public InstructionRes(int insId, int insType, int insLeftTime, int releaseFlag, int resourceId) {
		super(insId, insType, insLeftTime);
		this.releaseFlag = releaseFlag;
		this.resourceId = resourceId;
	}

}
