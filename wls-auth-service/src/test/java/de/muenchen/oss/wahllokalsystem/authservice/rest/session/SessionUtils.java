package de.muenchen.oss.wahllokalsystem.authservice.rest.session;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SessionUtils {
    public static List<String> getSessionIdsFromDatabase(final Connection conn) throws SQLException {
        List<String> result = new ArrayList<>();
        ResultSet rs = getResultSet(conn,
                "SELECT * FROM SPRING_SESSION");

        while (rs.next()) {
            result.add(rs.getString("SESSION_ID"));
        }
        return result;
    }

    public static HashMap<String,byte[]> getSessionAttributeBytesFromDb(final Connection conn) throws SQLException {
        HashMap<String,byte[]> result = new HashMap<>();
        ResultSet rs = getResultSet(conn,
                "SELECT * FROM SPRING_SESSION_ATTRIBUTES");

        while (rs.next()) {
            result.put(
                    rs.getString("ATTRIBUTE_NAME"), rs.getBytes("ATTRIBUTE_BYTES")
            );
        }
        return result;
    }

    private static ResultSet getResultSet(final Connection conn, String sql) throws SQLException {
        Statement stat = conn.createStatement();
        return stat.executeQuery(sql);
    }
}
