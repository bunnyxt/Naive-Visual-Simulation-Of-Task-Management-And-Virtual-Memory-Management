package simulation;

import java.util.ArrayList;

class Jcb {
	
	public final int jobId;
	public final int insNum;
	public ArrayList<Instruction> insList;
	public final int inTime;
	public final int memBlockRequired;
	public JobStatus status;

	public Jcb(int jobId, int insNum, int inTime, int memBlockRequired) {
		this.jobId = jobId;
		this.insNum = insNum; 
		insList = new ArrayList<Instruction>();
		this.inTime = inTime;
		this.memBlockRequired = memBlockRequired;
		status = JobStatus.NOT_PROPOSED;
	}
	
	public enum JobStatus {
	    NOT_PROPOSED, IN_QUEUE, FINISHED;
	}

}
