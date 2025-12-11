package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahllokalZustand;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahllokalzustand.WahllokalZustand;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahllokalzustand.WahllokalZustandRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.WahllokalZustandDTO;
import java.util.Collections;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WahllokalZustandServiceTest {

  @Mock WahllokalZustandMapper wahllokalZustandMapper;

  @Mock WahllokalZustandValidator wahllokalZustandValidator;

  @Mock WahllokalZustandRepository wahllokalZustandRepository;

  @InjectMocks WahllokalZustandService unitUnderTest;

  @Nested
  class SetWahllokalZustand {

    @Test
    void should_callValidatorAndRepo_when_dtoIsGiven() {
      val wahllokalZustand =
          new WahllokalZustandDTO("wahlbezirkID", null, null, Collections.emptySet());

      val mockedMappedWahllokalZustand = new WahllokalZustand();
      Mockito.when(wahllokalZustandMapper.toEntity(wahllokalZustand))
          .thenReturn(mockedMappedWahllokalZustand);

      unitUnderTest.setWahllokalZustand(wahllokalZustand);

      Mockito.verify(wahllokalZustandValidator).validWahllokalZustandOrThrow(wahllokalZustand);
      Mockito.verify(wahllokalZustandRepository).save(mockedMappedWahllokalZustand);
    }
  }
}
