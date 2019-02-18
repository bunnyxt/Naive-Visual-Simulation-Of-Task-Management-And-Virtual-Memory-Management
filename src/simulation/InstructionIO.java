package simulation;

class InstructionIO extends Instruction {
	
	public final int writeFlag;
	public final int address;

	public InstructionIO(int insId, int insType, int insLeftTime, int writeFlag, int address) {
		super(insId, insType, insLeftTime);
		this.writeFlag = writeFlag;
		this.address = address;
	}
	
}
