package simulation;

class Memory {

	public final int size;

	private short[] storage;

	public Memory() {
		size = 16384; // 32KB / 2
		storage = new short[size];
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

}
