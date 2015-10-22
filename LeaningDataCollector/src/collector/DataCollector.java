package collector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class DataCollector {
	static final String TEST_DIR_PASS = "C:\\Users\\sakailab\\Desktop\\PRO2015-第2回課題提出-141_20159208930";
	static final String TEST_CSV_PASS = "C:/Users/sakailab/Desktop/javablock.csv";
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
		csvFileWriter();
	}

	public void csvFileWriter() {
		// String path = "aaa";
		// System.out.println("出力先のフォルダを選択");
		// try (BufferedReader in = new BufferedReader(new
		// InputStreamReader(System.in))) {
		// path = in.readLine();
		// } catch (IOException ex) {
		// ex.printStackTrace();
		// }
		try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(TEST_CSV_PASS, false)));) { // なぜかnullぽ
			pw.println("StudentName" + "," + "Java times " + "," + "Block times");
			for (int i = 0; i < logFiles.length; i++) {
				pw.println((studentNames[i] + "," + ptc.getTextFocusTime()[i] + "," + ptc.getBlockFocusTime()[i]));

			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println("csv書き出し完了");
	}

	public static void main(String[] args) throws IOException {
		DataCollector dc;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("学習者のプロジェクトファイル一覧がある階層のパス");
		dc = new DataCollector(TEST_DIR_PASS);
		// dc = new DataCollector(in.readLine());
		in.close();
		dc.run();

	}
}
