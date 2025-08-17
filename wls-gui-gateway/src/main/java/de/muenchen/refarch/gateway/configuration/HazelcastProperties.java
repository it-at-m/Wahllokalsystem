package de.muenchen.refarch.gateway.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("refarch.hazelcast")
public class HazelcastProperties {
    /**
     * Name of the hazelcast cluster.
     */
    private String clusterName = "session_replication_group";
    /**
     * Name of the hazelcast instance.
     */
    private String instanceName = "hazl_instance";
    /**
     * Kubernetes service name.
     * Required for running hazelcast inside kubernetes.
     */
    private String serviceName;
}
