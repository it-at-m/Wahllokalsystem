package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.nachlieferungsbezirke;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahltagIdUndWahlbezirkId;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    classes = {MicroServiceApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE})
@Slf4j
public class NachlieferungsbezirkeRepositoryTest {

  @Autowired private NachlieferungsbezirkeRepository repository;

  @AfterEach
  void teardown() {
    repository.deleteAll();
  }

  @Test
  void should_findNachlieferungsbezirke_when_entriesExistForWahltagID() {
    val wahltagID = "wahltagID";
    val nachlieferungsbezirk1 =
        new Nachlieferungsbezirk(new WahltagIdUndWahlbezirkId(wahltagID, "wahlbezirkID1"));
    val nachlieferungsbezirk2 =
        new Nachlieferungsbezirk(new WahltagIdUndWahlbezirkId(wahltagID, "wahlbezirkID2"));
    repository.saveAll(List.of(nachlieferungsbezirk1, nachlieferungsbezirk2));

    val result = repository.findByWahltagIdUndWahlbezirkId_WahltagID(wahltagID);

    Assertions.assertThat(result.size()).isEqualTo(2);
    result.forEach(
        nachlieferungsbezirk -> {
          Assertions.assertThat(nachlieferungsbezirk.getWahltagIdUndWahlbezirkId().getWahltagID())
              .isEqualTo(wahltagID);
        });
  }

  @Test
  void should_findNoNachlieferungsbezirke_when_noEntriesExistForWahltagID() {
    val wahltagID = "wahltagID";

    val result = repository.findByWahltagIdUndWahlbezirkId_WahltagID(wahltagID);

    Assertions.assertThat(result.size()).isEqualTo(0);
  }
}
