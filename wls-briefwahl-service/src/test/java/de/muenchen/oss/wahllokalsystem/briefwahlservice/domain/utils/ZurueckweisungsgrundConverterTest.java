package de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.utils;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.common.beanstandetewahlbriefe.Zurueckweisungsgrund;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ZurueckweisungsgrundConverterTest {

    private final ZurueckweisungsgrundConverter unitUnderTest = new ZurueckweisungsgrundConverter();

    @Nested
    class ConvertToDatabaseColumn {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.convertToDatabaseColumn(null)).isNull();
        }

        @Test
        void should_returnEmptyArray_when_givenEmptyArray() {
            Assertions.assertThat(unitUnderTest.convertToDatabaseColumn(new Zurueckweisungsgrund[0])).isEqualTo(StringUtils.EMPTY);
        }

        @Test
        void should_convertToString_when_givenArray() {
            val arrayData = new Zurueckweisungsgrund[] { Zurueckweisungsgrund.ZUGELASSEN, Zurueckweisungsgrund.GEGENSTAND_IM_UMSCHLAG,
                    Zurueckweisungsgrund.NICHT_WAHLBERECHTIGT };

            val expectedResult = "ZUGELASSEN,GEGENSTAND_IM_UMSCHLAG,NICHT_WAHLBERECHTIGT";

            val result = unitUnderTest.convertToDatabaseColumn(arrayData);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ConvertToEntityAttribute {

        @Test
        void should_convertToEmptyString_when_givenEmptyArray() {
            Assertions.assertThat(unitUnderTest.convertToEntityAttribute("")).isEqualTo(new Zurueckweisungsgrund[0]);
        }

        @Test
        void should_convertCorrectly_when_givenValidArguments() {
            val argument = "ZUGELASSEN,GEGENSTAND_IM_UMSCHLAG,NICHT_WAHLBERECHTIGT";

            val expectedResult = new Zurueckweisungsgrund[] { Zurueckweisungsgrund.ZUGELASSEN, Zurueckweisungsgrund.GEGENSTAND_IM_UMSCHLAG,
                    Zurueckweisungsgrund.NICHT_WAHLBERECHTIGT };

            val result = unitUnderTest.convertToEntityAttribute(argument);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }
}
