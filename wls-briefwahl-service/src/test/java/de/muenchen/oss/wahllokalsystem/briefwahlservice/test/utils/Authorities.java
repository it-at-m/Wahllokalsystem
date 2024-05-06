package de.muenchen.oss.wahllokalsystem.briefwahlservice.test.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Authorities {
    public static final String SERVICE_GET_BEANSTANDETE_WAHLBRIEFE = "Briefwahl_BUSINESSACTION_GetBeanstandeteWahlbriefe";
    public static final String SERVICE_ADD_BEANSTANDETE_WAHLBRIEFE = "Briefwahl_BUSINESSACTION_PostBeanstandeteWahlbriefe";

    public static final String REPOSITORY_READ_BEANSTANDETE_WAHLBRIEFE = "Briefwahl_READ_BeanstandeteWahlbriefe";
    public static final String REPOSITORY_WRITE_BEANSTANDETE_WAHLBRIEFE = "Briefwahl_WRITE_BeanstandeteWahlbriefe";
    public static final String REPOSITORY_DELETE_BEANSTANDETE_WAHLBRIEFE = "Briefwahl_DELETE_BeanstandeteWahlbriefe";

    public static final String[] ALL_AUTHORITIES_GET_BEANSTANDETE_WAHLBRIEFE = {
            SERVICE_GET_BEANSTANDETE_WAHLBRIEFE,

            REPOSITORY_READ_BEANSTANDETE_WAHLBRIEFE
    };

    public static final String[] ALL_AUTHORITIES_ADD_BEANSTANDETE_WAHLBRIEFE = {
            SERVICE_ADD_BEANSTANDETE_WAHLBRIEFE,

            REPOSITORY_WRITE_BEANSTANDETE_WAHLBRIEFE
    };
}
