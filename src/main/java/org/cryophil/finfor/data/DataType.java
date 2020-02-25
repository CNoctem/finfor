package org.cryophil.finfor.data;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;

public enum DataType {

    TIMESTAMP(0) {
        @Override
        public float convert(String strVal) {
            return DataType.timestamp2float(strVal);
        }
    }, OPEN(1), HIGH(2), LOW(3), CLOSE(4), VOLUME(5);

    private int code;

    private DataType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public float convert(String strVal) {
        return Float.parseFloat(strVal);
    }

    private static float timestamp2float(String timestamp) {
        final DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("UTC"));
        Instant result = Instant.from(formatter.parse(timestamp));

        return result.toEpochMilli();

//        Timestamp ts = Timestamp.valueOf(timestamp);
//        return ts.getTime();
    }

}
