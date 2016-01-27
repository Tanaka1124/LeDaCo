package allCollectDaraPres;

import java.util.ArrayList;
import java.util.List;

public class CombineStudent {
	String studentNum;
	String lecture;
	List<Task> tasks;

	public CombineStudent(String studentNum, String lecture) {
		this.studentNum = studentNum.split("-", 0)[0];
		this.lecture = lecture;
		tasks = new ArrayList<>();
	}

	public void setFileName(String collectName, String studentName) {
		for (Task task : tasks) {
			if (task.cn == collectName) {
				task.studentFileName.add(studentName);
				return;
			}
		}
		Task tmp = new Task();
		tmp.cn = collectName;
		tmp.studentFileName.add(studentName);
		tasks.add(tmp);
	}
}

class Task {
	List<String> studentFileName;
	String cn;

	public Task() {
		studentFileName = new ArrayList<>();
	}
}