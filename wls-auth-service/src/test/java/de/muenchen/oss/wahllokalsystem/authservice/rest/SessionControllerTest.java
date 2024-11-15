package de.muenchen.oss.wahllokalsystem.authservice.rest;

import static de.muenchen.oss.wahllokalsystem.authservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.authservice.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.*;
import de.muenchen.oss.wahllokalsystem.authservice.MicroServiceApplication;
import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = { MicroServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
            "spring.datasource.url=jdbc:h2:mem:wls-auth-service;DB_CLOSE_ON_EXIT=FALSE",
            "refarch.gracefulshutdown.pre-wait-seconds=0"
        })
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SessionControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int port;

    private Connection conn;

    @BeforeEach
    void beforeEach() throws SQLException {
        conn = DriverManager.getConnection("jdbc:h2:mem:wls-auth-service", "sa", "");
        Statement stat = conn.createStatement();
        stat.execute("DELETE SPRING_SESSION_ATTRIBUTES");
        stat.execute("DELETE SPRING_SESSION");
    }

    private List<String> getSessionIdsFromDatabase() throws SQLException {
        List<String> result = new ArrayList<>();
        ResultSet rs = getResultSet(
                "SELECT * FROM SPRING_SESSION");

        while (rs.next()) {
            result.add(rs.getString("SESSION_ID"));
        }
        return result;
    }

    private List<byte[]> getSessionAttributeBytesFromDb() throws SQLException {
        List<byte[]> result = new ArrayList<>();
        ResultSet rs = getResultSet(
                "SELECT * FROM SPRING_SESSION_ATTRIBUTES");

        while (rs.next()) {
            result.add(rs.getBytes("ATTRIBUTE_BYTES"));
        }
        return result;
    }

    private ResultSet getResultSet(String sql) throws SQLException {
        Statement stat = conn.createStatement();
        return stat.executeQuery(sql);
    }

    @Test
    @Order(1)
    public void awhenH2DbIsQueried_thenSessionInfoIsEmpty() throws SQLException {
        assertEquals(
                0, getSessionIdsFromDatabase().size());
        assertEquals(
                0, getSessionAttributeBytesFromDb().size());
    }

    @Test
    @Order(2)
    public void bwhenH2DbIsQueried_thenOneSessionIsCreated() throws SQLException {
        val result = this.testRestTemplate.getForObject(
                "http://localhost:" + port + "/loginwls", String.class);

        Assertions.assertThat(result)
                .isNotEmpty();
        assertEquals(1, getSessionIdsFromDatabase().size());
    }

    @Test
    @Order(3)
    public void cwhenH2DbIsQueried_thenSessionAttributeIsRetrieved() throws Exception {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("color", "red");
        this.testRestTemplate.postForObject(
                "http://localhost:" + port + "/savecolor", map, String.class);
        List<byte[]> queryResponse = getSessionAttributeBytesFromDb();

        assertEquals(1, queryResponse.size());
        ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(queryResponse.get(0)));
        List<String> obj = (List<String>) in.readObject();
        assertEquals("red", obj.get(0));
    }
}