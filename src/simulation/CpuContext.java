package simulation;

class CpuContext {
	
	public final int registerNum;
	public short[] registers;
	public short PC;
	public short IR;
	public short PSW;
	
	public CpuContext() {
		registerNum = 8;
		registers = new short[registerNum];
		for (int i = 0; i < this.registerNum; i++) {
			registers[i] = registers[i];
		}
		PC = 0;
		IR = 0;
		PSW = 0;
	}
	
	public CpuContext(int registerNum, short[] registers, short PC, short IR, short PSW) {
		this.registerNum = registerNum;
		this.registers = new short[registerNum];
		for (int i = 0; i < this.registerNum; i++) {
			this.registers[i] = registers[i];
		}
		this.PC = PC;
		this.IR = IR;
		this.PSW = PSW;
	}

}
