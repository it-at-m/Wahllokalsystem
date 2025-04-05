package de.muenchen.oss.wahllokalsystem.eaiservice.service.init;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Stimmzettelgebiet;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.StimmzettelgebietRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahl;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahlRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahlbezirkRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahltag;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahltageRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Kandidat;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.KandidatRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.WahlvorschlaegeListe;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.WahlvorschlaegeListeRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Wahlvorschlag;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.WahlvorschlagRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto.RandomStimmzettelgebieteInitOptionsDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto.RandomWahlbezirkeInitOptionsDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto.RangeDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto.StimmzettelgebietInitOptionsDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto.WahlInitOptionsDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto.WahlbezirkOptionsDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto.WahltagInitOptionsDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto.WahltagInitRequestDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto.WahlvorschlagInitOptions;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlbezirkArtDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahldaten.WahldatenMapper;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitDataService {

    private final TransactionTemplate transactionTemplate;

    private final WahldatenMapper wahldatenMapper;

    private final KandidatRepository kandidatRepository;
    private final StimmzettelgebietRepository stimmzettelgebietRepository;
    private final WahlbezirkRepository wahlbezirkRepository;
    private final WahltageRepository wahltageRepository;
    private final WahlRepository wahlRepository;
    private final WahlvorschlagRepository wahlvorschlagRepository;
    private final WahlvorschlaegeListeRepository wahlvorschlaegeListeRepository;

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
                storeWahlen(wahltagInit.wahlInitOptions(), wahltag);
            }
        });
    }

    private void storeWahlen(final Collection<WahlInitOptionsDTO> wahlInitOptions, final Wahltag wahltag) {
        wahlInitOptions.forEach(option -> {
            val wahl = wahlRepository.save(new Wahl(option.name(), wahldatenMapper.toModel(option.wahlart()), wahltag));

            if (option.randomStimmzettelgebiete() != null) {
                storeRandomGeneratedStimmzettelgebiete(option.randomStimmzettelgebiete(), wahl);
            } else if (!CollectionUtils.isEmpty(option.stimmzettelgebietInitOptions())) {
                storeStimmzettelgebiete(option.stimmzettelgebietInitOptions(), wahl);
            }
        });
    }

    private void storeRandomGeneratedStimmzettelgebiete(
            final RandomStimmzettelgebieteInitOptionsDTO options,
            final Wahl wahl
    ) {
        IntStream.rangeClosed(1, getCountInRange(options.count())).forEach(i -> {
            val stimmzettelgebiet = stimmzettelgebietRepository.save(
                    new Stimmzettelgebiet(wahl.getName() + "_" + i, "" + i, wahldatenMapper.toEntity(options.stimmzettelgebietsart()), wahl));

            storeRandomGeneratedWahlbezirke(options.randomWahlbezirke(), options.wahlvorschlagInitOptions(), stimmzettelgebiet);
        });
    }

    private void storeStimmzettelgebiete(final Collection<StimmzettelgebietInitOptionsDTO> stimmzettelgebietInitOptions, final Wahl wahl) {
        stimmzettelgebietInitOptions.forEach(option -> {
            val stimmzettelgebiet = stimmzettelgebietRepository.save(
                    new Stimmzettelgebiet(option.nummer(), option.name(), wahldatenMapper.toEntity(option.stimmzettelgebietsart()), wahl));

            if (option.randomWahlbezirke() != null) {
                storeRandomGeneratedWahlbezirke(option.randomWahlbezirke(), option.wahlvorschlagInitOptions(), stimmzettelgebiet);
            } else if (!CollectionUtils.isEmpty(option.wahlbezirkOptions())) {
                storeWahlbezirke(option.wahlbezirkOptions(), option.wahlvorschlagInitOptions(), stimmzettelgebiet);
            }
        });
    }

    private void storeRandomGeneratedWahlbezirke(final RandomWahlbezirkeInitOptionsDTO options,
            final WahlvorschlagInitOptions wahlvorschlagInitOptions,
            final Stimmzettelgebiet stimmzettelgebiet) {
        val wahlvorschlaegeOfStimmzettelgebiet = setupWahlvorschlaege(wahlvorschlagInitOptions);

        //UWB
        val countUwbs = getCountInRange(options.uwb());
        log.atInfo().log("countUwbs: {} of szg: {}", countUwbs, stimmzettelgebiet.getNummer());
        IntStream.rangeClosed(1, countUwbs).forEach(i -> {
            val wahlbezirk = wahlbezirkRepository.save(
                    new Wahlbezirk(wahldatenMapper.toEntity(WahlbezirkArtDTO.UWB), stimmzettelgebiet.getNummer() + "_U_" + i, stimmzettelgebiet,
                            getCountInRange(options.a1()),
                            getCountInRange(options.a2()),
                            getCountInRange(options.a3())));

            storeWahlvorschlaege(wahlvorschlaegeOfStimmzettelgebiet, wahlbezirk);
        });

        //BWB
        val countBwbs = getCountInRange(options.bwb());
        log.atInfo().log("countBwbs: {} of szg: {}", countBwbs, stimmzettelgebiet.getNummer());
        IntStream.rangeClosed(1, countBwbs).forEach(i -> {
            val wahlbezirk = wahlbezirkRepository.save(
                    new Wahlbezirk(wahldatenMapper.toEntity(WahlbezirkArtDTO.BWB), stimmzettelgebiet.getNummer() + "_B_" + i, stimmzettelgebiet,
                            getCountInRange(options.a1()),
                            getCountInRange(options.a2()),
                            getCountInRange(options.a3())));

            storeWahlvorschlaege(wahlvorschlaegeOfStimmzettelgebiet, wahlbezirk);
        });
    }

    private void storeWahlbezirke(final Collection<WahlbezirkOptionsDTO> wahlbezirkInitOptions,
            final WahlvorschlagInitOptions wahlvorschlagInitOptions, final Stimmzettelgebiet stimmzettelgebiet) {
        val wahlvorschlaegeOfStimmzettelgebiet = setupWahlvorschlaege(wahlvorschlagInitOptions);

        wahlbezirkInitOptions.forEach(option -> {
            val wahlbezirk = wahlbezirkRepository.save(
                    new Wahlbezirk(wahldatenMapper.toEntity(option.wahlbezirkArt()), option.nummer(), stimmzettelgebiet, option.a1(),
                            option.a2(), option.a3()));

            storeWahlvorschlaege(wahlvorschlaegeOfStimmzettelgebiet, wahlbezirk);
        });
    }

    private void storeWahlvorschlaege(final Collection<InitWahlvorschlagModel> wahlvorschlaege, final Wahlbezirk wahlbezirk) {
        val wahlvorschlaegeListeEntity = wahlvorschlaegeListeRepository.save(
                new WahlvorschlaegeListe(wahlbezirk.getStimmzettelgebiet().getWahl().getWahltag().getTag(),
                        wahlbezirk.getStimmzettelgebiet().getWahl().getId().toString(),
                        Collections.emptySet()));

        val wahlvorschlaegeEntity = wahlvorschlagRepository.save(
                new Wahlvorschlaege(wahlbezirk.getId().toString(), wahlbezirk.getStimmzettelgebiet().getWahl().getId().toString(),
                        wahlbezirk.getStimmzettelgebiet().getId().toString(),
                        Collections.emptySet(), wahlvorschlaegeListeEntity));

        val wahlvorschlagEntities = wahlvorschlaege.stream().map(option -> {
            val wahlvorschlagEntity = new Wahlvorschlag(option.ordnungszahl(), option.kurzname(), true, Collections.emptySet(), wahlvorschlaegeEntity);
            val kandidatenEntities = createKandidatenEntities(option.kandidaten(), wahlvorschlagEntity);
            wahlvorschlagEntity.setKandidaten(kandidatenEntities);
            return wahlvorschlagEntity;
        }).collect(Collectors.toSet());

        wahlvorschlaegeEntity.setWahlvorschlaege(wahlvorschlagEntities);
    }

    private Set<Kandidat> createKandidatenEntities(final Collection<InitKandidatModel> wahlvorschlagInitOptions, final Wahlvorschlag wahlvorschlag) {
        return wahlvorschlagInitOptions.stream()
                .map(option -> new Kandidat(option.name(), option.listenposition(), false, option.listenposition(), false, wahlvorschlag)).collect(
                        Collectors.toSet());

    }

    private Collection<InitWahlvorschlagModel> setupWahlvorschlaege(final WahlvorschlagInitOptions wahlvorschlagInitOptions) {
        val countWahwahlvorschleage = getCountInRange(
                new RangeDTO(wahlvorschlagInitOptions.wahlvorschlaegeMinCount(), wahlvorschlagInitOptions.wahlvorschlaegeMaxCount()));
        log.atDebug().log("countWahwahlvorschleage: {}", countWahwahlvorschleage);

        return IntStream.rangeClosed(1, countWahwahlvorschleage).mapToObj(i -> {
            val kandidaten = setupKandidaten(wahlvorschlagInitOptions);
            return new InitWahlvorschlagModel(i, "Kurzname " + i, kandidaten);
        }).toList();
    }

    private Collection<InitKandidatModel> setupKandidaten(final WahlvorschlagInitOptions wahlvorschlagInitOptions) {
        val countKandidaten = getCountInRange(new RangeDTO(wahlvorschlagInitOptions.kandidatenMinCount(), wahlvorschlagInitOptions.kandidatenMaxCount()));

        return IntStream.rangeClosed(1, countKandidaten).mapToObj(i -> new InitKandidatModel("Kandidat " + i, i)).toList();
    }

    private static String createDefaultBeschreibung(final WahltagInitOptionsDTO wahltagInit) {
        return "beschreibung of " + wahltagInit.wahltag() + " nummer " + wahltagInit.nummer();
    }

    private int getCountInRange(final RangeDTO range) {
        return range.getValueInRange();
    }
}
