package allClassData;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class StudentData {

	Map<Integer, Map<String, Map<String, TaskDetail>>> student = new HashMap<>();
	LectureData ld;
	File[] lectures;

	public StudentData(File[] lectures) {
		this.lectures = lectures;
	}

	public void createStudentData(Integer[] num, String[] name) {
		for (int i = 0; i < num.length; i++) {
			System.out.println(" CreateStudent" + num[i]);
			ld = new LectureData();
			student.put(num[i], ld.createLectureData(lectures, (int) num[i]));

		}
	}

}
