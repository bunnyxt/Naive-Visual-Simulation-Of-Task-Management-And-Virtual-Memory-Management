package simulation;

import java.util.ArrayList;

class Pcb {
	
	public Jcb oriJob;
	
	public final int pcbId;
	public final int insNum;
	public ArrayList<Instruction> insList;
	
	public ProcessStatus status;
	public int nowInsIndex;
	public int timePieceLeft;
	public int waitTimeLeft;
	public int waitTimeCount;
	
	public CpuContext context;
	
	public PageTable pageTable;
	
	public boolean MemoryAllocated;
	
	public Pcb(Jcb job, int pcbId) {
		oriJob = job;
		this.pcbId = pcbId;
		this.insNum = job.insNum;
		this.insList = job.insList;
		
		this.status = ProcessStatus.CREATED;
		nowInsIndex = 0;
		timePieceLeft = 200;
		waitTimeLeft = 0;
		waitTimeCount = 0;
		
		context = new CpuContext();

		pageTable = new PageTable(job.memBlockRequired);
		pageTable.usedlength = pageTable.length;
		
		MemoryAllocated = false;
	}
	
	public enum ProcessStatus {
		CREATED, READY, RUNNING, WAIT, FINISHED;
	}

}
