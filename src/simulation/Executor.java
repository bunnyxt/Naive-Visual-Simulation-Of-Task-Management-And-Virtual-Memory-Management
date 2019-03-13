package simulation;

import java.util.Queue;

class Executor {
	
	public Cpu cpu;
	public Memory memory;
	public Disk disk;
	public ExchangeArea exchangeArea;
	public FastTable fastTable;
	public MMU mmu;
	
	public Semaphore[] semaphores;
	
	Queue<Pcb> readyQueue;
	Queue<Pcb> runningQueue;
	Queue<Pcb> waitQueue;
	
	public Executor(Cpu cpu, Memory memory, Disk disk, ExchangeArea exchangeArea, FastTable fastTable, MMU mmu, Semaphore[] semaphores, Queue<Pcb> readyQueue, Queue<Pcb> runningQueue, Queue<Pcb> waitQueue) {
		this.cpu = cpu;
		this.memory = memory;
		this.disk = disk;
		this.exchangeArea = exchangeArea;
		this.fastTable = fastTable;
		this.mmu = mmu;
		
		this.semaphores = semaphores;
		
		this.readyQueue = readyQueue;
		this.runningQueue = runningQueue;
		this.waitQueue = waitQueue;
	}
	
	public int Execute(Instruction instruction, Pcb pcb) {
		int returnCode = 0;
		System.out.println("Now execute instruction \"" + instruction.ToString() + "\"...");
		
		switch (instruction.insType) {
		case 0:
			// instruction calc rr
			InstructionCalcRR ins0 = (InstructionCalcRR)instruction;
			cpu.registers[ins0.reg3] = calc(cpu.registers[ins0.reg1], cpu.registers[ins0.reg2], ins0.op);
			break;
		case 1:
			// instruction calc ri
			InstructionCalcRI ins1 = (InstructionCalcRI)instruction;
			cpu.registers[ins1.reg2] = calc(cpu.registers[ins1.reg1], ins1.imm, ins1.op); 
			break;
		case 2:
			// instruction io
			InstructionIO ins2 = (InstructionIO)instruction;
			// TODO
			int address = mmu.Log_to_Phy(pcb, ins2.address, fastTable, pcb.pageTable, memory, exchangeArea);
			if (ins2.writeFlag == 1) {
				memory.set(address, cpu.registers[ins2.reg]);
				pcb.pageTable.page[ins2.address / 256].changed = true;
			} else {
				cpu.registers[ins2.reg] = memory.get(address);
			}
			break;
		case 3:
			// instruction res
			InstructionRes ins3 = (InstructionRes)instruction;
			if (ins3.resourceId < 0 || ins3.resourceId >= semaphores.length) {
				break;
			}
			if (ins3.releaseFlag == 1) {
				// release resource
				returnCode = Semaphore.V(semaphores[ins3.resourceId], waitQueue, readyQueue);
			} else {
				// request resource
				returnCode = Semaphore.P(semaphores[ins3.resourceId], runningQueue, waitQueue, cpu);
			}
			break;
		default:
			break;
		}
		
		System.out.println("Instruction executed successfully.");
		return returnCode;
	}
	
	private short calc(short a, short b, String op) {
		short result = 0;
		switch (op) {
		case "+":
			result = (short) (a + b);
			break;
		case "-":
			result = (short) (a - b);
			break;
		case "*":
			result = (short) (a * b);
			break;
		case "/":
			result = (short) (a / b);
			break;
		case "%":
			result = (short) (a % b);
			break;
		default:
			break;
		}
		return result;
	}

}
