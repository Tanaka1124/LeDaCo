package allClassData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class TaskDetailCal {

	public void setTaskData(File pres2LogPath, File[] javaFiles, Map<String, TaskDetail> taskData) {

		if (!pres2LogPath.exists()) {
			return;
		}
		// 各Javaファイルごとに状態を持たせる
		Map<String, CalTemp> calTemp = new HashMap<>();
		for (File f : javaFiles) {
			calTemp.put(f.getName(), new CalTemp());
		}
		calTemp.put("LoggingThread Start", new CalTemp()); // こいつらははじくと都合が悪いのでダミーデータとしてMapに追加
		calTemp.put("LoggingThread Stop", new CalTemp()); // 中身は使わない
		String debug1 = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pres2LogPath), "UTF-8"));
			String lastLine = null;// ふいにfocusout無しでログが途切れた場合は途切れた時点までのfocus時間を加算するために１つ前の行を残す
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				String[] temp = line.split("\t", 0);
				debug1 = line;
				if (temp.length > 4) {// 1行目は要素が少ないのでOutOfBoundsとなる よってはじく
					boolean westeLine = true;
					for (File f : javaFiles) {
						if ((f.getName().equals(temp[4]))) {
							westeLine = false;
							// System.out.println(f.getName() + " " + temp[4]);
						}
					}
					if (!westeLine) {// 提出課題に無いJavaファイルはそもそも計算しない
						if (temp[2].equals("COMMAND_RECORD") && temp[3].equals("FOCUS_GAINED")) {// textのFOCUSINの場合
							if (calTemp.get(temp[4]).textFocusing && lastLine != null) {// FucusOutが無いのに，新たなFOCUSINが来た場合
								String[] lastLineTime = lastLine.split("\t", 0);
								calTemp.get(lastLineTime[4]).textTime += Long.parseLong(lastLineTime[0])// 前の行のタイムスタンプまでで作業時間を加算
										- calTemp.get(lastLineTime[4]).textFocusGainTime;
							}
							calTemp.get(temp[4]).textFocusGainTime = Long.parseLong(temp[0]);// 新たなfocusをスタート
							calTemp.get(temp[4]).textFocusing = true;// foucus中である状態にする
						} else if (calTemp.get(temp[4]).textFocusing
								&& ((temp[2].equals("COMMAND_RECORD") && temp[3].equals("FOCUS_LOST")))) {// textのFOCUSOUTの場合
							calTemp.get(temp[4]).textTime += Long.parseLong(temp[0])
									- calTemp.get(temp[4]).textFocusGainTime;// FOCUSが当たっていた範囲を加算
							calTemp.get(temp[4]).textFocusing = false;// FOCUSが外れている状態にする．
						} else if (temp[2].equals("BLOCK_COMMAND_RECORD") && temp[3].equals("FOCUS_GAINED")) {// BlcokのFOCUSINが来た場合
							if (calTemp.get(temp[4]).blockFocusing && lastLine != null) {// FucusOutが無いのに，新たなFOCUSINが来た場合
								String[] lastLineTime = lastLine.split("\t", 0);
								calTemp.get(lastLineTime[4]).blockTime += Long.parseLong(lastLineTime[0])// 前の行のタイムスタンプまでで作業時間を加算
										- calTemp.get(lastLineTime[4]).blockFocusGainTime;
							}
							calTemp.get(temp[4]).blockFocusGainTime = Long.parseLong(temp[0]);// 新たなfocusをスタート
							calTemp.get(temp[4]).blockFocusing = true;// foucus中である状態にする
						} else if (calTemp.get(temp[4]).blockFocusing
								&& (temp[2].equals("BLOCK_COMMAND_RECORD") && temp[3].equals("FOCUS_LOST"))) {// BlockのFOCUSOUTの場合
							calTemp.get(temp[4]).blockTime += Long.parseLong(temp[0])
									- calTemp.get(temp[4]).blockFocusGainTime;// FOCUSが当たっていた範囲を加算
							calTemp.get(temp[4]).blockFocusing = false;// FOCUSが外れている状態にする．
						} else if ((calTemp.get(temp[4]).textFocusing || calTemp.get(temp[4]).blockFocusing)
								&& (temp[3].equals("INFO") && temp[4].equals("LoggingThread Stop"))
								&& (lastLine != null)) {// FOCUSOUTが来ずにプログラムが終了していた場合
							if (calTemp.get(temp[4]).textFocusing) {
								calTemp.get(temp[4]).textTime += Long.parseLong(temp[0])
										- calTemp.get(temp[4]).textFocusGainTime;// textのFOCUSが当たったままだったら，そこまでの作業時間を加算
							} else {
								calTemp.get(temp[4]).blockTime += Long.parseLong(temp[0])// BlcokのFOCUSが当たったままだったら，そこまでの作業時間を加算
										- calTemp.get(temp[4]).blockFocusGainTime;
							}
						}

						lastLine = line;
					}
				}
			}
			br.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println(pres2LogPath.getAbsolutePath().toString());
			System.out.println(debug1);
			e.printStackTrace();
			// System.exit(0);
		}
		// 各状態をTaskDetailに保存
		for (File f : javaFiles) {
			// System.out.println(
			// taskData.get(f.getName()).JavaProgrammingTime + " -> " +
			// calTemp.get(f.getName()).blockTime);
			taskData.get(f.getName()).JavaProgrammingTime = calTemp.get(f.getName()).textTime;
			taskData.get(f.getName()).BlockProgrammingTime = calTemp.get(f.getName()).blockTime;

		}
	}

}

class CalTemp {
	boolean textFocusing = false;
	boolean blockFocusing = false;
	long textTime = 0;
	long blockTime = 0;
	long blockFocusGainTime = 0;
	long textFocusGainTime = 0;
}
// TEMP[0]はTimastamp
// TEMP[2]はCOMMAND_RECORD
// temp[3]はFOCUS_GAINED
// temp[4]はjavaファイルの名前