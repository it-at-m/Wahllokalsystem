package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest(classes = MicroServiceApplication.class)
public class EreignisseRepositoryTest {

  @Autowired EreignisseRepository unitUnderTest;

  @AfterEach
  void teardown() {
    unitUnderTest.deleteAll();
  }

  @Nested
  class FindByWahlbezirkID {

    @Test
    @WithMockUser(
        authorities = {
          Authorities.REPOSITORY_DELETE_EREIGNISSE,
          Authorities.REPOSITORY_WRITE_EREIGNISSE,
          Authorities.REPOSITORY_READ_EREIGNISSE
        })
    void should_findEreignisse_when_givenWahlbezirkID() {
      val wahlbezirkID1 = "wahlbezirkID1";
      val wahlbezirkID2 = "wahlbezirkID2";
      val ereignisList1 =
          Set.of(
              TestdataFactory.CreateEreignisEntity.withData("beschreibung1"),
              TestdataFactory.CreateEreignisEntity.withData("beschreibung2"));
      val ereignisList2 = Set.of(TestdataFactory.CreateEreignisEntity.withData("beschreibung3"));
      val ereignisse1 =
          TestdataFactory.CreateEreignisseEntity.withData(wahlbezirkID1, ereignisList1);
      val ereignisse2 =
          TestdataFactory.CreateEreignisseEntity.withData(wahlbezirkID2, ereignisList2);
      unitUnderTest.save(ereignisse1);
      unitUnderTest.save(ereignisse2);

      val result = unitUnderTest.findByWahlbezirkID(wahlbezirkID1);
      Assertions.assertThat(result.get().getEreignisse().size()).isEqualTo(2);
      Assertions.assertThat(result.get().getEreignisse())
          .noneMatch(ereignis -> ereignis.getBeschreibung().equals("beschreibung3"));
    }
  }

  @Nested
  class DeleteByWahlbezirkID {

    @Test
    @WithMockUser(
        authorities = {
          Authorities.REPOSITORY_DELETE_EREIGNISSE,
          Authorities.REPOSITORY_WRITE_EREIGNISSE,
          Authorities.REPOSITORY_READ_EREIGNISSE
        })
    void should_deleteEreignisseWithMatchingWahlbezirkID_when_givenWahlbezirkID() {
      val wahlbezirkID = "wahlbezirkID";

      val ereignisList =
          Set.of(
              TestdataFactory.CreateEreignisEntity.withData("beschreibung1"),
              TestdataFactory.CreateEreignisEntity.withData("beschreibung2"));
      val ereignisse = TestdataFactory.CreateEreignisseEntity.withData(wahlbezirkID, ereignisList);
      unitUnderTest.save(ereignisse);

      val savedEreignisse = unitUnderTest.findByWahlbezirkID(wahlbezirkID);
      unitUnderTest.deleteByWahlbezirkID(wahlbezirkID);

      val result = unitUnderTest.findByWahlbezirkID(wahlbezirkID);
      Assertions.assertThat(savedEreignisse.get().getEreignisse().size())
          .isEqualTo(ereignisList.size());
      Assertions.assertThat(result).isEmpty();
    }
  }
}
