package allClassData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LectureData {

	Map<String, Map<String, TaskDetail>> lecture = new HashMap<>();
	TaskData td = new TaskData();

	public Map<String, Map<String, TaskDetail>> createLectureData(File[] lectures, int studentNum) {

		File[] submittedLectures = searchSubmittedLectures(lectures, studentNum);
		for (File f : submittedLectures) {
			// System.out.println(" CreateData" + f.getName());

			lecture.put(f.getParentFile().getName(), td.createTaskData(f));

		}
		return lecture;
	}

	private File[] searchSubmittedLectures(File[] lectures, int studentNum) {
		List<File> submittedLecture = new ArrayList<>();
		for (File f : lectures) {
			File temp = new File(f, (studentNum + "-" + f.getName()));
			if (temp.exists()) {
				submittedLecture.add(temp);
			}
		}
		return submittedLecture.toArray(new File[0]);
	}
}
