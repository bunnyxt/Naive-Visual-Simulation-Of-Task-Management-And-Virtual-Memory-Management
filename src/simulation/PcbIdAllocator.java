package simulation;

public class PcbIdAllocator {
	
	private static int nextPcbId = 0;
	
	public static int getNextPcbId() {
		return nextPcbId++;
	}
	

}
