package MakeSwitchTimeline;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MakeTL {
	File logRootPath;
	String setFile = "set.txt";

	List<List<String>> tls = new ArrayList<>();

	List<String> sets = new ArrayList<>();

	private void run() {
		System.out.println("LogのあるRootのPathが欲しいにゃん");
		Scanner scan = new Scanner(System.in);
		logRootPath = new File(scan.nextLine());
		// for debug
		if (logRootPath.equals(new File(""))) {
			logRootPath = new File("C:/Users/sakailab/Desktop/switch分析");
		}

		scan.close();

		setRead();
		for (String line : sets) {
			String[] elements = spilitLine(line);
			makeList(elements[1], elements[0]);

		}
		// printCSV();
		new DrawTL(tls);

	}

	private void setRead() {
		try (Scanner setScan = new Scanner(new File(logRootPath, setFile));) {
			while (setScan.hasNext()) {
				String tmp = setScan.nextLine();

				sets.add(tmp);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private void printCSV() {
		String csvName = new File(System.getProperty("user.home"), "Desktop").toString();
		csvName = csvName + "/" + System.currentTimeMillis() + ".csv";
		try (FileWriter fw = new FileWriter(csvName, false); PrintWriter pw = new PrintWriter(new BufferedWriter(fw))) {

			for (List<String> tl : tls) {
				StringBuilder sb = new StringBuilder();
				for (String l : tl) {
					sb.append(l);
					sb.append(",");
				}
				pw.println(sb);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void makeList(String javaName, String logPath) {

		List<Long> javaTimemil = new ArrayList<>();
		List<Long> blockTimemil = new ArrayList<>();
		try (Scanner logScan = new Scanner(new File(logRootPath, logPath + "/pres2.log"));) {
			boolean btjFlag = false;
			while (logScan.hasNext()) {

				String tmp = logScan.nextLine();

				String[] elements = spilitLine(tmp);

				if (elements.length <= 4) {
					continue;
				}

				if (!elements[4].equals(javaName)) {
					continue;
				}
				if (elements[3].equals("BLOCK_TO_JAVA")) {
					btjFlag = true;
				}
				if ((elements[2].equals("TEXTEDIT_RECORD") || elements[2].equals("COMMAND_RECORD")) && !btjFlag) {
					javaTimemil.add(Long.parseLong(elements[0]));
				}
				if (elements[2].equals("BLOCK_COMMAND_RECORD") && elements[3].equals("ANY")) {
					blockTimemil.add(Long.parseLong(elements[0]));
					btjFlag = false;
				}

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("ListSize " + blockTimemil.size() + " " + javaTimemil.size());
		if (javaTimemil.size() == 0 || blockTimemil.size() == 0) {
			System.out.println("switchが1度も無いにゃん");
		} else {
			makeTL(javaTimemil, blockTimemil, logPath + javaName);
		}
	}

	private String[] spilitLine(String line) {
		String[] tmp = line.split("\\t", 0);

		return tmp;
	}

	private void makeTL(List<Long> javaTimemil, List<Long> blockTimemil, String caseName) {
		List<String> tl = new ArrayList<>();
		System.out.println("TL作成開始");
		tl.add(caseName);
		int switchCount = 0;
		long startmil;
		boolean JavaTime = true;
		if (blockTimemil.get(0) < javaTimemil.get(0)) {
			startmil = blockTimemil.get(0);
			tl.add("B");
			JavaTime = false;
			System.out.println(caseName + " Block Start");
			switchCount++;
		} else {
			startmil = javaTimemil.get(0);
			tl.add("J");
			JavaTime = true;
			switchCount++;
		}

		// Todo 入れ替わったtimemillsを配列にぶち込んでいく
		tl.add("0");
		int blockIndex = 0;
		int javaIndex = 0;
		while ((blockIndex + 1) < blockTimemil.size() && (javaIndex + 1) < javaTimemil.size()) {
			if (blockTimemil.get(blockIndex) < javaTimemil.get(javaIndex)) {
				if (JavaTime) {
					tl.add(String.valueOf(blockTimemil.get(blockIndex) - startmil));
					switchCount++;
					JavaTime = false;
				}
				blockIndex++;
			} else {
				if (!JavaTime) {
					tl.add(String.valueOf(javaTimemil.get(javaIndex) - startmil));
					switchCount++;
					JavaTime = true;
				}
				javaIndex++;
			}

		}
		System.out.println(caseName + " switch count = " + switchCount);
		tls.add(tl);
	}

	@SuppressWarnings("unused")
	private void oldmakeTL(List<Long> javaTimemil, List<Long> blockTimemil, String caseName) {
		List<String> tl = new ArrayList<>();
		System.out.println("TL作成開始");
		tl.add(caseName);
		long startmil;
		if (blockTimemil.get(0) < javaTimemil.get(0)) {
			startmil = blockTimemil.get(0);
		} else {
			startmil = javaTimemil.get(0);
		}

		int blockIndex = 0;
		int javaIndex = 0;
		int loopCount = 1;
		int lastBI = 0;
		int lastJI = 0;
		int divisions = 1000 * 30 * 1;
		while ((blockIndex + 1) < blockTimemil.size() && (javaIndex + 1) < javaTimemil.size()) {
			int editCounter = 0;
			while (((loopCount * divisions) + startmil) > blockTimemil.get(blockIndex)
					&& (blockIndex + 1) != blockTimemil.size()) {
				blockIndex++;
				editCounter++;
			}
			while (((loopCount * divisions) + startmil) > javaTimemil.get(javaIndex)
					&& (javaIndex + 1) != javaTimemil.size()) {
				javaIndex++;
				editCounter--;
			}

			if (lastBI == blockIndex && lastJI == javaIndex) {
				System.out.print("0");
				tl.add("0");
			} else if (editCounter < 0) {
				System.out.print("J");
				tl.add("J");
			} else {
				System.out.print("B");
				tl.add("B");
			}
			lastBI = blockIndex;
			lastJI = javaIndex;
			loopCount++;
		}
		System.out.println("\nloop count " + loopCount);
		tls.add(tl);
	}

	public static void main(String[] args) {
		new MakeTL().run();
	}

}
