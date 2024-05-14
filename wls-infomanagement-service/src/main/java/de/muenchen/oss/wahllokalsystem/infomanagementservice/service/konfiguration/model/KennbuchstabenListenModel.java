package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model;

import java.util.List;
import lombok.Builder;

@Builder
public record KennbuchstabenListenModel(List<KennbuchstabenListeModel> kennbuchstabenListen) {
}
