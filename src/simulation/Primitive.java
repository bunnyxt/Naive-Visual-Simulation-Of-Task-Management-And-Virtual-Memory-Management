package simulation;

import java.util.Queue;

public class Primitive {
	
	// create new process
	public static void Create(Jcb job, Queue<Pcb> readyQueue) {
		
		// create Pcb
		Pcb pcb = new Pcb(job, PcbIdAllocator.getNextPcbId());
		System.out.println("job " + job.jobId + " proposed and pcb " + pcb.pcbId + " created.");
		
		// add to ready queue
		readyQueue.offer(pcb);
		System.out.println("Pcb " + pcb.pcbId + " added to ready queue.");
		
	}
	
	// withdraw process
	public static void Withdraw(Pcb pcb, Queue<Pcb> runningQueue, Semaphore[] semaphores) {
		
		// remove pcb from queue
		runningQueue.remove(pcb);
		System.out.println("Pcb " + pcb.pcbId + " removed from running queue.");
		
		// clear resources
		for (Semaphore semaphore : semaphores) {
			boolean flag = false;
			for (Pcb pcbInList : semaphore.list) {
				if (pcbInList == pcb) {
					flag = true;
					break;
				}
			}
			if (flag == true) {
				semaphore.list.remove(pcb);
			}
		}
		
		// change status
		pcb.status = Pcb.ProcessStatus.FINISHED;
		System.out.println("Pcb " + pcb.pcbId + " finished.");
		
	}
	
	// select process from ready queue to running queue
	public static void SelectIn(Pcb pcb, Queue<Pcb> readyQueue, Queue<Pcb> runningQueue, Cpu cpu) {
		
		// remove pcb from ready queue
		readyQueue.remove(pcb);
		System.out.println("Pcb " + pcb.pcbId + " removed from ready queue.");
		
		// add pcb to running queue
		runningQueue.offer(pcb);
		System.out.println("Pcb " + pcb.pcbId + " added to running queue.");
		
		// set time piece
		pcb.timePieceLeft = 200;
		
		// recover cpu context
		cpu.RecoverContext(pcb.context);
		
		// change status
		pcb.status = Pcb.ProcessStatus.RUNNING;
		
	}
	
	// exchange process from running queue to ready queue
	public static void ExchangeOut(Pcb pcb, Queue<Pcb> runningQueue, Queue<Pcb> readyQueue, Cpu cpu) {
		
		// remove pcb from running queue
		runningQueue.remove(pcb);
		System.out.println("Pcb " + pcb.pcbId + " removed from running queue.");
		
		// add pcb to ready queue
		readyQueue.offer(pcb);
		System.out.println("Pcb " + pcb.pcbId + " added to ready queue.");
		
		// protect cpu context
		pcb.context = cpu.ProtectContext();
		
		// change status
		pcb.status = Pcb.ProcessStatus.READY;
		
	}
	
	// move process from running queue to wait queue
	public static void Wait(Pcb pcb, Queue<Pcb> runningQueue, Queue<Pcb> waitQueue, int waitTime, Cpu cpu) {
		
		// remove pcb from running queue
		runningQueue.remove(pcb);
		System.out.println("Pcb " + pcb.pcbId + " removed from running queue.");
		
		// add pcb to wait queue
		waitQueue.offer(pcb);
		System.out.println("Pcb " + pcb.pcbId + " added to wait queue.");
		
		// set wait time
		pcb.waitTimeLeft = waitTime;
		
		// initialize wait tiem count
		pcb.waitTimeCount = 0;
		
		// protect cpu context
		pcb.context = cpu.ProtectContext();
		
		// change status
		pcb.status = Pcb.ProcessStatus.WAIT;
		
	}
	
	// move process from wait queue to ready queue
	public static void WakeUp(Pcb pcb, Queue<Pcb> waitQueue, Queue<Pcb> readyQueue) {
		
		// remove pcb from wait queue
		waitQueue.remove(pcb);
		System.out.println("Pcb " + pcb.pcbId + " removed from wait queue.");
		
		// add pcb to ready queue
		readyQueue.offer(pcb);
		System.out.println("Pcb " + pcb.pcbId + " added to ready queue.");
		
		// move to next instruction
		pcb.nowInsIndex++;
		
		// change status 
		pcb.status = Pcb.ProcessStatus.READY;
		
	}
	
}
