package simulation;

public abstract class Table {
	Page page[];
	int length;
	int usedlength;
	
	//方法
	Table(Page page1[], int i) {
		page = page1;
		length = i;
		usedlength = 0;
	}
	
	//添加插入一个页表项 
	void InsertPage(Page x, int i) {
		page[i] = x;
	}
	
	//判断在页表中没有
	int IsInTable(int id) {
		for(int i = 0; i < length; i++) {
			if(page[i].PageId == id) {
				return 1;
			}
		}
		return -1;
	}
	
	//根据页号，查找页表中的某个页表项 ，返回物理块号
	int SearchPageId(int pageid) {
		int i, blockid = 0;
		for(i = 0; i < length; i++) {
			if(page[i].PageId == pageid) {
				blockid = page[i].BlockId;
			}
		}
		return blockid;
	}
	
	//替换哪个
	int MaxVisitcount() { 
		int i ,t = 0; 
		for(i = 1; i < length; i++) { 
			if(this.page[i].visitcount > this.page[t].visitcount) { 
				t = i; 
			}   
		} 
		return t; 
	}
	
	//修改访问次数
	void ChangeVisit(int pageid) {
		for(int i = 0; i < length; i++) {
			if(page[i].PageId != pageid) {
				page[i].visitcount = page[i].visitcount + 1;
			} else {
				page[i].visitcount = 0;
			}
		}
	}
}
