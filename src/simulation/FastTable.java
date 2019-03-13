package simulation;

public class FastTable extends Table {
	
	FastTable(int pageNum) {
		super(pageNum);
		Clear();
	}
	
	public void Clear() {
		page = new Page[length];
		for (int i = 0; i < length; i++) {
			page[i] = new Page(-1, -1, 0, 0);
		}
		usedlength = 0;
	}

	//快表信息
	void DispalyFastTable() { 
		System.out.println("------------当前快表------------");
		System.out.println("页号\t" + "物理块号\t" + "访问次数\t"); 
		for(int i = 0; i < length; i++) { 					
			System.out.println(page[i].PageId + "\t" + "  " + page[i].BlockId + "\t" + "  " + page[i].visitcount); 			
		} 
		System.out.println("-------------------------------");
	} 
}
