package allCollectDaraPres;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class SaveAsCSV {
	public void outputCSVData(File selectedFile, Map<String, String> mustCheckTaskNames, List<String> studentNums,
			DataSetsInteger collectedData, String dataType) {

		try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
				new File(selectedFile, dataType + "-" + System.currentTimeMillis() + ".csv"), false)));) { // milli秒.csvで保存
			StringBuilder item = new StringBuilder();
			item.append("StudentNumber");
			item.append(",");
			for (String mctn : mustCheckTaskNames.keySet()) {
				item.append(mctn);
				item.append(",");
			}
			pw.println(item);

			for (String snum : studentNums) {
				StringBuilder line = new StringBuilder();
				line.append(snum);
				line.append(",");
				for (String mctn : mustCheckTaskNames.keySet()) {
					Map<String, Integer> tmp = collectedData.get(snum);
					if (tmp.containsKey(mctn)) {
						line.append(tmp.get(mctn));
						line.append(",");
					} else {
						line.append(0);
						line.append(",");
					}
				}
				pw.println(line);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		System.out.println("出力完了");
	}

	public void outputCSVData(File selectedFile, Map<String, String> mustCheckTaskNames, List<String> studentNums,
			DataSetsLong collectedData, String dataType) {
		try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
				new File(selectedFile, dataType + "-" + System.currentTimeMillis() + ".csv"), false)));) { // milli秒.csvで保存
			StringBuilder item = new StringBuilder();
			item.append("StudentNumber");
			item.append(",");
			for (String mctn : mustCheckTaskNames.keySet()) {
				item.append(mctn);
				item.append(",");
			}
			pw.println(item);

			for (String snum : studentNums) {
				StringBuilder line = new StringBuilder();
				line.append(snum);
				line.append(",");
				for (String mctn : mustCheckTaskNames.keySet()) {
					Map<String, Long> tmp = collectedData.get(snum);
					if (tmp.containsKey(mctn)) {
						line.append(tmp.get(mctn));
						line.append(",");
					} else {
						line.append(0);
						line.append(",");
					}
				}
				pw.println(line);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		System.out.println("出力完了");
	}
}
