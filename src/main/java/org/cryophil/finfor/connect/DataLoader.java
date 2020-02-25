package org.cryophil.finfor.connect;

import org.cryophil.finfor.conf.KeyProvider;
import org.cryophil.finfor.data.DB;
import org.cryophil.finfor.data.Dataset;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class DataLoader {

    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
        for (int i = 65; i < 91; i++) {
            String url = new URLBuilder()
                    .base("https://www.alphavantage.co/query")
                    .addParam("function", "SYMBOL_SEARCH")
                    .addParam("keywords", "" + (char) i)
                    .addParam("apikey", KeyProvider.INSTANCE.get())
                    .addParam("datatype", "csv")
                    .build();

            String body = getResponse(url);
            System.out.println(body);
            Set<StockInfo> stocks = StockInfo.fromCSV(body);
            System.out.println(stocks);
        }

        System.exit(-1);

        int frequencyMinute = 5;
        String symbol = "GOOGL";
        boolean full = true;
        String url = new URLBuilder()
                .base("https://www.alphavantage.co/query")
                .addParam("function", "TIME_SERIES_INTRADAY")
                .addParam("symbol", symbol)
                .addParam("interval", frequencyMinute + "min")
                .addParam("apikey", KeyProvider.INSTANCE.get())
                .addParam("datatype", "csv")
                .addParam("outputsize", full ? "full" : "compact")
                .build();
        String response = getResponse(url);

        if (!response.lines().limit(1).collect(Collectors.toList()).get(0).equals("timestamp,open,high,low,close,volume")) {
            throw new UnsupportedEncodingException("First row does not match the requirements of a CSV.");
        }
        Dataset ds = new Dataset((int) (response.lines().count() - 1), symbol);
        response.lines().skip(1).forEach(e -> ds.appendCSV(e, ","));

        for (int i = 0; i < ds.getSize(); i++) System.out.println(
                Instant.ofEpochMilli((long) ds.getDataPoint(i)[0]).truncatedTo(ChronoUnit.MINUTES) + ": " + Arrays.toString(ds.getDataPoint(i)));

        new DB().insert(ds);

    }

    private static String getResponse(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

}
