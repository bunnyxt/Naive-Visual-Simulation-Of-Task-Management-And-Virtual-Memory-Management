package simulation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Main {

	public static void main(String[] args) {
		
		// initialize components
		Cpu cpu = new Cpu();
		Memory memory = new Memory();
		Disk disk = new Disk();
		
		// initialize semaphores
		int semaphoreNum = 4;
		Semaphore[] semaphores = new Semaphore[semaphoreNum];
		semaphores[0] = new Semaphore(0);
		semaphores[1] = new Semaphore(1);
		semaphores[2] = new Semaphore(1);
		semaphores[3] = new Semaphore(1);
		
		// create queues
		Queue<Pcb> readyQueue = new LinkedList<Pcb>();
		Queue<Pcb> runningQueue = new LinkedList<Pcb>();
		Queue<Pcb> waitQueue = new LinkedList<Pcb>();
		
		// create executor
		Executor executor = new Executor(cpu, memory, disk, semaphores, readyQueue, runningQueue, waitQueue);
		
		// load tasks
		ArrayList<Task> tasks = TaskLoader.LoadTasks("tasks.txt");
		
		// time proposed
		int nowTime = 0;
		
		// speed delay
		double speedDelay = 10.0;
		
		// main loop
		while (!TaskHelper.AllTasksFinished(tasks)) {
			
			System.out.println("---------- " + nowTime + "ms ----------");
			
			// check new tasks
			for (Task task : tasks) {
				if (task.inTime == nowTime) {
					// create process
					Primitive.Create(task, readyQueue);
					
					// change task status
					task.status = Task.TaskStatus.IN_QUEUE;
				}
			} 
			
			// handle process in running queue
			if (!runningQueue.isEmpty()) {
				
				// get pcb of running process
				Pcb runningPcb = runningQueue.peek();
				
				// get running instruction
				Instruction runningInstruction = runningPcb.insList.get(runningPcb.nowInsIndex);
				
				// check whether now instruction finished or not
				if (runningInstruction.insLeftTime == 0) {
					// now instruction finished
					
					// check whether more instructions left
					if (runningPcb.nowInsIndex + 1 >= runningPcb.insNum) {
						// no more instructions left
						
						// withdraw process
						Primitive.Withdraw(runningPcb, runningQueue);
						
						// set task finished
						runningPcb.oriTask.status = Task.TaskStatus.FINISHED;
						System.out.println("Task " + runningPcb.oriTask.taskId + " finished.");
					} else {
						// has more instructions left
						
						// now ins index ++
						runningPcb.nowInsIndex++;
						
						// get next instruction
						Instruction nextInstruction = runningPcb.insList.get(runningPcb.nowInsIndex);
						
						// check time piece left time enough or not
						if (runningPcb.timePieceLeft < nextInstruction.insLeftTime) {
							// no enough time for next instruction
							
							// exchange out
							Primitive.ExchangeOut(runningPcb, runningQueue, readyQueue, cpu);
							
						} else {
							// has enough time for next instruction
							
							// execute instruction
							int returnCode = executor.Execute(nextInstruction);
							
							switch (returnCode) {
							case 3:
								// now pcb in wait queue
								break;
							case 0:
							default:
								// modify time
								System.out.println("Instruction " + nextInstruction.insId + " time left " + nextInstruction.insLeftTime + "ms, time piece left " + runningPcb.timePieceLeft + "ms.");
								nextInstruction.insLeftTime -= 10;
								runningPcb.timePieceLeft -= 10;
								break;
							}
						}
					}
				} else {
					// now instruction not finished
					
					// modify time
					System.out.println("Instruction " + runningInstruction.insId + " time left " + runningInstruction.insLeftTime + "ms, time piece left " + runningPcb.timePieceLeft + "ms.");
					runningInstruction.insLeftTime -= 10;
					runningPcb.timePieceLeft -= 10;
				}
			}
			
			// wakeup process in wait queue
			
			// pcbs can be waken up
			ArrayList<Pcb> wakenPcbs = new ArrayList<Pcb>();
			
			// select pcbs which can be waken up and modify other pcbs' time 
			for (Pcb pcb : waitQueue) {
				
				// check whether can be waken up
				if (pcb.waitTimeLeft == 0) {
					// can be waken up
					
					// add to list
					wakenPcbs.add(pcb);
				} else if (pcb.waitTimeLeft == -1) {
					// blocked due to semaphore
					
					System.out.println("Pcb " + pcb.pcbId + " continue waiting...");
				} else {
					// cannot be waken up
					
					// modify time
					System.out.println("Pcb " + pcb.pcbId + " wait time left " + pcb.waitTimeLeft + "ms.");
					pcb.waitTimeLeft -= 10;
				}
			}
			
			// wake pcbs up
			for (Pcb pcb : wakenPcbs) {
				
				// wake up pcb
				Primitive.WakeUp(pcb, waitQueue, readyQueue);
				
			}
			
			// select in new process
			if (runningQueue.isEmpty() && !readyQueue.isEmpty()) {
				
				// get first pcb in ready queue
				Pcb firstPcb = readyQueue.peek();
				
				// select in first pcb
				Primitive.SelectIn(firstPcb, readyQueue, runningQueue, cpu);
				
				// check whether more instructions left
				if (firstPcb.nowInsIndex >= firstPcb.insNum) {
					// no more instructions left
					
					// withdraw process
					Primitive.Withdraw(firstPcb, runningQueue);
					
					// set task finished
					firstPcb.oriTask.status = Task.TaskStatus.FINISHED;
					System.out.println("Task " + firstPcb.oriTask.taskId + " finished.");
				} else {
					// has more instructions left
					
					// get first instruction
					Instruction firstInstrruction = firstPcb.insList.get(firstPcb.nowInsIndex);
					
					// check time piece left time enough or not
					if (firstPcb.timePieceLeft < firstInstrruction.insLeftTime) {
						// no enough time for next instruction
						
						// exchange out
						Primitive.ExchangeOut(firstPcb, runningQueue, readyQueue, cpu);
						
					} else {
						// has enough time for next instruction
						
						// execute instruction
						int returnCode = executor.Execute(firstInstrruction);
						
						switch (returnCode) {
						case 3:
							// now pcb in wait queue
							break;
						case 0:
						default:
							// modify time
							System.out.println("Instruction " + firstInstrruction.insId + " time left " + firstInstrruction.insLeftTime + "ms, time piece left " + firstPcb.timePieceLeft + "ms.");
							firstInstrruction.insLeftTime -= 10;
							firstPcb.timePieceLeft -= 10;
							break;
						}
					}
				}
				
			}
			
			// modify now time
			nowTime += 10;
			
			// make time delay
			try {
				Thread.sleep(new Double(10 * speedDelay).longValue());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("---------- end ----------");
			System.out.println();
		}
	}

}
