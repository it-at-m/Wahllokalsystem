package de.muenchen.oss.wahllokalsystem.infomanagementservice.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithSecurityContext;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithMockUserAsJwtSecurityContextFactory.class)
public @interface WithMockUserAsJwt {

    String value() default "user";

    String[] authorities() default {};

    String[] claimProperties() default {};

    String claimPropertiesSeparator() default "=";

    int issuedBeforeHours() default 1;

    int expiredInHours() default 1;

    @AliasFor(annotation = WithSecurityContext.class)
    TestExecutionEvent setupBefore() default TestExecutionEvent.TEST_METHOD;

}
