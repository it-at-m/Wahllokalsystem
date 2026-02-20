package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahltermindaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlagenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.WahlvorschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.common.WahltagWithNummerModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.kopfdaten.BasisdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.referendumvorlagen.ReferendumvorlagenClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.referendumvorlagen.ReferendumvorlagenModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.referendumvorlagen.ReferendumvorlagenReferenceModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahlen.WahlartModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahlvorschlag.WahlvorschlaegeClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahlvorschlag.WahlvorschlaegeModelMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncWahltermindatenService {

  private final AsyncProgress asyncProgress;

  private final WahlvorschlaegeClient wahlvorschlaegeClient;
  private final ReferendumvorlagenClient referendumvorlagenClient;

  private final WahlvorschlaegeRepository wahlvorschlaegeRepository;
  private final ReferendumvorlagenRepository referendumvorlagenRepository;

  private final WahlvorschlaegeModelMapper wahlvorschlaegeModelMapper;
  private final ReferendumvorlagenModelMapper referendumvorlagenModelMapper;

  @Async
  public void initVorlagenAndVorschlaege(
      final WahltagWithNummerModel wahltagWithNummerModel, final BasisdatenModel basisdaten) {
    asyncProgress.reset(wahltagWithNummerModel.wahltag(), wahltagWithNummerModel.wahltagNummer());
    initWahlvorschlaege(basisdaten);
    initReferendumvorlagen(basisdaten);
    asyncProgress.setLastFinishTime(LocalDateTime.now());
  }

  private void initWahlvorschlaege(final BasisdatenModel basisdaten) {
    asyncProgress.setWahlvorschlaegeTotal(
        basisdaten.wahlen().size() * basisdaten.wahlbezirke().size());

    val currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
    basisdaten.wahlen().parallelStream()
        .filter(
            wahl ->
                !WahlartModel.VE.equals(wahl.wahlart()) && !WahlartModel.BEB.equals(wahl.wahlart()))
        .forEach(
            wahl ->
                basisdaten.wahlbezirke().parallelStream()
                    .forEach(
                        wahlbezirk -> {
                          if (wahl.wahlID().equals(wahlbezirk.wahlID())) {
                            SecurityContextHolder.getContext()
                                .setAuthentication(currentAuthentication);
                            loadAndPersistWahlvorschlaege(
                                wahl.wahlID(), wahlbezirk.wahlbezirkID(), wahlbezirk.nummer());
                            SecurityContextHolder.getContext().setAuthentication(null);
                          }
                        }));
  }

  private void loadAndPersistWahlvorschlaege(String wahlID, String wahlbezirk, String nummer) {
    try {
      log.info("#initWahlvorschlag von Wahlbezirk {}", nummer);
      asyncProgress.setWahlvorschlaegeNext(nummer);
      val wahlvorschlaege =
          wahlvorschlaegeClient.getWahlvorschlaege(new BezirkUndWahlID(wahlID, wahlbezirk));
      val wahlvorschlaegeEntity = wahlvorschlaegeModelMapper.toEntity(wahlvorschlaege);
      wahlvorschlaegeRepository.save(wahlvorschlaegeEntity);
    } catch (final Exception e) {
      log.info(
          "#initReferendumvorlage: Fehler bei initReferendumvortage -> möglicherweise richtiges Verhalten; Fehler:",
          e);
    } finally {
      asyncProgress.incWahlvorschlaegeFinished();
    }
  }

  private void initReferendumvorlagen(final BasisdatenModel basisdaten) {
    if (basisdaten.wahlen().stream()
        .anyMatch(
            wahl ->
                WahlartModel.VE.equals(wahl.wahlart())
                    || WahlartModel.BEB.equals(wahl.wahlart()))) {
      asyncProgress.setReferendumVorlagenTotal(
          basisdaten.wahlen().size() * basisdaten.wahlbezirke().size());
    }

    val currentAuthentication = SecurityContextHolder.getContext().getAuthentication();

    basisdaten.wahlen().parallelStream()
        .filter(
            wahl ->
                WahlartModel.VE.equals(wahl.wahlart()) || WahlartModel.BEB.equals(wahl.wahlart()))
        .forEach(
            wahl ->
                basisdaten.wahlbezirke().parallelStream()
                    .forEach(
                        wahlbezirk -> {
                          if (wahl.wahlID().equals(wahlbezirk.wahlID())) {
                            SecurityContextHolder.getContext()
                                .setAuthentication(currentAuthentication);
                            loadAndPersistReferendumvorlagen(
                                wahl.wahlID(), wahlbezirk.wahlbezirkID(), wahlbezirk.nummer());
                            SecurityContextHolder.getContext().setAuthentication(null);
                          }
                        }));
  }

  private void loadAndPersistReferendumvorlagen(String wahlID, String wahlbezirk, String nummer) {
    try {
      log.info("#initReferendumvorlage von Wahlbezirk {}", nummer);
      asyncProgress.setReferendumVorlagenNext(nummer);
      val referendumvorlagen =
          referendumvorlagenClient.getReferendumvorlagen(
              new ReferendumvorlagenReferenceModel(wahlID, wahlbezirk));
      val referendumvorlagenEntity =
          referendumvorlagenModelMapper.toEntity(
              referendumvorlagen, new BezirkUndWahlID(wahlID, wahlbezirk));
      referendumvorlagenRepository.save(referendumvorlagenEntity);
    } catch (final Exception e) {
      log.info(
          "#initReferendumvorlage: Fehler bei initReferendumvorlage -> möglicherweise richtiges Verhalten; Fehler:",
          e);
    } finally {
      asyncProgress.incReferendumVorlagenFinished();
    }
  }
}
