package org.cryophil.finfor.connect;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

class URLBuilder {
    private String base;
    private Map<String, String> params = new HashMap<>();

    public URLBuilder base(String base) {
        this.base = base;
        return this;
    }

    public URLBuilder addParam(String key, String val) {
        params.put(key, val);
        return this;
    }

    public String build() {
        StringJoiner sj = new StringJoiner("&");
        params.forEach((key, value) -> sj.add(key + "=" + value));
        return base + "?" + sj.toString();
    }

}