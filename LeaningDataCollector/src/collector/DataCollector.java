package collector;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class DataCollector {
	DevideJavaAndBlock djb;
	ProgrammingTimeCollector ptc;
	String[] studentNames;
	File[] logFiles;
	File rootFile = null;

	public DataCollector(String pf) {
		rootFile = new File(pf);
		printLog(rootFile.getName() + "内のデータを集めます");
		studentNames = getStudentNames();
		logFiles = getLogFiles();
	}

	private String[] getStudentNames() {
		String[] dirName = rootFile.list();
		String[] name = new String[dirName.length];
		for (int i = 0; i < dirName.length; i++) {
			String[] buf = dirName[i].split("_", 0);
			name[i] = buf[0];
		}
		return name;

	}

	private File[] getLogFiles() {
		File[] lg = new File[studentNames.length];
		int index = 0;
		int noPreslog = 0;
		printLog("pres2.log 探索開始");
		for (File f : rootFile.listFiles()) {
			File buf = new File(f, "\\.pres2\\pres2.log");

			if (buf.exists()) {
				lg[index] = buf;

				index++;
			} else {
				System.out.println("・" + studentNames[index] + "　　pres2.logなし");
				lg[index] = null;
				index++;
				noPreslog++;
			}
		}
		printLog("pres2.log 探索終了（全" + (index - noPreslog) + "件中　pres2.logなし" + noPreslog + "件）");
		return lg;

	}

	private void printLog(String s) {

		System.out.println("---------------" + s + "---------------");

	}

	private void run() {
		djb = new DevideJavaAndBlock();
		try {
			ptc = new ProgrammingTimeCollector(logFiles);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) throws IOException {
		DataCollector dc;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("学習者のプロジェクトファイル一覧がある階層のパス");

		dc = new DataCollector(in.readLine());
		dc.run();

	}
}