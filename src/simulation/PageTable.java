package simulation;

public class PageTable extends Table {
	
	PageTable(Page[] page1, int i) {
		super(page1, i);
	}
	
	//页表信息
	void DispalyPageTable() { 
		System.out.println("-------------当前页表-----------");
		System.out.println("页号\t" + "物理块号\t" + "访问次数\t"); 
		for(int i = 0; i < length; i++) { 					
			System.out.println(page[i].PageId + "\t" + "  " + page[i].BlockId + "\t" + "  " + page[i].visitcount); 			
		} 
		System.out.println("------------------------------");
	} 
	
	//把新的替换进来
	void Change(int i, int pageid) {
		page[i].PageId = pageid;
	}	
}
