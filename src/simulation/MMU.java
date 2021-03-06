package simulation;

public class MMU {
	
	int Log_to_Phy(Pcb pcb, int logAddress, FastTable fastTable, PageTable pageTable, Memory memory, ExchangeArea exchangeArea) {

		int pageid, offset, blockid, phyAddress = 0;
		int flag;
		pageid = logAddress / 256;
		offset = logAddress % 256;
		
		// 访问快表
		System.out.println("正在访问快表");
		flag = fastTable.IsInTable(pageid); // 是否在快表中
		if (flag == 1) { // 快表命中
			blockid = fastTable.SearchPageId(pageid); // 搜索快表，输入页号，返回块号
			phyAddress = blockid * 256 + offset; // 得到物理地址
			System.out.println("快表命中，物理地址为：" + phyAddress);
			fastTable.ChangeVisit(pageid);  // 修改快表访问次数
			pageTable.ChangeVisit(pageid);  // 修改页表访问次数
			fastTable.DispalyFastTable();	// 输出快表内容			
			pageTable.DispalyPageTable();   // 输出页表内容
		} else { // 快表没有命中 
			System.out.println("快表未命中，进入页表"); 	
			// must in page table
			if (pageTable.page[pageid].Dwell == 1) {
				// in memory
				blockid = pageTable.SearchPageId(pageid); // 搜索页表，输入页号，返回块号
				phyAddress = blockid * 256 + offset; // 得到物理地址
				System.out.println("页表命中，物理地址为：" + phyAddress);
				pageTable.ChangeVisit(pageid); // 修改页表访问次数
				// 修改快表
				if (fastTable.usedlength < fastTable.length) { // 快表没有满，添加
					Page addpage = new Page(pageid, blockid, 1, 0);
					fastTable.InsertPage(addpage, fastTable.usedlength); // 添加表项 
					fastTable.ChangeVisit(pageid);
					fastTable.usedlength += 1;
				} else { // 快表满了 替换
					int exchange = fastTable.MaxVisitcount();
					System.out.println("快表满了，把快表中的第" + exchange + "项换出");	
					Page addpage = new Page(pageid, blockid, 1, 0);
					fastTable.InsertPage(addpage, exchange);
					fastTable.ChangeVisit(pageid);
				}
				fastTable.DispalyFastTable();
				pageTable.DispalyPageTable();
			} else {
				//not in memory
				int exchange = pageTable.MaxVisitcount();
				System.out.println("页表满了，把页表中的第" + exchange + "项换出");	
				blockid = pageTable.SearchPageId(exchange); // 页号为索引搜索页表
				if (pageTable.page[exchange].changed) {
					// changed, need write back
					int exchangeAreaId = pageTable.page[exchange].exchangeAreaId;
					int traceId = exchangeAreaId / exchangeArea.disk.trackNum;
					int sectorId = exchangeAreaId % exchangeArea.disk.trackNum;
					for (int i = 0; i < 256; i++) {
						int address = blockid * 256 + i;
						short data = memory.get(address);
						exchangeArea.disk.set(traceId, sectorId, i * 2, (byte)(data % 256));
						exchangeArea.disk.set(traceId, sectorId, i * 2 + 1, (byte)(data / 256));
					}
				}
				
				pageTable.page[pageid].BlockId = pageTable.page[exchange].BlockId;
				pageTable.page[pageid].changed = false;
				pageTable.page[pageid].Dwell = 1;
				
				pageTable.page[exchange].BlockId = -1;
				pageTable.page[exchange].Dwell = 0; // not in memory
				
				//select in
				int memoryBlock = blockid;
				int exchangeAreaBlock = pageTable.page[pageid].exchangeAreaId;
				int trackId = exchangeArea.records[exchangeAreaBlock].trackId;
				int sectorId = exchangeArea.records[exchangeAreaBlock].sectorId;
				for (int j = 0; j < 256; j++) {
					int memAddress = memoryBlock * 256 + j;
					short data = (short)(exchangeArea.disk.get(trackId, sectorId, j * 2) + exchangeArea.disk.get(trackId, sectorId, j * 2 + 1) << 8);
					memory.set(memAddress, data);
				}
				
				pageTable.ChangeVisit(pageid);
			}
			/*
			flag = pageTable.IsInTable(pageid); // 是否在页表中
			if (flag == 1) { // 页表命中
				blockid = pageTable.SearchPageId(pageid); // 搜索页表，输入页号，返回块号
				phyAddress = blockid * 256 + offset; // 得到物理地址
				System.out.println("页表命中，物理地址为：" + phyAddress);
				pageTable.ChangeVisit(pageid); // 修改页表访问次数
				// 修改快表
				if (fastTable.usedlength < fastTable.length) { // 快表没有满，添加
					Page addpage = new Page(pageid, blockid, 1, 0);
					fastTable.InsertPage(addpage, fastTable.usedlength); // 添加表项 
					fastTable.ChangeVisit(pageid);
					fastTable.usedlength += 1;
				} else { // 快表满了 替换
					i = fastTable.MaxVisitcount();
					System.out.println("快表满了，把快表中的第" + i + "项换出");	
					Page addpage = new Page(pageid, blockid, 1, 0);
					fastTable.InsertPage(addpage, i);
					fastTable.ChangeVisit(pageid);
				}
				fastTable.DispalyFastTable();
				pageTable.DispalyPageTable();
			} else { // 页表没有命中
				System.out.println("页表未命中，产生缺页中断");		
				// 判断页表满没有
				if (pageTable.usedlength < pageTable.length) { // 没满
					System.out.println("页表没满，进行添加");	
					blockid = fenpei[pageTable.usedlength];
					Page addpage = new Page(pageid, blockid, 1, 0);
					pageTable.InsertPage(addpage, pageTable.usedlength);
					pageTable.ChangeVisit(pageid); // 修改页表访问次数
					pageTable.usedlength += 1;
				} else { // 满了
					i = pageTable.MaxVisitcount();
					System.out.println("页表满了，把页表中的第" + i + "项换出");	
					blockid = pageTable.SearchPageId(i); // 页号为索引搜索页表
					pageTable.Change(i, pageid);
					pageTable.ChangeVisit(pageid);
				}
				// 修改快表
				if (fastTable.usedlength < fastTable.length) { // 快表没有满 添加
					System.out.println("快表没满，进行添加");	
					Page addpage = new Page(pageid, blockid, 1, 0);
					fastTable.InsertPage(addpage, fastTable.usedlength);
					fastTable.ChangeVisit(pageid);
					fastTable.usedlength += 1;
				} else { // 快表满了 替换
					i = fastTable.MaxVisitcount();
					System.out.println("快表满了，把快表中的第" + i + "项换出");	
					fastTable.page[i].PageId = pageid;
					fastTable.page[i].BlockId = blockid;
					fastTable.page[i].Dwell = 1;
					fastTable.page[i].visitcount = 0;
					fastTable.ChangeVisit(pageid);
				}
				phyAddress = blockid * 256 + offset;
				System.out.println("重新执行指令");
				System.out.println("快表命中，物理地址为：" + phyAddress);
			}						
			fastTable.DispalyFastTable();
			pageTable.DispalyPageTable();
			*/
		}
		
		return phyAddress;
	}
}
