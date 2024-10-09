package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.clients.basisdaten.Wahl;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;

import java.util.List;

public interface BasisdatenClient {

    List<Wahl> getWahlen(String wahltagID) throws WlsException;
}
