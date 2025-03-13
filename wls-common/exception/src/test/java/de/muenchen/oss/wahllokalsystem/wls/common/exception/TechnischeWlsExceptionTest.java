package de.muenchen.oss.wahllokalsystem.wls.common.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionCategory;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TechnischeWlsExceptionTest {

    @Nested
    class WithCode {

        @Test
        void should_returnTechnischeWlsExceptionWithAllPropertiesSet_when_buildWithAllStepsOfBuilder() {
            val code = "089";
            val message = "very useful message";
            val serviceName = "testService";
            val causingException = new NullPointerException("something was null");

            val technischeWlsException = TechnischeWlsException.withCode(code).inService(serviceName).withCause(causingException)
                    .buildWithMessage(message);

            Assertions.assertThat(technischeWlsException.getCategory()).isSameAs(WlsExceptionCategory.TECHNISCH);
            Assertions.assertThat(technischeWlsException.getCode()).isSameAs(code);
            Assertions.assertThat(technischeWlsException.getMessage()).isSameAs(message);
            Assertions.assertThat(technischeWlsException.getServiceName()).isSameAs(serviceName);
            Assertions.assertThat(technischeWlsException.getCause()).isSameAs(causingException);
        }
    }
}
