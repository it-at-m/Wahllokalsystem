package de.muenchen.oss.wahllokalsystem.adminservice.service;

import java.util.List;

public interface WahlbezirkeClient {

    List<WahlbezirkModel> getWahlbezirke(String wahltagID);
}
