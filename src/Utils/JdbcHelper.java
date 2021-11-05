package Utils;

import java.sql.*;

public class JdbcHelper {

    private static String url = "jdbc:sqlserver://localhost;database=EduSys";
    private static String username = "sa";
    private static String password = "123";

    public static PreparedStatement preparedStatement(String sql, Object... args) throws SQLException {
        Connection conn = DriverManager.getConnection(url, username, password);
        PreparedStatement pst = null;
        if (sql.trim().startsWith("{")) {
            pst = conn.prepareCall(sql);
        } else {
            pst = conn.prepareStatement(sql);
        }

        for (int i = 0; i < args.length; i++) {
            pst.setObject(i + 1, args[i]);
        }
        return pst;
    }
    
    public static void executeUpdate(String sql, Object...args){
        try {
            PreparedStatement pst = preparedStatement(sql, args);
            try {
                pst.executeUpdate();
            }finally {
                pst.getConnection().close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public static ResultSet executeQuery(String sql, Object...args){
        try {
            PreparedStatement pst = preparedStatement(sql, args);
            return pst.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
