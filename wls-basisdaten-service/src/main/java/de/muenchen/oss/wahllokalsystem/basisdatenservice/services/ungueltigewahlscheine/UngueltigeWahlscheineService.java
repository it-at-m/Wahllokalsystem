package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.ungueltigewahlscheine.UngueltigeWahlscheineRepository;
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

    private final UngueltigeWahlscheineModelMapper ungueltigeWahlscheineModelMapper;

    private final UngueltigeWahlscheineValidator ungueltigeWahlscheineValidator;

    private final ExceptionFactory exceptionFactory;
    private final UngueltigeWahlscheineRepository ungueltigeWahlscheineRepository;

    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_GetUngueltigews')")
    public byte[] getUngueltigeWahlscheine(final UngueltigeWahlscheineReferenceModel ungueltigeWahlscheineReferenceModel) {
        log.info("#getUngueltigews");
        log.debug("#getUngueltigews getUngueltigews {}", ungueltigeWahlscheineReferenceModel);

        ungueltigeWahlscheineValidator.validUngueltigeWahlscheineReferenceOrThrow(ungueltigeWahlscheineReferenceModel);

        val entityId = ungueltigeWahlscheineModelMapper.toID(ungueltigeWahlscheineReferenceModel);
        val entity = ungueltigeWahlscheineRepository.findById(entityId);

        if (entity.isEmpty()) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.GETUNGUELTIGEWAHLSCHEINE_KEINE_DATEN);
        } else {
            val ungueltigeWahlscheineData = entity.get().getUngueltigeWahlscheine();
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
