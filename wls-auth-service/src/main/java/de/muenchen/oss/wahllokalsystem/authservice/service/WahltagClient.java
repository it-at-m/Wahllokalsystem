package de.muenchen.oss.wahllokalsystem.authservice.service;

public interface WahltagClient {

    /**
     * Check if the matching wahltag is currently active
     *
     * @param wahltagID ID of wahltag to check its active state
     * @return true when wahltag is active; false if it is not active or wahltag with given ID does not exists
     */
    boolean isWahltagActive(String wahltagID);
}
