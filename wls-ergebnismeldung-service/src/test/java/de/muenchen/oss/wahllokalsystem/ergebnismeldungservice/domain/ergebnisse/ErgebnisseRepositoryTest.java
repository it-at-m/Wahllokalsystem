package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles(profiles = TestConstants.SPRING_TEST_PROFILE)
public class ErgebnisseRepositoryTest {

    @Autowired
    private ErgebnisseRepository ergebnisseRepository;

    @Test
    public void should_returnErgebnisseList_when_getAllErgebnisseIsCalled() {
        val bezirkUndWahlIDStapelart = new BezirkUndWahlIDStapelart("wahlbezirkID", "wahlID", Stapelart.LTW_BZW_A);
        val ergebnis1 = new Ergebnis(null, null, null, 1, null);
        val ergebnisList = List.of(ergebnis1);

        val ergebnisseToSaveInRepo = new Ergebnisse(bezirkUndWahlIDStapelart, ergebnisList);

        ergebnisseRepository.save(ergebnisseToSaveInRepo);

        List<Ergebnisse> result = ergebnisseRepository.findByWahlbezirkIDAndWahlD("wahlbezirkID", "wahlID");

        Assertions.assertThat(result.get(0)).usingRecursiveComparison().isEqualTo(ergebnisseToSaveInRepo);
    }
}
