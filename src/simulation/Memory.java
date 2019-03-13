package simulation;

class Memory {

	public final int size;
	private short[] storage;
	
	public final int blockNum; //物理块数量  64个
	public BlockTable blockTable;
	public int freeSpace; //剩余空间   以块为单位
	public int usedSpace; //已用空间

	public Memory() {
		size = 16384; // 32KB / 2
		storage = new short[size];
		
		blockNum = 64;
		blockTable = new BlockTable(blockNum);
		freeSpace = blockNum;
		usedSpace = 0;
	}
	
	public short get(int address) {
		if(address >= 0 && address < size) {
			return storage[address];
		} else {
			return 0;
		}
	}
	
	public void set(int address, short data) {
		if(address <= 0 && address < size) {
			storage[address] = data;
		}
	}
	
	public boolean CanAllocate(int size) {
		if (size <= freeSpace) {
			return true;
		} else {
			return false;
		}
	}
	
	//分配资源
	public int[] AllocationSpace(int size, int pcbId) {
		int[] freeBlockIndexes = null;
		if(CanAllocate(size)) {
			freeBlockIndexes = new int[size];
			int count = 0;
			for (int i = 0; i < blockNum; i++) {
				if (blockTable.block[i].BlockState == 0) {
					freeBlockIndexes[count++] = i;
					blockTable.block[i].BlockState = 1;//置占用标志  1
					blockTable.block[i].OwnerPro = pcbId;//该块分配给的作业
					freeSpace--;//从空闲块数中减去本次占用块数
					usedSpace++;
					if (count == size) {
						break;
					}
				}
			}
		}
		return freeBlockIndexes;
	}
	
	//回收资源
	public void RecycleSpace(int pcbId) {
		for (int i = 0; i < blockNum; i++) {
			if(blockTable.block[i].OwnerPro == pcbId) {//找出需要归还的块
				blockTable.block[i].BlockState = 0;//置占用标志为 0
				blockTable.block[i].OwnerPro = -1;//清空占有进程标志位
				freeSpace++;//从空闲块数中减去本次占用块数
				usedSpace--;
			}
		}
	}
	
	//显示内存信息
	public void DisplayMemory() {
		System.out.println("已用空间：" + usedSpace + "页" + "\t剩余空间：" + freeSpace + "页\n");
	}

}
