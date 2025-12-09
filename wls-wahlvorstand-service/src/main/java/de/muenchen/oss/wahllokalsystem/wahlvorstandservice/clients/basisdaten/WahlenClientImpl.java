package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.clients.basisdaten;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.basisdaten.client.WahlenControllerApi;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.basisdaten.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlenClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile(Profiles.NOT + Profiles.DUMMY_CLIENTS)
public class WahlenClientImpl implements WahlenClient {

  private final ExceptionFactory exceptionFactory;
  private final WahlenControllerApi wahlenControllerApi;
  private final WahlenClientMapper wahlenClientMapper;

  @Override
  public List<WahlModel> getWahlen(final KonfigurierterWahltagModel wahltag) throws WlsException {
    final List<WahlDTO> wahlDTOs;
    try {
      wahlDTOs = wahlenControllerApi.getWahlen(wahltag.wahltagID());
    } catch (final Exception exception) {
      log.info("exception on loadwahl from external", exception);
      throw exceptionFactory.createTechnischeWlsException(
          ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN);
    }
    if (wahlDTOs == null) {
      throw exceptionFactory.createFachlicheWlsException(
          ExceptionConstants.BASISDATEN_ANTWORT_NULL);
    }
    return wahlenClientMapper.fromRemoteClientListOfWahlDTOtoListOfWahlModel(wahlDTOs);
  }
}
