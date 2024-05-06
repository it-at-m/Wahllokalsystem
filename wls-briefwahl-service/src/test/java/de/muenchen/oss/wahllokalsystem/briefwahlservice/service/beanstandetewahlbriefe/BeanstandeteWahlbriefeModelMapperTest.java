package de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.common.beanstandetewahlbriefe.Zurueckweisungsgrund;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.BeanstandeteWahlbriefe;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import java.util.HashMap;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class BeanstandeteWahlbriefeModelMapperTest {

    private final BeanstandeteWahlbriefeModelMapper unitUnderTest = Mappers.getMapper(BeanstandeteWahlbriefeModelMapper.class);

    @Nested
    class ToEmbeddedId {

        @Test
        void nullInNullOut() {
            Assertions.assertThat(unitUnderTest.toEmbeddedId(null)).isNull();
        }

        @Test
        void objectIsMapped() {
            val wahlbezirkID = "wbzId";
            val waehlerverzeichnisNummer = 123L;
            val reference = new BeanstandeteWahlbriefeReference(wahlbezirkID, waehlerverzeichnisNummer);

            val expectedResult = new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer);

            val result = unitUnderTest.toEmbeddedId(reference);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToModel {

        @Test
        void nullInNullOut() {
            Assertions.assertThat(unitUnderTest.toModel(null)).isNull();
        }

        @Test
        void objectIsMapped() {
            val wahlbezirkID = "wbzId";
            val waehlerverzeichnisNummer = 123L;
            val id = new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer);
            val zurueckweisungen = new HashMap<String, Zurueckweisungsgrund[]>();
            zurueckweisungen.put("wahl1", new Zurueckweisungsgrund[] { Zurueckweisungsgrund.ZUGELASSEN, Zurueckweisungsgrund.NICHT_WAHLBERECHTIGT });
            zurueckweisungen.put("wahl2", new Zurueckweisungsgrund[] { Zurueckweisungsgrund.ZUGELASSEN, Zurueckweisungsgrund.NICHT_WAHLBERECHTIGT,
                    Zurueckweisungsgrund.UNTERSCHRIFT_FEHLT });
            val beanstandeteWahlbriefe = new BeanstandeteWahlbriefe(id, zurueckweisungen);

            val expectedZurueckweisungen = new HashMap<String, Zurueckweisungsgrund[]>();
            expectedZurueckweisungen.put("wahl1", new Zurueckweisungsgrund[] { Zurueckweisungsgrund.ZUGELASSEN, Zurueckweisungsgrund.NICHT_WAHLBERECHTIGT });
            expectedZurueckweisungen.put("wahl2", new Zurueckweisungsgrund[] { Zurueckweisungsgrund.ZUGELASSEN, Zurueckweisungsgrund.NICHT_WAHLBERECHTIGT,
                    Zurueckweisungsgrund.UNTERSCHRIFT_FEHLT });
            val expecetedResult = new BeanstandeteWahlbriefeModel(wahlbezirkID, waehlerverzeichnisNummer, expectedZurueckweisungen);

            val result = unitUnderTest.toModel(beanstandeteWahlbriefe);

            Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expecetedResult);
        }
    }

    @Nested
    class ToEntity {

        @Test
        void nullInNullOut() {
            Assertions.assertThat(unitUnderTest.toEntity(null)).isNull();
        }

        @Test
        void objectIsMapped() {
            val wahlbezirkId = "wbzId";
            val waehlerverzeichnisnummer = 2L;
            val zurueckweisungen = new HashMap<String, Zurueckweisungsgrund[]>();
            zurueckweisungen.put("wahl1", new Zurueckweisungsgrund[] { Zurueckweisungsgrund.ZUGELASSEN, Zurueckweisungsgrund.NICHT_WAHLBERECHTIGT });
            zurueckweisungen.put("wahl2", new Zurueckweisungsgrund[] { Zurueckweisungsgrund.ZUGELASSEN, Zurueckweisungsgrund.NICHT_WAHLBERECHTIGT,
                    Zurueckweisungsgrund.UNTERSCHRIFT_FEHLT });
            val objectToMap = new BeanstandeteWahlbriefeModel(wahlbezirkId, waehlerverzeichnisnummer, zurueckweisungen);

            val id = new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkId, waehlerverzeichnisnummer);
            val expectedZurueckweisungen = new HashMap<String, Zurueckweisungsgrund[]>();
            expectedZurueckweisungen.put("wahl1", new Zurueckweisungsgrund[] { Zurueckweisungsgrund.ZUGELASSEN, Zurueckweisungsgrund.NICHT_WAHLBERECHTIGT });
            expectedZurueckweisungen.put("wahl2", new Zurueckweisungsgrund[] { Zurueckweisungsgrund.ZUGELASSEN, Zurueckweisungsgrund.NICHT_WAHLBERECHTIGT,
                    Zurueckweisungsgrund.UNTERSCHRIFT_FEHLT });
            val expectedResult = new BeanstandeteWahlbriefe(id, expectedZurueckweisungen);

            val result = unitUnderTest.toEntity(objectToMap);

            Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
        }
    }
}
