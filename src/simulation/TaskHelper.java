package simulation;

import java.util.ArrayList;

public class TaskHelper {
	
	public static boolean AllTasksFinished(ArrayList<Task> tasks) {
		boolean result = true;
		for (Task task : tasks) {
			if (task.status != Task.TaskStatus.FINISHED) {
				result = false;
				break;
			}
		}
		return result;
	}
	
}
