package de.muenchen.oss.wahllokalsystem.eaiservice.service.init;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Stimmzettelgebiet;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.StimmzettelgebietRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahl;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahlRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahlbezirkRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahltag;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahltageRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto.StimmzettelgebietInitOptionsDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto.WahlInitOptionsDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto.WahlbezirkOptionsDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto.WahltagInitOptionsDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto.WahltagInitRequestDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahldaten.WahldatenMapper;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class InitDataService {

    private final TransactionTemplate transactionTemplate;

    private final WahldatenMapper wahldatenMapper;

    private final StimmzettelgebietRepository stimmzettelgebietRepository;
    private final WahlbezirkRepository wahlbezirkRepository;
    private final WahltageRepository wahltageRepository;
    private final WahlRepository wahlRepository;

    public void initWahltage(@RequestBody WahltagInitRequestDTO request) {
        for (var wahltagInitRequest : request.wahltagInitRequests()) {
            createWahltag(wahltagInitRequest);
        }
    }

    private void createWahltag(final WahltagInitOptionsDTO wahltagInit) {
        transactionTemplate.executeWithoutResult(status -> {
            val wahltag = wahltageRepository.save(new Wahltag(wahltagInit.wahltag(),
                    Optional.ofNullable(wahltagInit.beschreibung()).orElse(createDefaultBeschreibung(wahltagInit)),
                    wahltagInit.nummer()));

            if (!CollectionUtils.isEmpty(wahltagInit.wahlInitOptions())) {
                createWahlen(wahltagInit.wahlInitOptions(), wahltag);
            }
        });
    }

    private void createWahlen(final Collection<WahlInitOptionsDTO> wahlInitOptions, final Wahltag wahltag) {
        wahlInitOptions.forEach(option -> {
            val wahl = wahlRepository.save(new Wahl(option.name(), wahldatenMapper.toModel(option.wahlart()), wahltag));

            if (!CollectionUtils.isEmpty(option.stimmzettelgebietInitOptions())) {
                createStimmzettelgebiete(option.stimmzettelgebietInitOptions(), wahl);
            }
        });
    }

    private void createStimmzettelgebiete(final Collection<StimmzettelgebietInitOptionsDTO> stimmzettelgebietInitOptions, final Wahl wahl) {
        stimmzettelgebietInitOptions.forEach(option -> {
            val stimmzettelgebiet = stimmzettelgebietRepository.save(
                    new Stimmzettelgebiet(option.nummer(), option.name(), wahldatenMapper.toEntity(option.stimmzettelgebietsart()), wahl));

            if (!CollectionUtils.isEmpty(option.wahlbezirkOptions())) {
                createWahlbezirke(option.wahlbezirkOptions(), stimmzettelgebiet);
            }
        });
    }

    private void createWahlbezirke(final Collection<WahlbezirkOptionsDTO> wahlbezirkInitOptions, final Stimmzettelgebiet stimmzettelgebiet) {
        wahlbezirkInitOptions.forEach(option -> {
            val wahlbezirk = wahlbezirkRepository.save(
                    new Wahlbezirk(wahldatenMapper.toEntity(option.wahlbezirkArt()), option.nummer(), stimmzettelgebiet, option.a1(),
                            option.a2(), option.a3()));
        });
    }

    private static String createDefaultBeschreibung(final WahltagInitOptionsDTO wahltagInit) {
        return "beschreibung of " + wahltagInit.wahltag() + " nummer " + wahltagInit.nummer();
    }
}
