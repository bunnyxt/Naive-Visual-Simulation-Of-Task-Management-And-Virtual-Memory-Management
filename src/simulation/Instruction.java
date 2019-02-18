package simulation;

class Instruction {
	
	public final int insId;
	public final int insType;
	public int insLeftTime;
	
	public Instruction(int insId, int insType, int insLeftTime) {
		this.insId = insId;
		this.insType = insType;
		this.insLeftTime = insLeftTime;
	}

}
