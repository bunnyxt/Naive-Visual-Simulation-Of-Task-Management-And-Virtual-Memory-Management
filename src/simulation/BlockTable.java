package simulation;

//系统要建立一张内存物理块表，用来记录内存物理块状态，管理内存物理块分配情况，
//所包含的信息有内存总块数、哪些为空闲块、哪些已分配及分配给哪个进程等。
public class BlockTable {
	
	Block block[]; 
	int length; //表长
	
	//方法
	BlockTable(int blockNum) {
		block = new Block[blockNum];
		for (int i = 0; i < blockNum; i++) {
			block[i] = new Block(i, 0, -1); // 参数：块号，是否分配，分配的进程号
		}
	} //构造函数
	
	void InsertBlock(Block x, int i) {
		block[i] = x;
	}//在 表的第 i 个数据元素之前插入一个Block x 
	
	void SearchBlock(int proid, int fenpei[]) {
		int i, j = 0;
		for(i = 0; i < 64; i++) {
			if(block[i].OwnerPro == proid) {
				fenpei[j] = block[i].BlockId;
				j++;
			}
		}
	}
	
	void DeleteBlockID(int id){
		
	}//根据物理块 ID，在物理块表中删除相应物理块
	
}
