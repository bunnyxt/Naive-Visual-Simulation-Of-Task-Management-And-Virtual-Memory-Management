package simulation;

public class Block {
	
	int BlockId;        //物理块 ID
	int BlockState;     //物理块分配状态（0 表示空闲，1 表示占有）
	int OwnerPro;       //分配给的进程 ID
	
	Block(int BlockId, int BlockState, int OwnerPro) {
		this.BlockId = BlockId;//物理块 ID
		this.BlockState = BlockState;//物理块分配状态（0 表示空闲，1 表示占有）
		this.OwnerPro = OwnerPro;//分配给的进程 ID
	}
	
}
