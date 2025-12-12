package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.common.WahltagWithNummerModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import java.util.List;

public interface WahlenClient {

  /**
   * @param wahltagWithNummerModel reference to a specific event on a date of election
   * @return List<WahltagModel>
   * @throws WlsException {@link
   *     de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException} if return would
   *     be null {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException}
   *     if there were trouble during communication
   */
  List<WahlModel> getWahlen(final WahltagWithNummerModel wahltagWithNummerModel)
      throws WlsException;
}
