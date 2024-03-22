package de.muenchen.oss.wahllokalsystem.wls.common.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionCategory;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class FachlicheWlsExceptionTest {

    @Test
    void verifyDataIsSetCorrect() {
        val code = "089";
        val message = "very useful message";
        val serviceName = "testService";
        val causingException = new NullPointerException("something was null");
        val fachlicheWlsException = FachlicheWlsException.withCode(code).inService(serviceName).withCause(causingException).buildWithMessage(message);

        Assertions.assertThat(fachlicheWlsException.getCategory()).isSameAs(WlsExceptionCategory.FACHLICH);
        Assertions.assertThat(fachlicheWlsException.getCode()).isSameAs(code);
        Assertions.assertThat(fachlicheWlsException.getMessage()).isSameAs(message);
        Assertions.assertThat(fachlicheWlsException.getServiceName()).isSameAs(serviceName);
        Assertions.assertThat(fachlicheWlsException.getCause()).isSameAs(causingException);
    }
}
