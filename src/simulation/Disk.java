package simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Disk {
	
	private final int trackNum;
	private final int sectorNum;
	private final int sectorSize;
	private RandomAccessFile storage;
	
	public Disk() {
		trackNum = 32; // 32 tracks
		sectorNum = 64; // 64 sectors
		sectorSize = 512; // 512 bytes per sector
		try {
			if (!isDiskFileExist()) {
				createDiskFile();
			}
			storage = new RandomAccessFile("disk", "rw");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean isDiskFileExist() {
		File file = new File("disk");
		return file.exists();
	}
	
	private void createDiskFile() {
		File file = new File("disk");
		try {
			file.createNewFile();
			FileOutputStream fop = new FileOutputStream(file);
			byte b = 0;
			for (int i = 0; i < trackNum; i++) {
				for (int j = 0; j < sectorNum; j++) {
					for (int k = 0; k < sectorSize; k++) {						
						fop.write(b);
					}
				}
			}
			fop.flush();
			fop.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public byte get(int trackId, int sectorId, int offset) {
		byte b = 0;
		if (seekPos(trackId, sectorId, offset)) {
			try {
				b = storage.readByte();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return b;
	}
	
	public void set(int trackId, int sectorId, int offset, byte data) {
		if (seekPos(trackId, sectorId, offset)) {
			try {
				storage.writeByte(data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private boolean seekPos(int trackId, int sectorId, int offset) {
		if (trackId >= 0 && trackId < trackNum) {
			if (sectorId >= 0 && sectorId < sectorNum) {
				if (offset >= 0 && offset < sectorSize) {
					long pos = trackId * (sectorNum * sectorSize) + sectorId * sectorSize + offset;
					try {
						storage.seek(pos);
						return true;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return false;
	}
}
