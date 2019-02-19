package simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TaskLoader {
	
	public static ArrayList<Task> LoadTasks(String fileName) {
		ArrayList<Task> tasks = null;
		File file = new File(fileName);
		if (file.exists()) {
			System.out.println("Now load tasks from file " + fileName + "...");
			tasks = new ArrayList<Task>();
			Task task = null;
			boolean isNewTask = true;
			int insLeft = 0;
			try {
				BufferedReader bf = new BufferedReader(new FileReader(fileName));
				String textLine;
				while((textLine = bf.readLine()) != null){
					if (isNewTask) {
						String[] numbers = textLine.split(" ");
						int taskId = Integer.parseInt(numbers[0]);
						int insNum = Integer.parseInt(numbers[1]);
						int inTime = Integer.parseInt(numbers[2]);
						task = new Task(taskId, insNum, inTime);
						System.out.println("Task " + taskId + " with " + insNum + " instruction(s) proposed at " + inTime + "ms loaded.");
						tasks.add(task);
						insLeft = insNum;
						isNewTask = false;
					} else {
						Instruction ins = null;
						String[] numbers = textLine.split(" ");
						int insId = Integer.parseInt(numbers[0]);
						int insType = Integer.parseInt(numbers[1]);
						int insLeftTime = Integer.parseInt(numbers[2]);
						int reg1;
						int reg2;
						int reg3;
						short imm;
						String op;
						int writeFlag;
						int address;
						int reg;
						int releaseFlag;
						int resourceId;
						switch (insType) {
						case 0:
							reg1 = Integer.parseInt(numbers[3]);
							reg2 = Integer.parseInt(numbers[4]);
							reg3 = Integer.parseInt(numbers[5]);
							op = numbers[6];
							ins = new InstructionCalcRR(insId, insType, insLeftTime, reg1, reg2, reg3, op);
							break;
						case 1:
							reg1 = Integer.parseInt(numbers[3]);
							imm = Short.parseShort(numbers[4]);
							reg2 = Integer.parseInt(numbers[5]);
							op = numbers[6];
							ins = new InstructionCalcRI(insId, insType, insLeftTime, reg1, imm, reg2, op);
							break;
						case 2:
							writeFlag = Integer.parseInt(numbers[3]);
							address = Integer.parseInt(numbers[4]);
							reg = Integer.parseInt(numbers[5]);
							ins = new InstructionIO(insId, insType, insLeftTime, writeFlag, address, reg);
							break;
						case 3:
							releaseFlag = Integer.parseInt(numbers[3]);
							resourceId = Integer.parseInt(numbers[4]);
							ins = new InstructionRes(insId, insType, insLeftTime, releaseFlag, resourceId);
							break;
						default:
							break;
						}
						System.out.println("Instruction " + insId + " : " + ins.ToString() + " with running time " + insLeftTime + "ms loaded.");
						task.insList.add(ins);
						insLeft--;
						if (insLeft == 0) {
							isNewTask = true;
						}
					}
				}
				bf.close();
				System.out.println("Tasks loaded successfully!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		return tasks;
	}

}
