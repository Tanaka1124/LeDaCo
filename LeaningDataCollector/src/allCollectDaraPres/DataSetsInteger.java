package allCollectDaraPres;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSetsInteger extends HashMap<Integer, Map<String, Integer>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataSetsInteger(List<Integer> studentNums) {
		for (Integer snum : studentNums) {
			Map<String, Integer> tmp = new HashMap<>();
			this.put(snum, tmp);
		}
	}

}
