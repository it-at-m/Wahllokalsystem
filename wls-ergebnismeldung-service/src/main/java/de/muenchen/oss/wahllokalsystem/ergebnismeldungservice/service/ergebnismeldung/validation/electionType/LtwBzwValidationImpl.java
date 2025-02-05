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
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LtwBzwValidationImpl implements ElectionTypeValidation {

    private final DefaultElectionTypeValidator validator;

    @Override
    public boolean supports(final WahlartModel wahlart) {
        return WahlartModel.LTW == wahlart || WahlartModel.BZW == wahlart;
    }

    @Override
    public boolean isValidUwb(final String wahlbezirkID, final String wahlID, final Long waehlerverzeichnisNummer, final MeldungsartModel meldungsart)
            throws WlsException {
        val necessaryStacks = buildNecessaryStack(meldungsart);
        return validator.checkValidation(WahlbezirkArtModel.UWB, wahlbezirkID, wahlID, waehlerverzeichnisNummer, necessaryStacks);
    }

    @Override
    public boolean isValidBwb(final String wahlbezirkID, final String wahlID, final Long waehlerverzeichnisNummer, final MeldungsartModel meldungsart)
            throws WlsException {
        val necessaryStacks = buildNecessaryStack(meldungsart);
        necessaryStacks.add(Stapelart.LTW_BZW_G_KLEIN);
        necessaryStacks.add(Stapelart.LTW_BZW_G_GROSS);
        necessaryStacks.add(Stapelart.LTW_BZW_G_BEIDE);

        return validator.checkValidation(WahlbezirkArtModel.BWB, wahlbezirkID, wahlID, waehlerverzeichnisNummer, necessaryStacks);
    }

    private List<Stapelart> buildNecessaryStack(final MeldungsartModel meldungsart) {
        List<Stapelart> necessaryStacks = new ArrayList<>();
        necessaryStacks.add(Stapelart.LTW_BZW_A);
        necessaryStacks.add(Stapelart.LTW_BZW_B);
        necessaryStacks.add(Stapelart.LTW_BZW_C_GUELTIG);
        necessaryStacks.add(Stapelart.LTW_BZW_C_UNGUELTIG);
        necessaryStacks.add(Stapelart.LTW_BZW_D);
        // Stapel LTW_BZW_DII nur nach der Schnellmeldung und vor der Niederschrift erforderlich
        log.debug("#addnecessary stapel meldungsart {}", meldungsart);
        if (meldungsart.equals(MeldungsartModel.V1)) {
            log.debug("#addnecessary stapel");
            necessaryStacks.add(Stapelart.LTW_BZW_DII);
            log.debug("#addnecessary stapel");
        } else {
            log.debug("do not #addnecessary stapel");
        }
        necessaryStacks.add(Stapelart.LTW_BZW_E);
        necessaryStacks.add(Stapelart.LTW_BZW_F_GUELTIG);
        necessaryStacks.add(Stapelart.LTW_BZW_F_UNGUELTIG);
        return necessaryStacks;
    }
}
