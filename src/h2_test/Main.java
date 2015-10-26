package h2_test;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Main {

	public static void main(String[] args) {
		String tn = "testTable2";
		
		DBConnector connector = new DBConnector(tn);
		System.out.println("Done");
		
		connector.addOrIncreaseValue(123);
		connector.addOrIncreaseValue(124);
		connector.addOrIncreaseValue(123);
		connector.addOrIncreaseValue(123);
		connector.addOrIncreaseValue(123);
		connector.addOrIncreaseValue(124);
		connector.logDBValues();
//		Map<Integer, Integer> res = connector.tableToKVMap();
//		Set<Entry<Integer, Integer>> test = res.entrySet();
//		for (Entry<Integer, Integer> entry : test) {
//			System.out.println(entry.getKey()+", "+entry.getValue());
//		}

	}

}
