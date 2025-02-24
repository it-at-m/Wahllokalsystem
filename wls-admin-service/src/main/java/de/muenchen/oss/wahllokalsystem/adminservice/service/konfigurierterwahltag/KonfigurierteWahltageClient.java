package de.muenchen.oss.wahllokalsystem.adminservice.service.konfigurierterwahltag;

import de.muenchen.oss.wahllokalsystem.adminservice.service.common.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import java.util.List;

public interface KonfigurierteWahltageClient {

    List<KonfigurierterWahltagModel> getKonfigurierteWahltage() throws WlsException;

}
