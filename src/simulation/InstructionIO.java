package simulation;

class InstructionIO extends Instruction {
	
	public final int writeFlag;
	public final int address;
	public final int reg;

	public InstructionIO(int insId, int insType, int insLeftTime, int writeFlag, int address, int reg) {
		super(insId, insType, insLeftTime);
		this.writeFlag = writeFlag;
		this.address = address;
		this.reg = reg;
	}
	
	public String ToString() {
		if (writeFlag == 1) {
			return address + " -> r" + reg;
		} else {
			return "r" + reg + " -> " + address;
		}
	}
	
}
