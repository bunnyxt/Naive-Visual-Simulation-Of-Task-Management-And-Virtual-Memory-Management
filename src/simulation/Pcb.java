package simulation;

import java.util.ArrayList;

class Pcb {
	
	public Task oriTask;
	
	public final int pcbId;
	public final int insNum;
	public ArrayList<Instruction> insList;
	
	public ProcessStatus status;
	public int nowInsIndex;
	public int timePieceLeft;
	public int waitTimeLeft;
	
	public CpuContext context;
	
	public Pcb(Task task, int pcbId) {
		oriTask = task;
		this.pcbId = pcbId;
		this.insNum = task.insNum;
		this.insList = task.insList;
		
		this.status = ProcessStatus.CREATED;
		nowInsIndex = 0;
		timePieceLeft = 200;
		waitTimeLeft = 0;
		
		context = new CpuContext();
	}
	
	public enum ProcessStatus {
		CREATED, READY, RUNNING, WAIT, FINISHED;
	}

}
