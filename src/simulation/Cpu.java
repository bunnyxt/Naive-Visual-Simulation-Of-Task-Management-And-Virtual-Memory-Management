package simulation;

class Cpu {
	
	public final int registerNum;
	public short[] registers;
	public short PC;
	public short IR;
	public short PSW;
	
	public Cpu() {
		registerNum = 8;
		registers = new short[registerNum];
		PC = 0;
		IR = 0;
		PSW = 0;
	}
	
	public CpuContext ProtectContext() {
		return new CpuContext(registerNum, registers, PC, IR, PSW);
	}
	
	public void RecoverContext(CpuContext context) {
		for (int i = 0; i < context.registerNum; i++) {
			if (i < this.registerNum) {
				this.registers[i] = context.registers[i];
			}
		}
		this.PC = context.PC;
		this.IR = context.IR;
		this.PSW = context.PSW;
	}
}
