package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignis;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.EreignisRepository;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest(classes = MicroServiceApplication.class)
public class EreignisRepositoryTest {

    @Autowired
    EreignisRepository unitUnderTest;

    @AfterEach
    void tearDown() {
        unitUnderTest.deleteAll();
    }

    @Nested
    class GetEreignisse {

        @Test
        @WithMockUser(
                authorities = { Authorities.REPOSITORY_DELETE_EREIGNISSE, Authorities.REPOSITORY_WRITE_EREIGNISSE, Authorities.REPOSITORY_READ_EREIGNISSE }
        )
        void findByWahlbezirkID() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlbezirkID2 = "newWahlbezirkID";
            List<Ereignis> ereignisList1 = new ArrayList<>();
            ereignisList1.add(TestdataFactory.createEreignisEntityWithData(wahlbezirkID, "beschreibung", LocalDateTime.now().withNano(0), Ereignisart.VORFALL));
            ereignisList1
                    .add(TestdataFactory.createEreignisEntityWithData(wahlbezirkID, "beschreibung", LocalDateTime.now().withNano(0), Ereignisart.VORKOMMNIS));
            List<Ereignis> ereignisList2 = new ArrayList<>();
            ereignisList2
                    .add(TestdataFactory.createEreignisEntityWithData(wahlbezirkID2, "beschreibung", LocalDateTime.now().withNano(0), Ereignisart.VORFALL));
            unitUnderTest.saveAll(ereignisList1);
            unitUnderTest.saveAll(ereignisList2);

            val result = unitUnderTest.findByWahlbezirkID(wahlbezirkID);

            Assertions.assertThat(result.size()).isEqualTo(2);
            Assertions.assertThat(result).filteredOn(ereignis -> ereignis.getWahlbezirkID().equals(wahlbezirkID));
            Assertions.assertThat(result).noneMatch(ereignis -> ereignis.getWahlbezirkID().equals(wahlbezirkID2));
        }
    }

    @Nested
    class DeleteEreignisse {

        @Test
        @WithMockUser(
                authorities = { Authorities.REPOSITORY_DELETE_EREIGNISSE, Authorities.REPOSITORY_WRITE_EREIGNISSE, Authorities.REPOSITORY_READ_EREIGNISSE }
        )
        void deleteByWahlbezirkID() {
            val wahlbezirkID = "wahlbezirkID";
            List<Ereignis> ereignisList = new ArrayList<>();
            ereignisList.add(TestdataFactory.createEreignisEntityWithData(wahlbezirkID, "beschreibung", LocalDateTime.now().withNano(0), Ereignisart.VORFALL));
            ereignisList
                    .add(TestdataFactory.createEreignisEntityWithData(wahlbezirkID, "beschreibung", LocalDateTime.now().withNano(0), Ereignisart.VORKOMMNIS));
            unitUnderTest.saveAll(ereignisList);

            val savedEreignisse = unitUnderTest.findByWahlbezirkID(wahlbezirkID);
            Assertions.assertThat(savedEreignisse.size()).isEqualTo(ereignisList.size());

            unitUnderTest.deleteByWahlbezirkID(wahlbezirkID);
            val deletedEreignisse = unitUnderTest.findByWahlbezirkID(wahlbezirkID);
            Assertions.assertThat(deletedEreignisse.size()).isZero();
        }
    }
}
