package allClassData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CollectAllDataMain {
	public static final String TARGET_FILES = ".\\targetFiles";
	public static String allProjectDataRoot;
	ReadOtherData rod = new ReadOtherData();
	StudentData sd;

	public static void main(String[] args) {
		setAllProjectRoot();
		CollectAllDataMain cdm = new CollectAllDataMain();
		cdm.madeAllDataframe();
	}

	private void madeAllDataframe() {
		File[] lectures = searchLectures();
		sd = new StudentData(lectures);
		System.out.println("CreateDataReady");
		sd.createStudentData(rod.getStudentNumber(), rod.getStudentName());
	}

	private File[] searchLectures() {
		File[] temp = new File(allProjectDataRoot).listFiles();
		List<File> lectures = new ArrayList<>();
		for (File f : temp) {
			if (f.isDirectory()) {
				lectures.add(f);
			}
		}
		return temp;
	}

	static void setAllProjectRoot() {
		if (new File(TARGET_FILES).list() == null) {
			Scanner sc = new Scanner(System.in);
			System.out.println("読み込むデータのパスを教えて下さい");
			allProjectDataRoot = sc.next();
			sc.close();
		} else {
			allProjectDataRoot = TARGET_FILES;
		}

	}
}
