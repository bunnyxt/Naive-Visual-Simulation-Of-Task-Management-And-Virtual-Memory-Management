package simulation;

class Executor {
	
	public Cpu cpu;
	public Memory memory;
	public Disk disk;
	
	public Executor(Cpu cpu, Memory memory, Disk disk) {
		this.cpu = cpu;
		this.memory = memory;
		this.disk = disk;
	}
	
	public void Execute(Instruction instruction) {
		System.out.println("Now execute instruction " + instruction.ToString() + "...");
		// TODO
		System.out.println("Instruction executed successfully.");
	}

}
