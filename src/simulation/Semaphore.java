package simulation;

import java.util.LinkedList;
import java.util.Queue;

class Semaphore {
	
	public int value;
	
	public Queue<Pcb> list;
	
	public Semaphore(int value) {
		this.value = value;
		list = new LinkedList<Pcb>();
	}
	
	public static int P(Semaphore s, Queue<Pcb> runningQueue, Queue<Pcb> waitQueue, Cpu cpu) {
		int returnCode = 0;
		s.value--;
		if (s.value < 0) {
			Pcb pcb = runningQueue.peek();
			if (pcb != null) {
				Primitive.Wait(pcb, runningQueue, waitQueue, -1, cpu);
				s.list.offer(pcb);
				returnCode = 3;
			}
		}
		return returnCode;
	}
	
	public static int V(Semaphore s, Queue<Pcb> waitQueue, Queue<Pcb> readyQueue) {
		int returnCode = 0;
		s.value++;
		if (s.value <= 0) {
			Pcb pcb = s.list.poll();
			if (pcb != null) {
				Primitive.WakeUp(pcb, waitQueue, readyQueue);
			}
		}
		return returnCode;
	}
	
}
