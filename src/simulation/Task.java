package simulation;

import java.util.ArrayList;

class Task {
	
	public final int taskId;
	public final int insNum;
	public ArrayList<Instruction> insList;
	public final int inTime;

	public Task(int taskId, int insNum, int inTime) {
		this.taskId = taskId;
		this.insNum = insNum; 
		insList = new ArrayList<Instruction>();
		this.inTime = inTime;
	}

}
