package de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten;

import java.util.List;

public interface WahlbezirkeClient {

    List<WahlbezirkModel> getWahlbezirke(String wahltagID);
}
