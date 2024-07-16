package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.UngueltigeWahlscheineRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UngueltigeWahlscheineService {

    private final UngueltigeWahlscheineRepository repository;

    private final UngueltigeWahlscheineModelMapper ungueltigeWahlscheineModelMapper;

    private final UngueltigeWahlscheineValidator ungueltigeWahlscheineValidator;

    private final ExceptionFactory exceptionFactory;
    private final UngueltigeWahlscheineRepository ungueltigeWahlscheineRepository;

    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_GetUngueltigews')")
    public byte[] getUngueltigeWahlscheine(final UngueltigeWahlscheineReference ungueltigeWahlscheineReference) {
        log.info("#getUngueltigews");
        log.debug("#getUngueltigews getUngueltigews {}", ungueltigeWahlscheineReference);

        ungueltigeWahlscheineValidator.validUngueltigeWahlscheineReferenceOrThrow(ungueltigeWahlscheineReference);

        val entityId = ungueltigeWahlscheineModelMapper.toID(ungueltigeWahlscheineReference);
        val entity = repository.findById(entityId);

        if (entity.isEmpty()) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.GETUNGUELTIGEWAHLSCHEINE_KEINE_DATEN);
        } else {
            val ungueltigeWahlscheineData = entity.get().getUngueltigews();
            return Arrays.copyOf(ungueltigeWahlscheineData, ungueltigeWahlscheineData.length);
        }
    }

    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_PostUngueltigews')")
    public void setUngueltigeWahlscheine(final UngueltigeWahlscheineWriteModel ungueltigeWahlscheineModel) {
        log.info("#postUngueltigews");

        ungueltigeWahlscheineValidator.validUngueltigeWahlscheineWriteModelOrThrow(ungueltigeWahlscheineModel);

        val entityToSave = ungueltigeWahlscheineModelMapper.toEntity(ungueltigeWahlscheineModel);
        try {
            ungueltigeWahlscheineRepository.save(entityToSave);
        } catch (final Exception e) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.POSTUNGUELTIGEWS_SPEICHERN_NICHT_ERFOLGREICH);
        }
    }

}
