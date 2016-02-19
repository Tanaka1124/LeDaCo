package SearchSwitchStudent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

public class SearchController {

	File parentPath;
	static String presPath = "/project/.pres2/";
	static String BLOCK_LOG = "BLOCK_COMMAND_RECORD";
	static String TEXT_LOG = "TEXTEDIT_RECORD";
	static String TEST_PATH = "H:\\MadeDataSet\\2015\\data";

	AnalysisLogs al = new AnalysisLogs();

	public void getPath() {
		System.out.println("rootのパスが欲しいにゃん");

		Scanner scan = new Scanner(System.in);
		parentPath = new File(scan.nextLine());
		if (parentPath.equals(new File(""))) {
			parentPath = new File(TEST_PATH);
		}
		scan.close();
	}

	public void search() {
		List<File> lectures = Arrays.asList(parentPath.listFiles());
		for (File lec : lectures) {
			searchLogFile(Arrays.asList(lec.listFiles()), lec);
		}
	}

	private void searchLogFile(List<File> students, File lec) {
		Map<String, Map<String, Integer>> switchCounts = new HashMap<>();
		for (File sd : students) {
			File pres = new File(sd, presPath);
			if (pres.exists()) {
				switchCounts.put(sd.getName(), al.analLog(pres));
			}
		}
		printCSV(lec.getName(), switchCounts);
	}

	private void printCSV(String lecture, Map<String, Map<String, Integer>> switchCounts) {
		// とりあえずDeskTopに出力

		/*
		 * System.out.println("保存先を教えるにゃん"); Scanner sc = new
		 * Scanner(System.in); String csvName = sc.nextLine(); sc.close(); if
		 * (csvName.equals("")) { csvName = new
		 * File(System.getProperty("user.home"), "Desktop").toString(); }
		 */
		String csvName = new File(System.getProperty("user.home"), "Desktop").toString();
		csvName = csvName + "/" + System.currentTimeMillis() + "-" + lecture + ".csv";

		System.out.println(csvName);
		try (FileWriter fw = new FileWriter(csvName, false); PrintWriter pw = new PrintWriter(new BufferedWriter(fw))) {

			for (Entry<String, Map<String, Integer>> sd : switchCounts.entrySet()) {
				for (Entry<String, Integer> entry : sd.getValue().entrySet()) {
					if (!entry.getValue().equals(0)) {
						StringBuilder sb = new StringBuilder();
						sb.append(sd.getKey());
						sb.append(",");
						sb.append(entry.getKey());
						sb.append(",");
						sb.append(entry.getValue());
						pw.println(sb);
						System.out.println(sb);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Map<String, Integer> analLog(File pres) {
		File pres2Log = new File(pres, "pres2.log");
		Map<String, Integer> switchCount = new HashMap<>();
		Map<String, Boolean> state = new HashMap<>();
		try (Scanner scan = new Scanner(pres2Log);) {
			// type
			// -1 -> error
			// 0 -> java
			// 1 -> Block
			int type = 0;
			while (scan.hasNext()) {
				String tmp = scan.nextLine();
				// 開始タグなら該当するJavaファイルのBlockWorkingをTrueしてカウント++
				// 終了タグなら該当するJavaファイルのBWをfalseでカウント++
				// それ以外はスルー

				int nextType = BlockOrJava(tmp);

				if (nextType != -1) {// エラー行をはじく
					if (nextType != type) {// 表示が切り替わったら
						String fn = fileName(tmp);
						if (fn != null) {
							if (switchCount.containsKey(fn)) {
								switchCount.put(fn, switchCount.get(fn) + 1);
							} else {
								switchCount.put(fn, 0);
							}

						}
					}
					type = nextType;
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return switchCount;
	}

	private String fileName(String line) {
		String[] tmp = line.split("\\t", 0);
		if (tmp.length < 6) {
			return null;
		}
		if (tmp[2].equals(BLOCK_LOG) || tmp[2].equals(TEXT_LOG) || tmp[2].equals("COMMAND_RECORD")) {
			return tmp[4];
		} else {
			return null;
		}
	}

	private int BlockOrJava(String line) {
		String[] tmp = line.split("\\t", 0);
		if (tmp.length < 3) {
			return -1;
		}
		if (tmp[2].equals(BLOCK_LOG)) {
			return 1;
		}
		return 0;
	}
}
