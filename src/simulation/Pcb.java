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
	public int waitTimeCount;
	
	public CpuContext context;
	
	public PageTable pageTable;
	
	public Pcb(Task task, int pcbId) {
		oriTask = task;
		this.pcbId = pcbId;
		this.insNum = task.insNum;
		this.insList = task.insList;
		
		this.status = ProcessStatus.CREATED;
		nowInsIndex = 0;
		timePieceLeft = 200;
		waitTimeLeft = 0;
		waitTimeCount = 0;
		
		context = new CpuContext();
		
		//形成页表项
		Page page[] = new Page[20];
		for (int i = 0; i < 20; i++) {
			page[i] = new Page(-1, -1, 0, 0);//参数：页号 块号  是否在内存  访问次数
		}	
		pageTable = new PageTable(page, 5); // TODO choose proper length i
	}
	
	public enum ProcessStatus {
		CREATED, READY, RUNNING, WAIT, FINISHED;
	}

}
