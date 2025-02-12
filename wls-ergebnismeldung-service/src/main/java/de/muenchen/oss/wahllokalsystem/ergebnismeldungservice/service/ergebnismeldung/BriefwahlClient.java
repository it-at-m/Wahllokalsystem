package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

public interface BriefwahlClient {

    /**
     * @param wahlbezirkID filter for beanstandete Wahlbriefe
     * @param wahlID filter for beanstandete Wahlbriefe
     * @param waehlerverzeichnisNummer filter for beanstandete Wahlbriefe
     * @return count of beanstandete wahlbriefe with
     *         {@link de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.briefwahl.model.Zurueckweisungsgrund}
     *         not
     *         {@link de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.briefwahl.model.Zurueckweisungsgrund#ZUGELASSEN}.
     *         Is 0 when no wahl with wahlID
     *         exists
     * @throws de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException when problem
     *             on retrieving the information occurred
     * @throws de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException when no
     *             beanstandete wahlbriefe for filter wahlbezirkID and
     *             waehlerverzeichnisnummer exists
     */
    long getAnzahlZurueckgewiesenerWahlbriefe(String wahlbezirkID, String wahlID, long waehlerverzeichnisNummer);
}
