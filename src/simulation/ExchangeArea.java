package simulation;

class ExchangeArea {

	public final int size;
	public Disk disk;
	
	public ExchangeAreaRecord[] records;
	public int freeSpace;
	
	public ExchangeArea(Disk disk) {
		size = 1024; // can store 1024 blocks
		this.disk = disk;
		this.records = new ExchangeAreaRecord[size];
		for (int i = 0; i < size; i++) {
			records[i] = new ExchangeAreaRecord(i, i / disk.trackNum, i % disk.trackNum);
		}
		this.freeSpace = size;
	} 
	
	public boolean CanAllocate(int size) {
		if (size <= freeSpace) {
			return true;
		} else {
			return false;
		}
	}
	
	public int[] AllocationSpace(int size, int pcbId) {
		int[] freeRecordIndexes = null;
		if(CanAllocate(size)) {
			freeRecordIndexes = new int[size];
			int count = 0;
			for (int i = 0; i < this.size; i++) {
				if (!records[i].occupied) {
					freeRecordIndexes[count++] = i;
					records[i].occupied = true;//置占用标志
					records[i].pcbId = pcbId;//该块分配给的作业
					freeSpace--;//从空闲块数中减去本次占用块数
					if (count == size) {
						break;
					}
				}
			}
		}
		return freeRecordIndexes;
	}
	
	//回收资源
	public void RecycleSpace(int pcbId) {
		for (int i = 0; i < size; i++) {
			if(records[i].pcbId == pcbId) {//找出需要归还的块
				records[i].occupied = false;//置占用标志为 0
				records[i].pcbId = -1;//清空占有进程标志位
				records[i].logBlockId = -1;
				freeSpace++;//从空闲块数中减去本次占用块数
			}
		}
	}
	
}
