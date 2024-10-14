package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltermindaten;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltermindaten.AsyncProgress;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
class AsyncProgressControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AsyncProgress asyncProgress;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    class GetAsyncProgress {

        @Test
        void should_returnCurrentAsyncProgress_when_requested() throws Exception {
            val expectedAsyncProgressDTO = setupAsyncProgressStateAndReturnExpectedDTO();

            val request = MockMvcRequestBuilders.get("/businessActions/asyncProgress");
            val mockMvcPerform = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

            val responseBodyAsAsycProgressDTO = objectMapper.readValue(mockMvcPerform.getResponse().getContentAsString(), AsyncProgressDTO.class);
            Assertions.assertThat(responseBodyAsAsycProgressDTO).isEqualTo(expectedAsyncProgressDTO);
        }

        private AsyncProgressDTO setupAsyncProgressStateAndReturnExpectedDTO() {
            val wahltag = LocalDate.now();
            val wahlnummer = "wahlnummer";
            val lastStartTime = LocalDateTime.now().minusDays(2);
            val lastFinishTime = LocalDateTime.now().minusDays(1);
            val wahlvorschlaegeIsLoading = true;
            val wahlvorschlaegeTotal = 23;
            val wahlvorschlaegeFinished = 12;
            val wahlvorschlaegeNext = "wahlvorschlagID13";
            val referendumvorlagenIsLoading = false;
            val referendumvorlagenTotal = 21;
            val referendumvorlagenFinished = 20;
            val referendumvorlagenNext = "referendumvorlagenID13";

            asyncProgress.setForWahltag(wahltag);
            asyncProgress.setWahlNummer(wahlnummer);
            asyncProgress.setLastStartTime(lastStartTime);
            asyncProgress.setLastFinishTime(lastFinishTime);
            asyncProgress.setWahlvorschlaegeLoadingActive(wahlvorschlaegeIsLoading);
            asyncProgress.setWahlvorschlaegeTotal(wahlvorschlaegeTotal);
            asyncProgress.setWahlvorschlageFinished(wahlvorschlaegeFinished);
            asyncProgress.setWahlvorschlaegeNext(wahlvorschlaegeNext);
            asyncProgress.setReferendumLoadingActive(referendumvorlagenIsLoading);
            asyncProgress.setReferendumVorlagenTotal(referendumvorlagenTotal);
            asyncProgress.setReferendumVorlagenFinished(referendumvorlagenFinished);
            asyncProgress.setReferendumVorlagenNext(referendumvorlagenNext);

            return new AsyncProgressDTO(wahltag, wahlnummer, lastStartTime, lastFinishTime, wahlvorschlaegeIsLoading, wahlvorschlaegeTotal,
                    wahlvorschlaegeFinished, wahlvorschlaegeNext, referendumvorlagenIsLoading, referendumvorlagenTotal, referendumvorlagenFinished,
                    referendumvorlagenNext);
        }
    }

}
