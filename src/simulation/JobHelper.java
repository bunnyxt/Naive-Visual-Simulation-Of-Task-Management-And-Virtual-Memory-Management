package simulation;

import java.util.ArrayList;

public class JobHelper {
	
	public static boolean AllJobsFinished(ArrayList<Jcb> jobs) {
		boolean result = true;
		for (Jcb job : jobs) {
			if (job.status != Jcb.JobStatus.FINISHED) {
				result = false;
				break;
			}
		}
		return result;
	}
	
}
