package de.muenchen.oss.wahllokalsystem.wls.common.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionCategory;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class InfrastrukturelleWlsExceptionTest {

    @Test
    void verifyDataIsSetCorrect() {
        val code = "089";
        val message = "very useful message";
        val serviceName = "testService";
        val causingException = new NullPointerException("something was null");

        val infrastrukturelleWlsException = InfrastrukturelleWlsException.withCode(code).inService(serviceName).withCause(causingException)
                .buildWithMessage(message);

        Assertions.assertThat(infrastrukturelleWlsException.getCategory()).isSameAs(WlsExceptionCategory.INFRASTRUKTUR);
        Assertions.assertThat(infrastrukturelleWlsException.getCode()).isSameAs(code);
        Assertions.assertThat(infrastrukturelleWlsException.getMessage()).isSameAs(message);
        Assertions.assertThat(infrastrukturelleWlsException.getServiceName()).isSameAs(serviceName);
        Assertions.assertThat(infrastrukturelleWlsException.getCause()).isSameAs(causingException);
    }

}
