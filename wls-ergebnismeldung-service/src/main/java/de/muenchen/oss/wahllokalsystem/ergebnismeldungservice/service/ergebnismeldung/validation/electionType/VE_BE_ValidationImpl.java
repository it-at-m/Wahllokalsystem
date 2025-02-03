package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation.electionType;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation.DefaultElectionTypeValidator;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation.ElectionTypeValidation;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VE_BE_ValidationImpl implements ElectionTypeValidation {

    private final DefaultElectionTypeValidator validator;

    @Override
    public boolean supports(WahlartModel wahlart) {
        return WahlartModel.VE == wahlart || WahlartModel.BEB == wahlart;
    }

    @Override
    public boolean isValidUwb(String wahlbezirkID, String wahlID, Long waehlerverzeichnisNummer, MeldungsartModel meldungsart) throws WlsException {
        val necessaryStacks = buildNecessaryStack();
        return validator.checkValidation(WahlbezirkArtModel.UWB, wahlbezirkID, wahlID, waehlerverzeichnisNummer, necessaryStacks);
    }

    @Override
    public boolean isValidBwb(String wahlbezirkID, String wahlID, Long waehlerverzeichnisNummer, MeldungsartModel meldungsart) throws WlsException {
        val necessaryStacks = buildNecessaryStack();
        return validator.checkValidation(WahlbezirkArtModel.BWB, wahlbezirkID, wahlID, waehlerverzeichnisNummer, necessaryStacks);
    }

    private List<Stapelart> buildNecessaryStack() {
        List<Stapelart> necessaryStacks = new ArrayList<>();
        necessaryStacks.add(Stapelart.VE_BE_D_OPTION_1);
        necessaryStacks.add(Stapelart.VE_BE_D_OPTION_2);
        necessaryStacks.add(Stapelart.VE_BE_D_UNGUELTIG);
        necessaryStacks.add(Stapelart.VE_BE_CBA_OPTION_1);
        necessaryStacks.add(Stapelart.VE_BE_CBA_OPTION_2);
        necessaryStacks.add(Stapelart.VE_BE_CBA_UNGUELTIG);
        return necessaryStacks;
    }
}
