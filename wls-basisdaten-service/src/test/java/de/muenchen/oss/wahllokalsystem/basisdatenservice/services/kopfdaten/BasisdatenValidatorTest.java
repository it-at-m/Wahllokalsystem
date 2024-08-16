package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import com.google.common.collect.ImmutableSet;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.MockDataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasisdatenValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    BasisdatenValidator unitUnderTest;

    @Nested
    class ValidBasisdatenContentOrThrow {

        final FachlicheWlsException mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

        @Test
        void noExceptionWhenBasisdatenContentIsValid() {
            val valideBasisdaten = MockDataFactory.createBasisdatenModel(LocalDate.now());
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validBasisdatenContentOrThrow(valideBasisdaten));
        }

        @Test
        void exceptionWhenNotEveryPropertyContaininsData() {
            val valideBasisdaten = MockDataFactory.createBasisdatenModel(LocalDate.now());
            val invalideBD1_0 = BasisdatenModel.builder()
                    .basisstrukturdaten(null)
                    .wahlbezirke(valideBasisdaten.wahlbezirke())
                    .wahlen(valideBasisdaten.wahlen())
                    .stimmzettelgebiete(valideBasisdaten.stimmzettelgebiete())
                    .build();
            val invalideBD1_1 = BasisdatenModel.builder()
                    .basisstrukturdaten(Collections.emptySet())
                    .wahlbezirke(valideBasisdaten.wahlbezirke())
                    .wahlen(valideBasisdaten.wahlen())
                    .stimmzettelgebiete(valideBasisdaten.stimmzettelgebiete())
                    .build();
            val invalideBD2_0 = BasisdatenModel.builder()
                    .basisstrukturdaten(valideBasisdaten.basisstrukturdaten())
                    .wahlbezirke(null)
                    .wahlen(valideBasisdaten.wahlen())
                    .stimmzettelgebiete(valideBasisdaten.stimmzettelgebiete())
                    .build();
            val invalideBD2_1 = BasisdatenModel.builder()
                    .basisstrukturdaten(valideBasisdaten.basisstrukturdaten())
                    .wahlbezirke(Collections.emptySet())
                    .wahlen(valideBasisdaten.wahlen())
                    .stimmzettelgebiete(valideBasisdaten.stimmzettelgebiete())
                    .build();
            val invalideBD3_0 = BasisdatenModel.builder()
                    .basisstrukturdaten(valideBasisdaten.basisstrukturdaten())
                    .wahlbezirke(valideBasisdaten.wahlbezirke())
                    .wahlen(null)
                    .stimmzettelgebiete(valideBasisdaten.stimmzettelgebiete())
                    .build();
            val invalideBD3_1 = BasisdatenModel.builder()
                    .basisstrukturdaten(valideBasisdaten.basisstrukturdaten())
                    .wahlbezirke(valideBasisdaten.wahlbezirke())
                    .wahlen(Collections.emptySet())
                    .stimmzettelgebiete(valideBasisdaten.stimmzettelgebiete())
                    .build();
            val invalideBD4_0 = BasisdatenModel.builder()
                    .basisstrukturdaten(valideBasisdaten.basisstrukturdaten())
                    .wahlbezirke(valideBasisdaten.wahlbezirke())
                    .wahlen(valideBasisdaten.wahlen())
                    .stimmzettelgebiete(null)
                    .build();
            val invalideBD4_1 = BasisdatenModel.builder()
                    .basisstrukturdaten(valideBasisdaten.basisstrukturdaten())
                    .wahlbezirke(valideBasisdaten.wahlbezirke())
                    .wahlen(valideBasisdaten.wahlen())
                    .stimmzettelgebiete(Collections.emptySet())
                    .build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.INITIALIZE_KOPFDATEN_BASISDATEN_DATA_INCONSISTENTCY))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validBasisdatenContentOrThrow(invalideBD1_0)).isSameAs(mockedWlsException);
            Assertions.assertThatThrownBy(() -> unitUnderTest.validBasisdatenContentOrThrow(invalideBD1_1)).isSameAs(mockedWlsException);
            Assertions.assertThatThrownBy(() -> unitUnderTest.validBasisdatenContentOrThrow(invalideBD2_0)).isSameAs(mockedWlsException);
            Assertions.assertThatThrownBy(() -> unitUnderTest.validBasisdatenContentOrThrow(invalideBD2_1)).isSameAs(mockedWlsException);
            Assertions.assertThatThrownBy(() -> unitUnderTest.validBasisdatenContentOrThrow(invalideBD3_0)).isSameAs(mockedWlsException);
            Assertions.assertThatThrownBy(() -> unitUnderTest.validBasisdatenContentOrThrow(invalideBD3_1)).isSameAs(mockedWlsException);
            Assertions.assertThatThrownBy(() -> unitUnderTest.validBasisdatenContentOrThrow(invalideBD4_0)).isSameAs(mockedWlsException);
            Assertions.assertThatThrownBy(() -> unitUnderTest.validBasisdatenContentOrThrow(invalideBD4_1)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptioIfNotforEveryBasistrutkturdatenCorespondingAtLeastOneWahlOneWahlbezirkAndOneStimmzettelgebiet() {
            val valideBasisdaten = MockDataFactory.createBasisdatenModel(LocalDate.now());
            Set<BasisstrukturdatenModel> moreBasisstrukturdaten = new ImmutableSet.Builder<BasisstrukturdatenModel>()
                    .add(BasisstrukturdatenModel.builder().wahlID("wahlID999").build())
                    .addAll(valideBasisdaten.basisstrukturdaten())
                    .build();

            val invalideBSD = BasisdatenModel.builder()
                    .basisstrukturdaten(moreBasisstrukturdaten)
                    .wahlbezirke(valideBasisdaten.wahlbezirke())
                    .wahlen(valideBasisdaten.wahlen())
                    .stimmzettelgebiete(valideBasisdaten.stimmzettelgebiete())
                    .build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.INITIALIZE_KOPFDATEN_BASISDATEN_DATA_INCONSISTENTCY))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validBasisdatenContentOrThrow(invalideBSD)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptioIfNotforEveryWahlCorespondingAtLeastOneBasistrutkturdatenAndOneWahlbezirk() {
            val valideBasisdaten = MockDataFactory.createBasisdatenModel(LocalDate.now());

            Set<WahlModel> moreWahlen = new ImmutableSet.Builder<WahlModel>()
                    .add(WahlModel.builder().wahlID("wahlID999").build())
                    .addAll(valideBasisdaten.wahlen())
                    .build();
            val invalideWahlen = BasisdatenModel.builder()
                    .basisstrukturdaten(valideBasisdaten.basisstrukturdaten())
                    .wahlbezirke(valideBasisdaten.wahlbezirke())
                    .wahlen(moreWahlen)
                    .stimmzettelgebiete(valideBasisdaten.stimmzettelgebiete())
                    .build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.INITIALIZE_KOPFDATEN_BASISDATEN_DATA_INCONSISTENTCY))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validBasisdatenContentOrThrow(invalideWahlen)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptioIfNotforEveryWahlbezirkCorespondingAtLeastOneBasistrutkturdatenAndOneWahl() {
            val valideBasisdaten = MockDataFactory.createBasisdatenModel(LocalDate.now());

            Set<WahlbezirkModel> moreWahlbezirke = new ImmutableSet.Builder<WahlbezirkModel>()
                    .add(WahlbezirkModel.builder().identifikator("wahlbezirk999").wahlID("wahlID987").build())
                    .addAll(valideBasisdaten.wahlbezirke())
                    .build();
            val invalideWahlbezirke = BasisdatenModel.builder()
                    .basisstrukturdaten(valideBasisdaten.basisstrukturdaten())
                    .wahlbezirke(moreWahlbezirke)
                    .wahlen(valideBasisdaten.wahlen())
                    .stimmzettelgebiete(valideBasisdaten.stimmzettelgebiete())
                    .build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.INITIALIZE_KOPFDATEN_BASISDATEN_DATA_INCONSISTENTCY))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validBasisdatenContentOrThrow(invalideWahlbezirke)).isSameAs(mockedWlsException);
        }

        @Test
        void exceptioIfNotforEveryStimmzettelgebietCorespondingAtLeastOneBasistrutkturdaten() {
            val valideBasisdaten = MockDataFactory.createBasisdatenModel(LocalDate.now());

            Set<StimmzettelgebietModel> moreStimmzettelGebiete = new ImmutableSet.Builder<StimmzettelgebietModel>()
                    .add(StimmzettelgebietModel.builder().identifikator("szgb999").build())
                    .addAll(valideBasisdaten.stimmzettelgebiete())
                    .build();
            val invalideStimmzettelgebiete = BasisdatenModel.builder()
                    .basisstrukturdaten(valideBasisdaten.basisstrukturdaten())
                    .wahlbezirke(valideBasisdaten.wahlbezirke())
                    .wahlen(valideBasisdaten.wahlen())
                    .stimmzettelgebiete(moreStimmzettelGebiete)
                    .build();

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.INITIALIZE_KOPFDATEN_BASISDATEN_DATA_INCONSISTENTCY))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.validBasisdatenContentOrThrow(invalideStimmzettelgebiete)).isSameAs(mockedWlsException);
        }
    }
}
