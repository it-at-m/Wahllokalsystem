package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.Meldung;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.Status;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.Validierungsstatus;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

class StatusModelMapperTest {

    private StatusModelMapper unitUnderTest = Mappers.getMapper(StatusModelMapper.class);

    @Nested
    class ToModel {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toModel(null)).isNull();
        }

        @Test
        void should_returnStatusModel_when_givenStatusEntity() {
            val niederschrift = new Meldung();
            niederschrift.setGedruckt(true);
            niederschrift.setSendeuhrzeit(LocalDateTime.now());
            niederschrift.setUebermittelt(true);
            niederschrift.setValidierungsstatus(Validierungsstatus.VALIDE);

            val schnellmeldung = new Meldung();
            schnellmeldung.setGedruckt(true);
            schnellmeldung.setSendeuhrzeit(LocalDateTime.now().minusDays(1));
            schnellmeldung.setUebermittelt(true);
            schnellmeldung.setValidierungsstatus(Validierungsstatus.INVALIDE);

            val statusEntiy = new Status(new BezirkUndWahlID("wahlID", "wahlbezirkID"), schnellmeldung, niederschrift);

            val result = unitUnderTest.toModel(statusEntiy);

            val expectedResult = new StatusModel(
                    new BezirkUndWahlID("wahlID", "wahlbezirkID"),
                    new MeldungModel(ValidierungsstatusModel.INVALIDE, schnellmeldung.isGedruckt(), schnellmeldung.getUebermittelt(),
                            schnellmeldung.getSendeuhrzeit()),
                    new MeldungModel(ValidierungsstatusModel.VALIDE, niederschrift.isGedruckt(), niederschrift.getUebermittelt(),
                            niederschrift.getSendeuhrzeit()));
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @ParameterizedTest
        @EnumSource(Validierungsstatus.class)
        void should_mapToEnumWithSameName_when_givenEntityValidierungsstatusEnumValue(final Validierungsstatus validierungsstatus) {
            val niederschrift = new Meldung();
            niederschrift.setValidierungsstatus(validierungsstatus);

            val schnellmeldung = new Meldung();
            schnellmeldung.setValidierungsstatus(validierungsstatus);

            val statusEntity = new Status(null, schnellmeldung, niederschrift);

            val result = unitUnderTest.toModel(statusEntity);

            val expectedResult = new StatusModel(null,
                    new MeldungModel(ValidierungsstatusModel.valueOf(validierungsstatus.name()), false, null, null),
                    new MeldungModel(ValidierungsstatusModel.valueOf(validierungsstatus.name()), false, null, null));
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToEntity {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toEntity(null)).isNull();
        }

        @Test
        void should_returnStatusEntity_when_givenStatusModel() {
            val statusModel = new StatusModel(new BezirkUndWahlID("wahlID", "wahlbezirkID"),
                    new MeldungModel(ValidierungsstatusModel.VALIDE, true, true, LocalDateTime.now()),
                    new MeldungModel(ValidierungsstatusModel.INVALIDE, true, true, LocalDateTime.now().minusDays(1)));

            val result = unitUnderTest.toEntity(statusModel);

            val expectedSchnellMeldung = new Meldung();
            expectedSchnellMeldung.setGedruckt(statusModel.schnellmeldung().gedruckt());
            expectedSchnellMeldung.setSendeuhrzeit(statusModel.schnellmeldung().sendeuhrzeit());
            expectedSchnellMeldung.setValidierungsstatus(Validierungsstatus.VALIDE);
            expectedSchnellMeldung.setUebermittelt(statusModel.schnellmeldung().uebermittelt());

            val expectedNiederschrift = new Meldung();
            expectedNiederschrift.setGedruckt(statusModel.niederschrift().gedruckt());
            expectedNiederschrift.setSendeuhrzeit(statusModel.niederschrift().sendeuhrzeit());
            expectedNiederschrift.setValidierungsstatus(Validierungsstatus.INVALIDE);
            expectedNiederschrift.setUebermittelt(statusModel.niederschrift().uebermittelt());

            val expectedResult = new Status(new BezirkUndWahlID("wahlID", "wahlbezirkID"), expectedSchnellMeldung, expectedNiederschrift);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @ParameterizedTest
        @EnumSource(ValidierungsstatusModel.class)
        void should_mapToEnumWithSameName_when_givenModelValidierungsstatusEnumValue(final ValidierungsstatusModel validierungsstatus) {
            val statusModel = new StatusModel(null,
                    new MeldungModel(validierungsstatus, false, null, null),
                    new MeldungModel(validierungsstatus, false, null, null));

            val result = unitUnderTest.toEntity(statusModel);

            val expectedSchnellmeldung = new Meldung();
            expectedSchnellmeldung.setValidierungsstatus(Validierungsstatus.valueOf(validierungsstatus.name()));
            val expectedNiederschrift = new Meldung();
            expectedNiederschrift.setValidierungsstatus(Validierungsstatus.valueOf(validierungsstatus.name()));
            val expectedResult = new Status(null, expectedSchnellmeldung, expectedNiederschrift);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
