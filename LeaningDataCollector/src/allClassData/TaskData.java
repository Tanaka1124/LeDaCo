package allClassData;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

public class TaskData {

	Map<String, TaskDetail> taskData = new HashMap<>();
	File pres2LogPath;
	TaskDetail td;

	TaskDetailCal tdc = new TaskDetailCal();

	public Map<String, TaskDetail> createTaskData(File f) {
		pres2LogPath = new File(f, ".pres2\\pres2.log");
		File[] javaFiles = searchJavaFiles(f);
		if (javaFiles != null) {
			for (File jf : javaFiles) {
				// System.out.println(" CreateTask" + jf.toString());
				td = new TaskDetail(jf);
				taskData.put(jf.getName(), td);
			}
			tdc.setTaskData(pres2LogPath, javaFiles, taskData);
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