package allClassData;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

public class TaskData {

	Map<String, TaskDetail> taskData = new HashMap<>();

	public Map<String, TaskDetail> createTaskData(File f) {
		File[] javaFiles = searchJavaFiles(f);
		for (File jf : javaFiles) {
			System.out.println("   CreateTask" + jf.toString());
			taskData.put(jf.toString(), new TaskDetail(jf));
		}
		return taskData;
	}

	private File[] searchJavaFiles(File lecturePath) {
		return lecturePath.listFiles(new FileFilter());
	}

}

class FileFilter implements FilenameFilter {
	private final String JAVA_FILE = ".java";

	public boolean accept(File dir, String name) {
		File file = new File(name);
		if (file.isDirectory()) {
			return false;
		}
		return (name.endsWith(JAVA_FILE));
	}
}