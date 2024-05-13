package de.muenchen.oss.wahllokalsystem.briefwahlservice.rest.beanstandetewahlbriefe;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.common.beanstandetewahlbriefe.Zurueckweisungsgrund;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe.BeanstandeteWahlbriefeModel;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe.BeanstandeteWahlbriefeReference;
import java.util.HashMap;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class BeanstandeteWahlbriefeDTOMapperTest {

    private final BeanstandeteWahlbriefeDTOMapper unitUnderTest = Mappers.getMapper(BeanstandeteWahlbriefeDTOMapper.class);

    @Nested
    class ToCreateModel {

        @Test
        void nullInNullOut() {
            Assertions.assertThat(unitUnderTest.toCreateModel(null, null, null)).isNull();
        }

        @Test
        void isMapped() {
            val zurueckweisungen = new HashMap<String, Zurueckweisungsgrund[]>();
            zurueckweisungen.put("wahl1", new Zurueckweisungsgrund[] { Zurueckweisungsgrund.ZUGELASSEN, Zurueckweisungsgrund.UNTERSCHRIFT_FEHLT });
            zurueckweisungen.put("wahl2", new Zurueckweisungsgrund[] { Zurueckweisungsgrund.KEIN_ORIGINAL_SCHEIN });
            val dtoToMap = new BeanstandeteWahlbriefeCreateDTO(zurueckweisungen);
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnisNummer = 3L;

            val expectedZurueckweisungen = new HashMap<String, Zurueckweisungsgrund[]>();
            expectedZurueckweisungen.put("wahl1", new Zurueckweisungsgrund[] { Zurueckweisungsgrund.ZUGELASSEN, Zurueckweisungsgrund.UNTERSCHRIFT_FEHLT });
            expectedZurueckweisungen.put("wahl2", new Zurueckweisungsgrund[] { Zurueckweisungsgrund.KEIN_ORIGINAL_SCHEIN });
            val expectedResult = new BeanstandeteWahlbriefeModel(wahlbezirkID, waehlerverzeichnisNummer, expectedZurueckweisungen);

            val result = unitUnderTest.toCreateModel(dtoToMap, wahlbezirkID, waehlerverzeichnisNummer);

            Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToReferenceModel {

        @Test
        void nullInNullOut() {
            Assertions.assertThat(unitUnderTest.toReferenceModel(null, null)).isNull();
        }

        @Test
        void isMapped() {
            val wahlbezirkId = "wahlbezirkID";
            val waehlerverzeichnisNummer = 3L;

            val expectedResult = new BeanstandeteWahlbriefeReference(wahlbezirkId, waehlerverzeichnisNummer);

            val result = unitUnderTest.toReferenceModel(wahlbezirkId, waehlerverzeichnisNummer);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToDTO {
        @Test
        void nullInNullOut() {
            Assertions.assertThat(unitUnderTest.toDTO(null)).isNull();
        }

        @Test
        void isMapped() {
            val wahlbezirkId = "wahlbezirkID";
            val waehlerverzeichnisNummer = 3L;
            val zurueckweisungen = new HashMap<String, Zurueckweisungsgrund[]>();
            zurueckweisungen.put("wahl1", new Zurueckweisungsgrund[] { Zurueckweisungsgrund.UNTERSCHRIFT_FEHLT, Zurueckweisungsgrund.KEIN_ORIGINAL_SCHEIN });
            zurueckweisungen.put("wahl2", new Zurueckweisungsgrund[] { Zurueckweisungsgrund.GEGENSTAND_IM_UMSCHLAG });
            val modelToMap = new BeanstandeteWahlbriefeModel(wahlbezirkId, waehlerverzeichnisNummer, zurueckweisungen);

            val expectedZurueckweisungen = new HashMap<String, Zurueckweisungsgrund[]>();
            expectedZurueckweisungen.put("wahl1",
                    new Zurueckweisungsgrund[] { Zurueckweisungsgrund.UNTERSCHRIFT_FEHLT, Zurueckweisungsgrund.KEIN_ORIGINAL_SCHEIN });
            expectedZurueckweisungen.put("wahl2", new Zurueckweisungsgrund[] { Zurueckweisungsgrund.GEGENSTAND_IM_UMSCHLAG });
            val expectedResult = new BeanstandeteWahlbriefeDTO(wahlbezirkId, waehlerverzeichnisNummer, expectedZurueckweisungen);

            val result = unitUnderTest.toDTO(modelToMap);

            Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
        }
    }
}
