package allCollectDaraPres;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CPath;
import pres.loader.model.PLFile;
import pres.loader.model.PLProject;

public class NameIdentification {

	public NameIdentification() {

	}

	public Set<String> collectExistTaskName(List<CDirectory> lectures) {

		Set<String> existTaskNames = new HashSet<>();
		for (CDirectory lec : lectures) {
			System.out.println(lec.toString() + " start");
			List<CDirectory> students;
			students = lec.getDirectoryChildren();
			// for ppv.data
			// students = new CDirectory(new CPath(new File(lec.toJavaFile(),
			// "project"))).getDirectoryChildren();
			for (CDirectory sd : students) {
				try {
					// for ppv.data
					CDirectory project = new CDirectory(new CPath(new File(sd.toJavaFile(), "project")));
					PLProject pr = new PLProject(project.getNameByString(), project, project.getAbsolutePath());
					pr.load();
					for (PLFile plFile : pr.getRootPackage().getFilesRecursively()) {
						if (existTaskNames.add(plFile.getName())) {

						} else {
							// ファイルの数を数えてみては？Map<hogehoge.java,0>みたいな感じ
						}
						// PLMetricsCalculator calc = new
						// PLMetricsCalculator(plFile);
					}
				} catch (Exception e) {
					System.err.println(e);
				}
			}
		}

		// for (String tn : existTaskNames) {
		// System.out.println(tn);
		// }
		return existTaskNames;
	}

	public Map<String, String> autoIdentification(Set<String> existTaskNames, Map<String, String> mustCheckTaskNames,
			Set<String> ignoreTaskNames) {
		// TODO表記ゆれを吸収する
		// 現状は一字一句正しいファイル名のみ取得
		Map<String, String> tmpIdentifiNames = new TreeMap<>();
		for (String etn : existTaskNames) {
			if (!ignoreTaskNames.contains(etn)) {
				if (mustCheckTaskNames.containsKey(etn)) {
					tmpIdentifiNames.put(etn, etn);
				} else {
					tmpIdentifiNames.put(etn, "");
				}
			}
		}
		return tmpIdentifiNames;
	}

}
