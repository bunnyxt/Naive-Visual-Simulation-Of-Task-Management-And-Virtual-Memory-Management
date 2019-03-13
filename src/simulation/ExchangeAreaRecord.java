package simulation;

class ExchangeAreaRecord {
	
	public int recordId;
	public int trackId;
	public int sectorId;
	public boolean occupied;
	public int pcbId;
	public int logBlockId;
	
	public ExchangeAreaRecord(int recordId, int trackId, int sectorId) {
		this.recordId = recordId;
		this.trackId = trackId;
		this.sectorId = sectorId;
		this.occupied = false;
		this.pcbId = -1;
		this.logBlockId = -1;
	}

}
