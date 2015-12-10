package allClassData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ReadOtherData {

	String studentListPath = ".\\data2read\\studentsList.csv";
	List<Integer> studentNum = new ArrayList<>();
	List<String> studentName = new ArrayList<>();

	public ReadOtherData() {
		setStudentData();
	}

	void setStudentData() {
		File studentList = new File(studentListPath);
		List<String> temp = new ArrayList<>();
		try (FileReader fr = new FileReader(studentList.getAbsolutePath().toString());
				BufferedReader br = new BufferedReader(fr)) {

			String line;
			StringTokenizer token;
			while ((line = br.readLine()) != null) {
				token = new StringTokenizer(line, ",");
				for (int i = 0; token.hasMoreTokens(); i++) {
					if (i == 0) {
						temp.add(token.nextToken());
					} else if (i == 1) {
						studentName.add(token.nextToken());
					} else {
						token.nextToken();
					}
				}
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		temp.remove(0);
		for (String t : temp) {
			studentNum.add(Integer.parseInt(t));
		}
		studentName.remove(0);

	}

	public Integer[] getStudentNumber() {

		return (Integer[]) studentNum.toArray(new Integer[0]);
	}

	public String[] getStudentName() {

		return (String[]) studentName.toArray(new String[0]);

	}

	public static void main(String[] args) {
		ReadOtherData rod = new ReadOtherData();
		rod.getStudentNumber();
	}
}
