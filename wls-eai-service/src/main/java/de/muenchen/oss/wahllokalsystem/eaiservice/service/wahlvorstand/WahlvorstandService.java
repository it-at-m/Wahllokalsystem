package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand.Wahlvorstand;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand.WahlvorstandRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand.Wahlvorstandsmitglied;
import de.muenchen.oss.wahllokalsystem.eaiservice.exception.NotFoundException;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandsaktualisierungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandsmitgliedAktualisierungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.IDConverter;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WahlvorstandService {

    private final WahlvorstandRepository wahlvorstandRepository;

    private final WahlvorstandMapper wahlvorstandMapper;

    private final WahlvorstandValidator wahlvorstandValidator;

    private final IDConverter idConverter;

    @PreAuthorize("hasAuthority('aoueai_BUSINESSACTION_LoadWahlvorstand')")
    public WahlvorstandDTO getWahlvorstandForWahlbezirk(final String wahlbezirkID) {
        wahlvorstandValidator.validateWahlbezirkIDOrThrow(wahlbezirkID);
        val wahlbezirkUUID = idConverter.convertIDToUUIDOrThrow(wahlbezirkID);
        return wahlvorstandMapper.toDTO(findByWahlbezirkIDOrThrow(wahlbezirkUUID));
    }

    @PreAuthorize("hasAuthority('aoueai_BUSINESSACTION_SaveAnwesenheit')")
    public void setAnwesenheit(final WahlvorstandsaktualisierungDTO aktualisierung) {
        wahlvorstandValidator.validateSaveAnwesenheitDataOrThrow(aktualisierung);

        val wahlvorstandToUpdate = findByWahlbezirkIDOrThrow(idConverter.convertIDToUUIDOrThrow(aktualisierung.wahlbezirkID()));
        updateAnwesenheitOfWahlvorstand(aktualisierung, wahlvorstandToUpdate);
        wahlvorstandRepository.save(wahlvorstandToUpdate);
    }

    private void updateAnwesenheitOfWahlvorstand(final WahlvorstandsaktualisierungDTO updateData, final Wahlvorstand existingWahlvorstand) {
        updateData.mitglieder().forEach(mitgliedUpdateData -> {
            val mitgliedToUpdate = existingWahlvorstand.getMitglieder().stream()
                    .filter(setElement -> setElement.getId().toString().equals(mitgliedUpdateData.identifikator())).findFirst();
            mitgliedToUpdate.ifPresent(mitglied -> updateAnwesenheitOfWahlvorstandsmitglied(updateData.anwesenheitBeginn(), mitgliedUpdateData, mitglied));
        });
    }

    private void updateAnwesenheitOfWahlvorstandsmitglied(final LocalDateTime timeOfUpdate, WahlvorstandsmitgliedAktualisierungDTO mitgliedUpdateData,
            Wahlvorstandsmitglied mitglied) {
        mitglied.setAnwesenheitUpdatedOn(timeOfUpdate);
        mitglied.setAnwesend(mitgliedUpdateData.anwesend());
    }

    private Wahlvorstand findByWahlbezirkIDOrThrow(final UUID wahlbezirkID) {
        return wahlvorstandRepository.findFirstByWahlbezirkID(wahlbezirkID).orElseThrow(() -> new NotFoundException(wahlbezirkID, Wahlvorstand.class));
    }

}
