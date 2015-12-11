package allClassData;

import java.io.File;

public class TaskDetail {
	File taskPath;
	File pres2logPath;

	long JavaProgrammingTime;
	long BlockProgrammingTime;
	int BlockPrintSize;
	int CompileCount;
	int CompileCorrectionTime;

	public TaskDetail(File jf) {
		taskPath = jf;
		setPres2rogPath();
	}

	private void setPres2rogPath() {
		pres2logPath = new File(taskPath.getParent(), ".pres2\\pres2.log");
	}
}
