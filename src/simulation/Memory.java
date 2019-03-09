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
		Block block[] = new Block[blockNum];
		for (int i = 0; i < 64; i++) {
			block[i] = new Block(i, 0, -1); // 参数：块号，是否分配，分配的进程号
		}
		blockTable = new BlockTable(block);
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
	
	//分配资源 TODO size-5?
	public int AllocationSpace(int size, int pcbId) {
		if((size - 5) <= freeSpace) {//如果空闲块数满足，则分配
			int i = 0;
			while(size - 5 > 0) {
				if(blockTable.block[i].BlockState == 0) {//找出空闲的块
					blockTable.block[i].BlockState = 1;//置占用标志
					blockTable.block[i].OwnerPro = pcbId;//该块分配给的作业
					freeSpace--;//从空闲块数中减去本次占用块数
					usedSpace++;
					size--;
					//相应的页对应
				}
				i++;
			}
			return 1;//分配成功
		}
		else {//如果空闲块数不足则令进程等待
			// TODO
			return 0;//分配失败
		}
	}
	
	//回收资源
	public void RecycleSpace(int size, int ProId) {
		int i = 0;
		while(size > 0) {
			if(blockTable.block[i].OwnerPro == ProId) {//找出需要归还的块
				blockTable.block[i].BlockState = 0;//置占用标志为 0
				blockTable.block[i].OwnerPro = -1;//清空占有进程标志位
				freeSpace++;//从空闲块数中减去本次占用块数
				usedSpace--;
				size--;
			}
			i++;
		}
	}
	
	//显示内存信息
	public void DisplayMemory() {
		System.out.println("已用空间：" + usedSpace + "页" + "\t剩余空间：" + freeSpace + "页\n");
	}

}
