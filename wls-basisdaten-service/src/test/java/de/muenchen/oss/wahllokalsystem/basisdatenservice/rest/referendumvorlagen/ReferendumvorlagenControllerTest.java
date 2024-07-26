package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.referendumvorlagen;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlagenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlagenReferenceModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlagenService;
import java.util.Collections;
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
class ReferendumvorlagenControllerTest {

    @Mock
    ReferendumvorlagenService referendumvorlagenService;

    @Mock
    ReferendumvorlagenDTOMapper referendumvorlagenDTOMapper;

    @InjectMocks
    ReferendumvorlagenController unitUnderTest;

    @Nested
    class GetReferendumvorlagen {

        @Test
        void serviceIsCalled() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val mockedReferenceModel = new ReferendumvorlagenReferenceModel(wahlID, wahlbezirkID);
            val mockedServiceResponse = new ReferendumvorlagenModel("szgID", Collections.emptySet());
            val mockedServiceResponseAsDTO = new ReferendumvorlagenDTO("szgID", Collections.emptySet());

            Mockito.when(referendumvorlagenDTOMapper.toModel(eq(wahlbezirkID), eq(wahlID))).thenReturn(mockedReferenceModel);
            Mockito.when(referendumvorlagenDTOMapper.toDTO(mockedServiceResponse)).thenReturn(mockedServiceResponseAsDTO);
            Mockito.when(referendumvorlagenService.getReferendumvorlagen(mockedReferenceModel)).thenReturn(mockedServiceResponse);

            val result = unitUnderTest.getReferendumvorlagen(wahlID, wahlbezirkID);

            Assertions.assertThat(result).isSameAs(mockedServiceResponseAsDTO);
        }
    }

}
