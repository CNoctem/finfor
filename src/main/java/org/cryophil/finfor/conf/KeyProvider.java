package org.cryophil.finfor.conf;

import org.cryophil.finfor.data.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Timestamp;

public enum KeyProvider {

    INSTANCE;

    private static final Logger log = LoggerFactory.getLogger(KeyProvider.class);

    private static final long FIVEMINSMILLIS = 5 * 60 * 1000;

    private Key[] keyStore;
    private int[] counter;
    private int currentIndex;


    private DB db = new DB();

    KeyProvider() {
        init();
    }

    public String get() {
        log.info("Providing next key.");
        if (counter[currentIndex] > 4) {
            log.info("Used this one up...");
            if (over5Minutes(keyStore[currentIndex])) {
                log.info("but time healed it. Using again.");
                keyStore[currentIndex].setFirstUsed(now());
                setKeyUsed();
                counter[currentIndex] = 1;
            } else {
                log.info("and its time has not come again yet.");
                currentIndex++;
                if (currentIndex == keyStore.length) {
                    long ht = getHealTime();
                    if (ht > 0) {
                        log.info("Used all our creativity up. Waiting for {} sec and continuing with key index {}.", ht / 1000.0, currentIndex);
                        try {
                            Thread.sleep(ht);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                log.info("Stepping to the next.");
                return get();
            }
        }
        if (counter[currentIndex]++ == 1) {
            keyStore[currentIndex].setFirstUsed(now());
            setKeyUsed();
        }
        log.info("Current Key is {} at index {}, used: {}.", keyStore[currentIndex], currentIndex, counter[currentIndex]);
        return keyStore[currentIndex].getKey();
    }

    private void init() {
        try {
            keyStore = new DB().readKeyStore();
            counter = new int[keyStore.length];
            currentIndex = 0;
            System.out.println("KeyStore size is " + keyStore.length);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private long getHealTime() {
        long minTime = Long.MAX_VALUE;
        for (int i = 0; i < keyStore.length; i++) {
            if (over5Minutes(keyStore[i])) {
                currentIndex = i;
                return 0;
            }
            long t = keyStore[i].getFirstUsed().getTime();
            if (t < minTime) {
                minTime = t;
                currentIndex = i;
            }
        }
        return FIVEMINSMILLIS - System.currentTimeMillis() + minTime;
    }

    private boolean over5Minutes(Key key) {
        return over5Minutes(key.getFirstUsed().getTime());
    }

    private boolean over5Minutes(long time) {
        return System.currentTimeMillis() - time >= FIVEMINSMILLIS;
    }

    private static Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    private void setKeyUsed() {
        try {
            db.setKeyUsed(keyStore[currentIndex]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
