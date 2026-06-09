package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.BedenklicheStimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.BedenklicheStimmzettelRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
class MBWBedenklicheStimmzettelServiceTest {

  @Mock BedenklicheStimmzettelRepository repository;

  @Mock BedenklicheStimmzettelModelMapper modelMapper;

  @InjectMocks MBWBedenklicheStimmzettelService unitUnderTest;

  @Nested
  class GetBedenklicheStimmzettelOrderedByOrderIndexAsc {

    @Test
    void should_returnMappedModel_when_entitiesWereFound() {
      val bezirkUndWahlId = new BezirkUndWahlID("wahlID", "wahlbezirkID");

      val mockedEntity = new BedenklicheStimmzettel();

      Mockito.when(
              repository.findByBezirkUndWahlIDOrderbyOrderIndexAsc(
                  Mockito.eq(bezirkUndWahlId.getWahlbezirkID()),
                  Mockito.eq(bezirkUndWahlId.getWahlID())))
          .thenReturn(Optional.of(mockedEntity));

      val mockedMappedEntity =
          new BedenklicherStimmzettelModel(0, Collections.emptySet(), ValidityModel.INVALID);
      Mockito.when(modelMapper.toModel(mockedEntity.getBedenklicheStimmzettels()))
          .thenReturn(List.of(mockedMappedEntity));

      val result = unitUnderTest.getBedenklicheStimmzettelOrderedByOrderIndexAsc(bezirkUndWahlId);

      val expectedResult = List.of(mockedMappedEntity);
      Assertions.assertThat(result).isEqualTo(Optional.of(expectedResult));
    }

    @Test
    void should_returnEmptyOptional_when_noEntitiesWereFound() {
      val bezirkUndWahlId = new BezirkUndWahlID("wahlID", "wahlbezirkID");

      Mockito.when(
              repository.findByBezirkUndWahlIDOrderbyOrderIndexAsc(
                  Mockito.eq(bezirkUndWahlId.getWahlbezirkID()),
                  Mockito.eq(bezirkUndWahlId.getWahlID())))
          .thenReturn(Optional.empty());

      val result = unitUnderTest.getBedenklicheStimmzettelOrderedByOrderIndexAsc(bezirkUndWahlId);

      Assertions.assertThat(result.isEmpty()).isTrue();
    }
  }

  @Nested
  class SetBedenklicheStimmzettel {

    @Test
    void should_saveEntities_when_modelsAreGiven() {
      val bedenklicherStimmzettel =
          new BedenklicherStimmzettelModel(12, Collections.emptySet(), ValidityModel.INVALID);
      val modelToSave = List.of(bedenklicherStimmzettel, bedenklicherStimmzettel);

      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      val mockedMappedModel = new BedenklicheStimmzettel();
      Mockito.when(
              modelMapper.toEntity(
                  Mockito.eq(modelToSave), Mockito.eq(wahlbezirkID), Mockito.eq(wahlID)))
          .thenReturn(mockedMappedModel);

      Assertions.assertThatNoException()
          .isThrownBy(
              () ->
                  unitUnderTest.setBedenklicheStimmzettel(
                      new BezirkUndWahlID(wahlID, wahlbezirkID), modelToSave));
      Mockito.verify(repository).save(mockedMappedModel);
    }
  }
}
