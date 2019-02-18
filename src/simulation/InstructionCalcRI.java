package simulation;

class InstructionCalcRI extends Instruction {
	
	// reg1 op imm -> reg2
	public final int reg1;
	public final short imm;
	public final int reg2;
	public final String op;

	public InstructionCalcRI(int insId, int insType, int insLeftTime, int reg1, short imm, int reg2, String op) {
		super(insId, insType, insLeftTime);
		this.reg1 = reg1;
		this.imm = imm;
		this.reg2 = reg2;
		this.op = op;
	}

}
