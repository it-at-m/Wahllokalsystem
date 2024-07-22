package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage.ReferendumoptionModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage.ReferendumvorlageModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage.ReferendumvorlagenClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage.ReferendumvorlagenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage.ReferendumvorlagenReferenceModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.KandidatModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.WahlvorschlaegeClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.WahlvorschlaegeModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.WahlvorschlagModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Set;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.DUMMY_CLIENTS)
public class DummyClientImpl implements WahlvorschlaegeClient, ReferendumvorlagenClient {

    @Override
    public WahlvorschlaegeModel getWahlvorschlaege(BezirkUndWahlID bezirkUndWahlID) {
        return new WahlvorschlaegeModel(bezirkUndWahlID, "stimmzettelgebiedID",
                Set.of(new WahlvorschlagModel(UUID.randomUUID().toString(), 1L, "kurzname1", true,
                        Set.of(new KandidatModel(UUID.randomUUID().toString(), "kandidat11", 1L, true, 1L, true),
                                new KandidatModel(UUID.randomUUID().toString(), "kandidat21", 2L, false, 1L, false))),
                        new WahlvorschlagModel(UUID.randomUUID().toString(), 2L, "kurzname2", true,
                                Set.of(new KandidatModel(UUID.randomUUID().toString(), "kandidat21", 1L, true, 1L, true),
                                        new KandidatModel(UUID.randomUUID().toString(), "kandidat22", 2L, false, 1L, false)))));
    }

    @Override
    public ReferendumvorlagenModel getReferendumvorlagen(ReferendumvorlagenReferenceModel referendumvorlagenReferenceModel) {
        return new ReferendumvorlagenModel("stimmzettelgebiedID", Set.of(new ReferendumvorlageModel("wahlvorschlagID1", 1L, "kurzname1", "frage1",
                Set.of(new ReferendumoptionModel("optionID11", "option11", 1L), new ReferendumoptionModel("optionID12", "option12", 2L))),
                new ReferendumvorlageModel("wahlvorschlagID2", 2L, "kurzname2", "frage2",
                        Set.of(new ReferendumoptionModel("optionID21", "option21", 1L), new ReferendumoptionModel("optionID22", "option22", 2L)))));
    }
}
