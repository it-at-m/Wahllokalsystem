package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten;

import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import java.time.LocalDate;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MicroServiceApplication.class)
class WahltageRepositoryTest {

    @Autowired
    WahltageRepository wahltageRepository;

    @AfterEach
    void tearDown() {
        wahltageRepository.deleteAll();
    }

    @Nested
    class FindByTagAfterOrTagEquals {

        @Test
        void dataFound() {
            val equalsDateString = "2024-07-03";
            val afterDateString = "2024-08-02";

            val wahltag1ToFind = wahltageRepository.save(new Wahltag(LocalDate.parse("2024-07-03"), "beschreibung", "1"));
            val wahltag2ToFind = wahltageRepository.save(new Wahltag(LocalDate.parse("2024-08-03"), "beschreibung", "1"));
            wahltageRepository.save(new Wahltag(LocalDate.parse("2024-08-02"), "beschreibung", "3"));
            wahltageRepository.save(new Wahltag(LocalDate.parse("2024-07-02"), "beschreibung", "4"));
            wahltageRepository.save(new Wahltag(LocalDate.parse("2024-07-04"), "beschreibung", "5"));

            val result = wahltageRepository.findByTagAfterOrTagEquals(LocalDate.parse(afterDateString), LocalDate.parse(equalsDateString));

            val expectedResult = new Wahltag[] { wahltag1ToFind, wahltag2ToFind };

            Assertions.assertThat(result).containsOnly(expectedResult);
        }
    }

}
