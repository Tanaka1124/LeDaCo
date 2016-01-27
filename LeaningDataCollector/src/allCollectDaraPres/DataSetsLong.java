package allCollectDaraPres;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSetsLong extends HashMap<String, Map<String, Long>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataSetsLong(List<String> studentNums) {
		for (String snum : studentNums) {
			Map<String, Long> tmp = new HashMap<>();
			this.put(new StringBuilder(snum).insert(4, "-").toString(), tmp);
		}
	}

}
