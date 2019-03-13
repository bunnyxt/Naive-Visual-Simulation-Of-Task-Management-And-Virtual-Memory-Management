package simulation;

public class Page {
	
	int PageId;         //页号，从 0 开始编号
	int BlockId;        //块号，从 0 开始编号
	int exchangeAreaId;
	int Dwell;          //驻留标志位，0 表示不在内存，1 表示在内存
	int visitcount;     //访问次数  访问到了置0 没有加1
	boolean changed;
	
	Page(){
		
	}
	
	Page(int PageId, int BlockId, int Dwell, int visitcount) {
		this.PageId = PageId;       
		this.BlockId = BlockId;     
		this.exchangeAreaId = -1;
		this.Dwell = Dwell;                 
		this.visitcount = visitcount;
		this.changed = false;
	}
	
	//初始化函数
	void Init(int id, int dwell) {
		PageId = id;
		Dwell = dwell;
	}
	
} 