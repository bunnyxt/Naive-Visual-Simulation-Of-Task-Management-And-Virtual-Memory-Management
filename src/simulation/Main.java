package simulation;

public class Main {

	public static void main(String[] args) {
		Memory memory = new Memory();
		System.out.println(memory.size);
		
		Disk disk = new Disk();

		byte data = 100;
		for(int i = 0; i < 100; i++) {
			disk.set(0, 0, i, data);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
