package de.muenchen.oss.wahllokalsystem.briefwahlservice.rest.beanstandetewahlbriefe;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe.BeanstandeteWahlbriefeModel;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe.BeanstandeteWahlbriefeReference;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe.BeanstandeteWahlbriefeService;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class BeanstandeteWahlbriefeControllerTest {

    @Mock
    BeanstandeteWahlbriefeService beanstandeteWahlbriefeService;

    @Mock
    BeanstandeteWahlbriefeDTOMapper beanstandeteWahlbriefeDTOMapper;

    @InjectMocks
    BeanstandeteWahlbriefeController controller;

    @Test
    void getBeanstandeteWahlbriefeDTO() {
        val wahlbezirkID = "wahlbezirkId";
        val waehlerverzeichnisNummer = 5L;

        val modelReference = BeanstandeteWahlbriefeReference.builder().build();
        val serviceResponse = BeanstandeteWahlbriefeModel.builder().build();
        val mappedServiceResponse = BeanstandeteWahlbriefeDTO.builder().build();

        Mockito.when(beanstandeteWahlbriefeDTOMapper.toReferenceModel(wahlbezirkID, waehlerverzeichnisNummer)).thenReturn(modelReference);
        Mockito.when(beanstandeteWahlbriefeService.getBeanstandeteWahlbriefe(modelReference)).thenReturn(serviceResponse);
        Mockito.when(beanstandeteWahlbriefeDTOMapper.toDTO(serviceResponse)).thenReturn(mappedServiceResponse);

        val result = controller.getBeanstandeteWahlbriefe(wahlbezirkID, waehlerverzeichnisNummer);

        Assertions.assertThat(result.getBody()).isEqualTo(mappedServiceResponse);
        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void addBeanstandeteWahlbriefe() {
        val wahlbezirkID = "wahlbezirkId";
        val waehlerverzeichnisNummer = 5L;
        val requestBody = BeanstandeteWahlbriefeCreateDTO.builder().build();

        val mappedModel = BeanstandeteWahlbriefeModel.builder().build();

        Mockito.when(beanstandeteWahlbriefeDTOMapper.toCreateModel(requestBody, wahlbezirkID, waehlerverzeichnisNummer)).thenReturn(mappedModel);

        controller.setBeanstandeteWahlbriefe(wahlbezirkID, waehlerverzeichnisNummer, requestBody);

        Mockito.verify(beanstandeteWahlbriefeService).setBeanstandeteWahlbriefe(mappedModel);
    }

}
