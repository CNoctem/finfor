package org.cryophil.finfor.data;

import org.cryophil.finfor.conf.Key;

import java.sql.*;
import java.util.Locale;
import java.util.Properties;

public class DB {

    static enum ConnectionManager {
        INSTANCE;

        private Connection connection;

        Connection getConnection() throws SQLException {
            if (connection == null) {
                String url = "jdbc:postgresql://localhost:8085/finfordb";
                Properties props = new Properties();
                props.setProperty("user", "finfor");
                props.setProperty("password", "qwert");
//                props.setProperty("ssl", "true");
                connection = DriverManager.getConnection(url, props);
            }
            return connection;
        }
    }

    public void insert(Dataset ds) throws SQLException {
        int stockId = getStockIdByName(ds.getStockName());
        if (stockId == -1) stockId = insertStock(ds.getStockName());
        for (int i = 0; i < ds.getSize(); i++) {
            float[] dp = ds.getDataPoint(i);

            String sql = String.format(Locale.US, "INSERT INTO TIME_SERIES (STOCK_ID, TIMESTAMP, OPEN, HIGH, LOW, CLOSE, VOLUME) VALUES (%d, '%d', '%f', '%f', '%f', '%f', '%f')",
                    stockId, (long)dp[0], dp[1], dp[2], dp[3], dp[4], dp[5]);

            System.out.println(sql);

            Statement stmt = ConnectionManager.INSTANCE.getConnection().createStatement();
            stmt.executeUpdate(sql);
        }
    }

    public int getStockIdByName(String stockName) throws SQLException {
        String sql = String.format("SELECT ID FROM STOCK WHERE NAME='%s'", stockName);
        Statement stmt = ConnectionManager.INSTANCE.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            return rs.getInt("ID");
        }
        return -1;
    }

    public int insertStock(String stockName) throws SQLException {
        String sql = String.format("INSERT INTO STOCK (NAME) VALUES ('%s')", stockName);
        Statement stmt = ConnectionManager.INSTANCE.getConnection().createStatement();
        stmt.executeUpdate(sql);
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) return rs.getInt("ID");
        String select = String.format("SELECT ID FROM STOCK WHERE NAME='%s'", stockName);
        ResultSet idrs = ConnectionManager.INSTANCE.getConnection().createStatement().executeQuery(select);
        if (idrs.next()) return idrs.getInt("ID");
        return -1;
    }

    public Key[] readKeyStore() throws SQLException {
        String sql = "SELECT * FROM KEY_STORE";
        ResultSet rs = ConnectionManager.INSTANCE.getConnection().createStatement().executeQuery(sql);
        int c = 0;
        while (rs.next()) c++;
        rs.beforeFirst();
        Key[] keys = new Key[c];
        c = 0;
        while (rs.next()) keys[c++] = new Key(rs.getString("KEY"), rs.getTimestamp("first_used"));
        return keys;
    }

    public void setKeyUsed(Key key) throws SQLException {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        key.setFirstUsed(now);
        String sql = "UPDATE KEY_STORE SET LAST_USED='" + now + "' WHERE KEY='" + key.getKey() + "'";
        ConnectionManager.INSTANCE.getConnection().createStatement().executeUpdate(sql);
    }

    public static void main(String[] args) throws SQLException {
        System.out.println(new DB().insertStock("QWERT"));
    }


}
