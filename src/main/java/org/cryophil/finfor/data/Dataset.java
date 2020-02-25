package org.cryophil.finfor.data;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Iterator;

public class Dataset {

    private String stockName;

    private float[][] data;
    private int index;
    private int size;

    public Dataset(int capacity, String stockName) {
        data = new float[DataType.values().length][capacity];
        index = size = 0;
        this.stockName = stockName;
    }

    public void append(float[] dataPoint) {
        for (var dt : DataType.values()) data[dt.getCode()][index] = dataPoint[dt.getCode()];
        index++;
        size++;
    }

    public void appendCSV(String line, String separator) {
        String[] parts = line.split(separator);
        float[] dataPoint = new float[parts.length];
        for (int i = 0; i < DataType.values().length; i++) {
            dataPoint[i] = DataType.values()[i].convert(parts[i]);
        }
        append(dataPoint);
    }

    public int getSize() {
        return size;
    }

    public float[] getDataPoint(int index) {
        float[] row = new float[DataType.values().length];
        for (var dt : DataType.values()) {
            row[dt.getCode()] = data[dt.getCode()][index];
        }
        return row;
    }

    public float[] get(DataType column) {
        return data[column.getCode()];
    }

    public String getStockName() {
        return stockName;
    }
}
