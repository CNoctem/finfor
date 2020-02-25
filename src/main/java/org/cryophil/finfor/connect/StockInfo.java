package org.cryophil.finfor.connect;

import java.util.*;
import java.util.stream.Collectors;

public class StockInfo {

    private Map<String, String> info = new HashMap<>();

    public static Set<StockInfo> fromCSV(String csv) {
        List<String> lines = csv.lines().collect(Collectors.toList());
        String[] keys = lines.get(0).split(",");
        Set<StockInfo> stocks = new HashSet<>();
        for (int i = 1; i < lines.size(); i++) {
            String[] vals = lines.get(i).split(",");
            StockInfo si = new StockInfo();
            for (int j = 0; j < keys.length; j++) {
                si.info.put(keys[j], vals[j]);
            }
            stocks.add(si);
        }
        return stocks;
    }

    public Map<String, String> getInfo() {
        return info;
    }

}
