package allCollectDaraPres;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSetsLong extends HashMap<Integer, Map<String, Long>> {

	public DataSetsLong(List<Integer> studentNums) {
		for (Integer snum : studentNums) {
			Map<String, Long> tmp = new HashMap<>();
			this.put(snum, tmp);
		}
	}

}
