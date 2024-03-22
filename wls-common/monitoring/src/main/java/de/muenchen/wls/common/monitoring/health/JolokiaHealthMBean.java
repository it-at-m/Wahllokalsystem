package de.muenchen.wls.common.monitoring.health;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

/**
 * Initialisiert ein MBean das zur Überwachung durch Jolokia genutzt werden kann.
 */
@Component
@ConfigurationProperties("wls.monitoring.jolokia")
@ConditionalOnProperty(value = "wls.monitoring.jolokia.enabled", havingValue = "true")
@ManagedResource(
        objectName = "de.muenchen.wls.jolokia:name=HealthInformation",
        description = "Liefert detaillierte Zustandsinformationen über das System"
)
public class JolokiaHealthMBean {

    private static final Logger LOG = LoggerFactory.getLogger(JolokiaHealthMBean.class);
    @Autowired(required = false)
    DataSource dataSource;

    /**
     * Liste aller Services die dieser Service für eine korrekte Funktionalität benötigt
     */
    private final List<String> requiredServices = new ArrayList<>();

    /**
     * Konfiguriert, ob dieser Service eine Datenbankverbindung hat. Checks bezüglich Datenbankverbindungen werden nur durchgeführt, wenn dieses property auf
     * "true" gesetzt wird.
     */
    private boolean hasDatabaseConnection = false;

    @ManagedOperation(description = "Prüft ob die Datenbankverbindung aktiv ist")
    public boolean isDatabaseConnected() {
        if (hasDatabaseConnection) {
            try {
                Connection c = dataSource.getConnection();
                boolean valid = c.isValid(1000);
                c.close();
                return valid;
            } catch (SQLException e) {
                LOG.error("Fehler beim überprüfen der Datenbankverbindung", e);
                return false;
            }
        } else {
            return true;
        }
    }

    @ManagedOperation(description = "Liefert eventuell aufgetretene Datenbankfehler")
    public String getDatabaseWarnings() {
        if (hasDatabaseConnection) {
            try {
                Connection connection = dataSource.getConnection();
                SQLWarning sqlWarning = connection.getWarnings();
                connection.close();
                if (sqlWarning == null) {
                    return "";
                } else {
                    return sqlWarning.getMessage();
                }
            } catch (SQLException e) {
                LOG.error("Fehler beim auslesen der aufgetretenen Datenbankfehler", e);
                return e.getMessage();
            }
        } else {
            return "";
        }
    }

    public List<String> getRequiredServices() {
        return requiredServices;
    }

    public boolean isHasDatabaseConnection() {
        return hasDatabaseConnection;
    }

    public void setHasDatabaseConnection(boolean hasDatabaseConnection) {
        this.hasDatabaseConnection = hasDatabaseConnection;
    }
}
