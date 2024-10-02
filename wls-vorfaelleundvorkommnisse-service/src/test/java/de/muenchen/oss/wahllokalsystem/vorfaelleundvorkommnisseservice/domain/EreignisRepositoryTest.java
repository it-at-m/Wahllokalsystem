package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.EreignisRepository;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils.TestdataFactory;
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
    class FindByWahlbezirkID {

        @Test
        @WithMockUser(
                authorities = { Authorities.REPOSITORY_DELETE_EREIGNISSE, Authorities.REPOSITORY_WRITE_EREIGNISSE, Authorities.REPOSITORY_READ_EREIGNISSE }
        )
        void should_returnListOfEreignisEntities_when_givenValidWahlbezirkid() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlbezirkID2 = "newWahlbezirkID";

            val mockedEreignisList1 = List.of(
                    TestdataFactory.CreateEreignisEntity.withData(wahlbezirkID),
                    TestdataFactory.CreateEreignisEntity.withData(wahlbezirkID));
            val mockedEreignisList2 = List.of(TestdataFactory.CreateEreignisEntity.withData(wahlbezirkID2));
            unitUnderTest.saveAll(mockedEreignisList1);
            unitUnderTest.saveAll(mockedEreignisList2);

            val result = unitUnderTest.findByWahlbezirkID(wahlbezirkID);
            Assertions.assertThat(result.size()).isEqualTo(2);
            Assertions.assertThat(result).noneMatch(ereignis -> ereignis.getWahlbezirkID().equals(wahlbezirkID2));
        }
    }

    @Nested
    class DeleteByWahlbezirkID {

        @Test
        @WithMockUser(
                authorities = { Authorities.REPOSITORY_DELETE_EREIGNISSE, Authorities.REPOSITORY_WRITE_EREIGNISSE, Authorities.REPOSITORY_READ_EREIGNISSE }
        )
        void should_deleteAllEreignisEntitiesWithMatching_Wahlbezirkid_when_givenValidWahlbezirkid() {
            val wahlbezirkID = "wahlbezirkID";

            val mockedEreignisList = List.of(
                    TestdataFactory.CreateEreignisEntity.withData(wahlbezirkID),
                    TestdataFactory.CreateEreignisEntity.withData(wahlbezirkID));
            unitUnderTest.saveAll(mockedEreignisList);

            val savedEreignisse = unitUnderTest.findByWahlbezirkID(wahlbezirkID);
            unitUnderTest.deleteByWahlbezirkID(wahlbezirkID);

            val result = unitUnderTest.findByWahlbezirkID(wahlbezirkID);
            Assertions.assertThat(savedEreignisse.size()).isEqualTo(mockedEreignisList.size());
            Assertions.assertThat(result).isEmpty();
        }
    }
}
