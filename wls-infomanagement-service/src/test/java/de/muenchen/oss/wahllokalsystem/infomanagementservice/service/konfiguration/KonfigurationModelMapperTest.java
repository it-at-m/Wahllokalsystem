package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.Konfiguration;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KennbuchstabenListeModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KennbuchstabenListenModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KennbuchstabenModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationKonfigKey;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationSetModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.WahlbezirkArt;
import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class KonfigurationModelMapperTest {

    private final KonfigurationModelMapper unitUnderTest = Mappers.getMapper(KonfigurationModelMapper.class);

    @Nested
    class ToModel {
        @Test
        void entityIsMapped() {
            val key = "key";
            val value = "value";
            val description = "description";
            val defaultValue = "defaultValue";
            val entityToMap = new Konfiguration(key, value, description, defaultValue);

            val expectedResult = new KonfigurationModel(key, value, description, defaultValue);

            val result = unitUnderTest.toModel(entityToMap);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class GetAlternativKey {

        @Test
        void alternativKeyWasFound() {
            Assertions.assertThat(unitUnderTest.getAlternativKey(KonfigurationKonfigKey.FRUEHESTE_EROEFFNUNGSZEIT, WahlbezirkArt.BWB).get()).isNotNull();
        }

        @Test
        void noAlternativKeyWasFound() {
            Assertions.assertThat(unitUnderTest.getAlternativKey(KonfigurationKonfigKey.WILLKOMMENSTEXT, WahlbezirkArt.BWB)).isEmpty();
        }
    }

    @Nested
    class ToEntity {

        @Test
        void modelIsMappedToEntity() {
            val schluessel = "schluessel";
            val wert = "wert";
            val beschreibung = "beschreibung";
            val standardwert = "standardwert";
            val modelToMap = new KonfigurationSetModel(schluessel, wert, beschreibung, standardwert);
            val expectedResult = new Konfiguration(schluessel, wert, beschreibung, standardwert);

            val result = unitUnderTest.toEntity(modelToMap);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

    }

    @Nested
    class MapStandardwertFromModel {

        @Test
        void mapExistingStandardwert() {
            val wert = "wert";
            val standardwert = "standardwert";
            val modelToMap = KonfigurationSetModel.builder().standardwert(standardwert).wert(wert).build();

            val result = unitUnderTest.mapStandardwertFromModel(modelToMap);

            Assertions.assertThat(result).isEqualTo(standardwert);
        }

        @Test
        void mapFallbackValueWhenStandardwertIsNull() {
            val wert = "wert";
            final String standardwert = null;
            val modelToMap = KonfigurationSetModel.builder().standardwert(standardwert).wert(wert).build();

            val result = unitUnderTest.mapStandardwertFromModel(modelToMap);

            Assertions.assertThat(result).isEqualTo(wert);
        }
    }

    @Nested
    class ToKennbuchstabenModelTests {
        //includes all default methods of the mapper because they are using each other

        @Test
        void stringIsMappedToKennbuchstabenListenModel() {
            val stringToMap = "K1,K2,K3 (K1 + K2),K4,K (K3 + K4);L1,L2,L3 (L1 + L2),L4,L (L3 + L4)$K1,K2,K3 (K1 + K2),K4,"
                    + "K (K3 + K4);L1,L2,L3 (L1 + L2),L4,L (L3 + L4)";

            List<KennbuchstabenListeModel> kennbuchstabenListe_list = new ArrayList<>();
            val expectedKennbuchstabenListen = new KennbuchstabenListenModel(kennbuchstabenListe_list);

            val kennbuchstaben_1_list = new ArrayList<KennbuchstabenModel>();
            val kennbuchstabenListe_1 = new KennbuchstabenListeModel(kennbuchstaben_1_list);

            val kennbuchstaben_2_list = new ArrayList<KennbuchstabenModel>();
            val kennbuchstabenListe_2 = new KennbuchstabenListeModel(kennbuchstaben_2_list);

            val kennbuchstaben_1_1_list = List.of("K1", "K2", "K3 (K1 + K2)", "K4", "K (K3 + K4)");
            val kennbuchstaben_1_1 = new KennbuchstabenModel(kennbuchstaben_1_1_list);
            kennbuchstaben_1_list.add(kennbuchstaben_1_1);

            val kennbuchstaben_1_2_list = List.of("L1", "L2", "L3 (L1 + L2)", "L4", "L (L3 + L4)");
            val kennbuchstaben_1_2 = new KennbuchstabenModel(kennbuchstaben_1_2_list);
            kennbuchstaben_1_list.add(kennbuchstaben_1_2);

            kennbuchstabenListe_list.add(kennbuchstabenListe_1);

            val kennbuchstaben_2_1_list = List.of("K1", "K2", "K3 (K1 + K2)", "K4", "K (K3 + K4)");
            val kennbuchstaben_2_1 = new KennbuchstabenModel(kennbuchstaben_2_1_list);
            kennbuchstaben_2_list.add(kennbuchstaben_2_1);

            val kennbuchstaben_2_2_list = List.of("L1", "L2", "L3 (L1 + L2)", "L4", "L (L3 + L4)");
            val kennbuchstaben_2_2 = new KennbuchstabenModel(kennbuchstaben_2_2_list);
            kennbuchstaben_2_list.add(kennbuchstaben_2_2);

            kennbuchstabenListe_list.add(kennbuchstabenListe_2);

            Assertions.assertThat(unitUnderTest.toKennbuchstabenListenModel(stringToMap)).isEqualTo(expectedKennbuchstabenListen);
        }
    }

    @Test
    void toKennbuchstabenListeModel() {
        val stringToMap = "first; second";

        val result = unitUnderTest.toKennbuchstabenListeModel(stringToMap);

        val expectedResult = new KennbuchstabenListeModel(List.of(new KennbuchstabenModel(List.of("first")), new KennbuchstabenModel(List.of(" second"))));

        Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void toKennbuchstabenModel() {
        val stringToMap = "a,b , 1";

        val result = unitUnderTest.toKennbuchstabenModel(stringToMap);

        val expectedResult = new KennbuchstabenModel(List.of("a", "b ", " 1"));

        Assertions.assertThat(result).isEqualTo(expectedResult);
    }

}
