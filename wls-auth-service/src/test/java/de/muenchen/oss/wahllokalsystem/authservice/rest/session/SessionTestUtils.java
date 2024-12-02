package de.muenchen.oss.wahllokalsystem.authservice.rest.session;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test utility class for managing session-related database operations in tests.
 * Provides methods to retrieve session IDs and attributes from the Spring Session tables.
 */
public class SessionTestUtils {
    public static List<String> getSessionIdsFromDatabase(final Connection conn) throws SQLException {
        if (conn == null || conn.isClosed()) {
            throw new IllegalArgumentException("Connection must be valid and open");
        }
        List<String> result = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM SPRING_SESSION")) {
            while (rs.next()) {
                result.add(rs.getString("SESSION_ID"));
            }
        }
        return result;
    }

    public static Map<String, byte[]> getSessionAttributeBytesFromDb(final Connection conn) throws SQLException {
        if (conn == null || conn.isClosed()) {
            throw new IllegalArgumentException("Connection must be valid and open");
        }
        Map<String, byte[]> result = new HashMap<>();
        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ATTRIBUTE_NAME, ATTRIBUTE_BYTES FROM SPRING_SESSION_ATTRIBUTES")) {
            while (rs.next()) {
                result.put(rs.getString("ATTRIBUTE_NAME"), rs.getBytes("ATTRIBUTE_BYTES"));
            }
        }
        return Map.copyOf(result);
    }
}
