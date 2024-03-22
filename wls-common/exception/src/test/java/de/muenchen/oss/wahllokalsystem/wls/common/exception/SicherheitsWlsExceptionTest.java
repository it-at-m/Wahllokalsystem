package de.muenchen.oss.wahllokalsystem.wls.common.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionCategory;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class SicherheitsWlsExceptionTest {

    @Test
    void verifyDataIsSetCorrect() {
        val code = "089";
        val message = "very useful message";
        val serviceName = "testService";
        val causingException = new NullPointerException("something was null");

        val sicherheitsWlsException = SicherheitsWlsException.withCode(code).inService(serviceName).withCause(causingException).buildWithMessage(message);

        Assertions.assertThat(sicherheitsWlsException.getCategory()).isSameAs(WlsExceptionCategory.SECURITY);
        Assertions.assertThat(sicherheitsWlsException.getCode()).isSameAs(code);
        Assertions.assertThat(sicherheitsWlsException.getMessage()).isSameAs(message);
        Assertions.assertThat(sicherheitsWlsException.getServiceName()).isSameAs(serviceName);
        Assertions.assertThat(sicherheitsWlsException.getCause()).isSameAs(causingException);
    }
}
