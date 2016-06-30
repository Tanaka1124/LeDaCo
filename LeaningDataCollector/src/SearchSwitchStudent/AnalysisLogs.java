package SearchSwitchStudent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AnalysisLogs {

	static String BLOCK_LOG = "BLOCK_COMMAND_RECORD";
	static String TEXT_LOG = "TEXTEDIT_RECORD";
	static String COMMAND_LOG = "COMMAND_RECORD";
	static String START_TAG = "FOCUS_GAINED";
	static String END_TAG = "FOCUS_LOST";

	// BUG1 BlockのFocusGainが抜けた場合，1回カウントが減る．

	public Map<String, Integer> analLog(File pres) {
		File pres2Log = new File(pres, "pres2.log");
		Map<String, Boolean> states = new HashMap<>();
		Map<String, Integer> switchCount = new HashMap<>();
		try (Scanner scan = new Scanner(pres2Log);) {
			while (scan.hasNext()) {

				String tmp = scan.nextLine();
				// タグだけ返す
				String[] elements = spilitLine(tmp);

				// エラー行(空行)をはじく

				if (elements.length < 3) {
					continue;
				}

				// Block_COMMAND_RECORDとTEXTEDIT_RECORDとCOMMAND_RECORD以外飛ばす
				if (!(elements[2].equals(BLOCK_LOG) || elements[2].equals(COMMAND_LOG)
						|| elements[2].equals(TEXT_LOG))) {
					continue;
				}

				// MAPに登録していないJavaファイルなら登録
				if (!(states.containsKey(elements[4]) && switchCount.containsKey(elements[4]))) {
					states.put(elements[4], false);
					switchCount.put(elements[4], 0);
				}

				// Block_Focus_Gainタグで，該当stateがfalseならstateをリセットして該当だけtrue
				if (elements[2].equals(BLOCK_LOG) && elements[3].equals(START_TAG)) {

					// TextからBlockにFocusが移ったら
					if (!states.get(elements[4]).booleanValue()) {
						// switchCountをインクリメント
						switchCount.put(elements[4], switchCount.get(elements[4]));
					}

					// endタグが壊れていた場合(TextのfocusGainが無いまま次のBlockのFocusGain)
					for (Map.Entry<String, Boolean> entry : states.entrySet()) {
						if (entry.getValue().booleanValue() && !entry.getKey().equals(elements[4])) {
							// switchCountをインクリメント
							switchCount.put(entry.getKey(), switchCount.get(entry.getKey()) + 1);
							System.out.println(entry.getKey() + " " + switchCount.get(entry.getKey()));
						}
					}
					// endタグが壊れているとアレなので一応，初期化
					for (String key : states.keySet()) {
						states.put(key, false);
					}

					// 該当だけtrue
					states.put(elements[4], true);

				}

				// Textにfocusが当たったらstateをリセット
				// このときstateがtrueのファイルは全てインクリメント
				if (elements[2].equals(COMMAND_LOG) && elements[3].equals(START_TAG)) {
					for (Map.Entry<String, Boolean> entry : states.entrySet()) {
						if (entry.getValue().booleanValue()) {
							// switchCountをインクリメント
							switchCount.put(entry.getKey(), switchCount.get(entry.getKey()) + 1);
						}
					}
					// 初期化
					for (String key : states.keySet()) {
						states.put(key, false);
					}

				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return switchCount;
	}

	private String[] spilitLine(String line) {
		String[] tmp = line.split("\\t", 0);

		return tmp;
	}
}
