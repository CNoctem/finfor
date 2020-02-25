package org.cryophil.finfor.conf;

import java.sql.Timestamp;

public class Key {

    private String key;

    private Timestamp firstUsed;

    public Key(String key, Timestamp t) {
        this.key = key;
        firstUsed = t;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Timestamp getFirstUsed() {
        return firstUsed;
    }

    public void setFirstUsed(Timestamp firstUsed) {
        this.firstUsed = firstUsed;
    }

}
