package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.BedenklicheStimmzettelErfassung;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.BedenklicheStimmzettelRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
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

  @Mock BedenklicheStimmzettelValidator validator;

  @Mock ExceptionFactory exceptionFactory;

  @InjectMocks MBWBedenklicheStimmzettelService unitUnderTest;

  @Nested
  class GetBedenklicheStimmzettelOrderedByOrderIndexAsc {

    @Test
    void should_returnMappedModel_when_entitiesWereFound() {
      val bezirkUndWahlId = new BezirkUndWahlID("wahlID", "wahlbezirkID");

      val mockedEntity = new BedenklicheStimmzettelErfassung();

      Mockito.when(
              repository.findByBezirkUndWahlIDOrderbyOrderIndexAsc(
                  Mockito.eq(bezirkUndWahlId.getWahlbezirkID()),
                  Mockito.eq(bezirkUndWahlId.getWahlID())))
          .thenReturn(Optional.of(mockedEntity));

      val mockedMappedEntity =
          new BedenklicherStimmzettelModel(0, Collections.emptySet(), ValidityModel.INVALID);
      Mockito.when(modelMapper.toModel(mockedEntity.getBedenklicheStimmzettel()))
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

    @Test
    void should_throwExceptionOfValidator_when_validationFailed() {
      val bezirkUndWahlId = new BezirkUndWahlID("wahlID", "wahlbezirkID");

      val mockedValidationException =
          FachlicheWlsException.withCode("000")
              .inService("service")
              .buildWithMessage("mocked failure");
      Mockito.doThrow(mockedValidationException)
          .when(validator)
          .validateGetBedenklicheStimmzettelParameterOrThrow(Mockito.eq(bezirkUndWahlId));

      Assertions.assertThatException()
          .isThrownBy(
              () -> unitUnderTest.getBedenklicheStimmzettelOrderedByOrderIndexAsc(bezirkUndWahlId))
          .isEqualTo(mockedValidationException);
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

      val mockedMappedModel = new BedenklicheStimmzettelErfassung();
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

    @Test
    void should_throwExceptionOfValidator_when_validationFails() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      val bedenklicherStimmzettel =
          new BedenklicherStimmzettelModel(12, Collections.emptySet(), ValidityModel.INVALID);
      val modelToSave = List.of(bedenklicherStimmzettel, bedenklicherStimmzettel);

      val mockedValidationException =
          FachlicheWlsException.withCode("000")
              .inService("service")
              .buildWithMessage("mocked failure");
      Mockito.doThrow(mockedValidationException)
          .when(validator)
          .validateSetBedenklicheStimmzettelParameterOrThrow(
              Mockito.eq(new BezirkUndWahlID(wahlID, wahlbezirkID)), Mockito.eq(modelToSave));

      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  unitUnderTest.setBedenklicheStimmzettel(
                      new BezirkUndWahlID(wahlID, wahlbezirkID), modelToSave))
          .isEqualTo(mockedValidationException);
    }

    @Test
    void should_throwTechnischeWlsException_when_savingFailed() {
      val bedenklicherStimmzettel =
          new BedenklicherStimmzettelModel(12, Collections.emptySet(), ValidityModel.INVALID);
      val modelToSave = List.of(bedenklicherStimmzettel, bedenklicherStimmzettel);

      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      val mockedMappedModel = new BedenklicheStimmzettelErfassung();
      Mockito.when(
              modelMapper.toEntity(
                  Mockito.eq(modelToSave), Mockito.eq(wahlbezirkID), Mockito.eq(wahlID)))
          .thenReturn(mockedMappedModel);

      val mockedRepoException = new RuntimeException("mocked saving exception");
      Mockito.doThrow(mockedRepoException).when(repository).save(mockedMappedModel);

      val mockedTechnischeWlsException =
          TechnischeWlsException.withCode("000").buildWithMessage("mocked failure");
      Mockito.when(
              exceptionFactory.createTechnischeWlsException(
                  ExceptionConstants.POST_BEDENKLICHE_STIMMZETTEL_SAVING_FAILED))
          .thenReturn(mockedTechnischeWlsException);

      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  unitUnderTest.setBedenklicheStimmzettel(
                      new BezirkUndWahlID(wahlID, wahlbezirkID), modelToSave))
          .isSameAs(mockedTechnischeWlsException);
    }
  }
}
