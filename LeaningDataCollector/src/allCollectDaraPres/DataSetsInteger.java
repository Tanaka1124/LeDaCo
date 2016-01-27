package allCollectDaraPres;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSetsInteger extends HashMap<String, Map<String, Integer>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataSetsInteger(List<String> studentNums) {
		for (String snum : studentNums) {
			Map<String, Integer> tmp = new HashMap<>();

			this.put(new StringBuilder(snum).insert(4, "-").toString(), tmp);
		}
	}

}
