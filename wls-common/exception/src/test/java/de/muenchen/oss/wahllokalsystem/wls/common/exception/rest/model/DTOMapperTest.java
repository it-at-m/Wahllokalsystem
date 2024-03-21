package de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

class DTOMapperTest {

    private final DTOMapper unitUnderTest = Mappers.getMapper(DTOMapper.class);

    @Nested
    class ToDTO {

        @Test
        void nullInNullOut() {
            Assertions.assertThat(unitUnderTest.toDTO(null)).isNull();
        }

        @Test
        void toDTO() {
            val code = "089";
            val serviceName = "dto mapper test";
            val message = "lets check the mapping";
            val source = FachlicheWlsException.withCode(code).inService(serviceName).buildWithMessage(message);
            val expectedResult = new WlsExceptionDTO(WlsExceptionCategory.F, code, serviceName, message);

            val result = unitUnderTest.toDTO(source);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

    }

    @ParameterizedTest(name = "expected: {0} input: {1}")
    @MethodSource
    void toDTOCategory(final WlsExceptionCategory expectedResult,
            final de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionCategory categoryToMap) {
        Assertions.assertThat(unitUnderTest.toDTOCategory(categoryToMap)).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> toDTOCategory() {
        return Stream.of(
                Arguments.of(WlsExceptionCategory.F, de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionCategory.FACHLICH),
                Arguments.of(WlsExceptionCategory.I, de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionCategory.INFRASTRUKTUR),
                Arguments.of(WlsExceptionCategory.S, de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionCategory.SECURITY),
                Arguments.of(WlsExceptionCategory.T, de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionCategory.TECHNISCH),
                Arguments.of(null, null));
    }
}
