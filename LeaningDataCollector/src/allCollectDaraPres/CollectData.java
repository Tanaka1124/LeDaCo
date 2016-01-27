package allCollectDaraPres;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CPath;
import pres.loader.model.PLFile;
import pres.loader.model.PLProject;
import tea.analytics.CompileErrorAnalyzerList;
import tea.analytics.model.TCompileErrorHistory;

public class CollectData {
	private DataSetsInteger compileCount;
	private DataSetsInteger runCount;
	private DataSetsLong textWorkTime;
	private DataSetsLong blockWorkTime;

	public CollectData(List<String> studentNums) {
		compileCount = new DataSetsInteger(studentNums);
		runCount = new DataSetsInteger(studentNums);
		textWorkTime = new DataSetsLong(studentNums);
		blockWorkTime = new DataSetsLong(studentNums);
	}

	public void dataCollect(List<CDirectory> lectures, Map<String, String> identifiNames,
			Map<String, String> mustCheckTaskNames) {
		// makeDataSet();
		// lectures = taskRootPath.getDirectoryChildren();
		List<CombineStudent> combineList = new ArrayList<>();
		for (CDirectory lec : lectures) {
			System.out.println(lec.toString());
			List<CDirectory> students;
			students = lec.getDirectoryChildren();
			for (CDirectory sd : students) {

				try {
					// for ppv.data
					CDirectory project = new CDirectory(new CPath(new File(sd.toJavaFile(), "project")));
					PLProject pr = new PLProject(project.getNameByString(), project, project.getAbsolutePath());
					pr.load();
					// pr.compileAllTime(workDirForProject, libDir);
					CombineStudent tmp = new CombineStudent(sd.toString(), lec.toString());
					for (PLFile plFile : pr.getRootPackage().getFilesRecursively()) {
						// Middleだけは，全てMiddle.javaへ
						if (lec.toString().equals("Middle")) {
							tmp.setFileName("Middle.java", plFile.getName());
						} else {
							// 他のlectureに同名のJavaファイルがあっても無視
							if (identifiNames.containsKey(plFile.getName()) && mustCheckTaskNames
									.get(identifiNames.get(plFile.getName())).equals(lec.toString())) {

								// System.out.println(lec.toString() + " " +
								// sd.toString() + " "
								// + identifiNames.get(plFile.getName()) + " ->"
								// +
								// plFile.getName());
								tmp.setFileName(identifiNames.get(plFile.getName()), plFile.getName());
								// PLMetricsCalculator calc = new
								// PLMetricsCalculator(plFile);
								// for style 70XX-XXXX
								// System.out.println(plFile.getName());
								// compileCount.get(sd.toString().split("-",
								// 0)[0]).put(identifiNames.get(plFile.getName()),
								// calc.getCompileCount());
								// runCount.get(sd.toString().split("-",
								// 0)[0]).put(identifiNames.get(plFile.getName()),
								// calc.getRunCount());
								// textWorkTime.get(sd.toString().split("-",
								// 0)[0]).put(identifiNames.get(plFile.getName()),
								// calc.getWorkingTime().getTime() / 1000);
								// blockWorkTime.get(sd.toString().split("-",
								// 0)[0]).put(identifiNames.get(plFile.getName()),
								// calc.getBEWorkingTime().getTime() /
								// 1000);

								// for style 7XXXXXXX
								// compileCount.get(Integer.parseInt(sd.toString().split("-",
								// 0)[0]))
								// .put(identifiNames.get(plFile.getName()),
								// calc.getCompileCount());
								// runCount.get(Integer.parseInt(sd.toString().split("-",
								// 0)[0]))
								// .put(identifiNames.get(plFile.getName()),
								// calc.getRunCount());
								// textWorkTime.get(Integer.parseInt(sd.toString().split("-",
								// 0)[0]))
								// .put(identifiNames.get(plFile.getName()),
								// calc.getWorkingTime().getTime() / 1000);
								// blockWorkTime.get(Integer.parseInt(sd.toString().split("-",
								// 0)[0]))
								// .put(identifiNames.get(plFile.getName()),
								// calc.getBEWorkingTime().getTime() / 1000);

								// compileErrorCT(plFile);
							}
						}

					}
					combineList.add(tmp);
				} catch (Exception e) {
					// System.err.println(e);
					e.printStackTrace();
				}
			}
		}
		printCombine2CSV(combineList);
	}

	private void printCombine2CSV(List<CombineStudent> combineList) {
		try (PrintWriter pw = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(new File("Combine.csv")), "Shift_JIS")));) {
			pw.println("StudentName,Lecture,FileName,FileName(student)");
			for (CombineStudent cs : combineList) {
				for (Task sn : cs.tasks) {
					StringBuilder tmp = new StringBuilder();
					tmp.append(cs.studentNum);
					tmp.append(",");
					tmp.append(cs.lecture);
					tmp.append(",");
					tmp.append(sn.cn);
					tmp.append(",");
					tmp.append("\"");
					for (String sfn : sn.studentFileName) {
						tmp.append(sfn);
						tmp.append(",");
					}
					tmp.append("\"");
					pw.println(tmp.toString());
					System.out.println(tmp.toString());

				}

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@SuppressWarnings("unused")
	private long compileErrorCT(PLProject plFile) {
		long correctTime = 0;
		CompileErrorAnalyzerList analyzer = new CompileErrorAnalyzerList(plFile);
		analyzer.analyze();
		for (TCompileErrorHistory history : analyzer.getHistories()) {
			correctTime += history.getCorrectionTime().getTime();
		}

		return correctTime;

	}

	public DataSetsInteger getCompileCount() {
		return compileCount;
	}

	public DataSetsInteger getRunCount() {
		return runCount;
	}

	public DataSetsLong getTextWorkTime() {
		return textWorkTime;
	}

	public DataSetsLong getBlockWorkTime() {
		return blockWorkTime;
	}

}
