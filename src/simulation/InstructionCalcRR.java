package simulation;

class InstructionCalcRR extends Instruction {
	
	// reg1 op reg2 -> reg3
	public final int reg1;
	public final int reg2;
	public final int reg3;
	public final String op;

	public InstructionCalcRR(int insId, int insType, int insLeftTime, int reg1, int reg2, int reg3, String op) {
		super(insId, insType, insLeftTime);
		this.reg1 = reg1;
		this.reg2 = reg2;
		this.reg3 = reg3;
		this.op = op;
	}
	
	public String ToString() {
		return "r" + reg1 + " " + op + " r" + reg2 + " -> r" + reg3;
	}
	
}
