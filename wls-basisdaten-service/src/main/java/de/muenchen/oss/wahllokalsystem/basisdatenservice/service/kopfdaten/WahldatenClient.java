package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.common.WahltagWithNummerModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;

public interface WahldatenClient {

  /**
   * @param wahltagWithNummerModel filter for requested Basisdaten
   * @return BasisdatenDTO
   * @throws WlsException {@link
   *     de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException} if return would
   *     be null {@link de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException}
   *     if there were trouble during communication
   */
  BasisdatenModel loadBasisdaten(WahltagWithNummerModel wahltagWithNummerModel) throws WlsException;
}
