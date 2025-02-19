package de.muenchen.oss.wahllokalsystem.wls.common.testing.archunit.condition;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.VisibleForTesting;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

class RequestMappingMethodParameterAnnotationConditionTest {

    final ArchRule ruleWithConditionUnderTest = methods().that().areAnnotatedWith(VisibleForTesting.class)
            .should(new RequestMappingMethodParameterAnnotationCondition());

    @Nested
    class Check {

        @Test
        void should_throwNoException_when_pathVariableAnnotationIsGiven() {
            ruleWithConditionUnderTest.check(
                    new ClassFileImporter(List.of(new ImportOption.OnlyIncludeTests())).importClasses(PathVariableAnnotationIsGivenOnParameter.class));
        }

        @Test
        void should_throwNoException_when_RequestParamAnnotationIsGiven() {
            ruleWithConditionUnderTest.check(
                    new ClassFileImporter(List.of(new ImportOption.OnlyIncludeTests())).importClasses(RequestParamAnnotationIsGivenOnParameter.class));
        }

        @Test
        void should_throwNoException_when_RequestBodyAnnotationIsGiven() {
            ruleWithConditionUnderTest.check(
                    new ClassFileImporter(List.of(new ImportOption.OnlyIncludeTests())).importClasses(RequestBodyAnnotationIsGivenOnParameter.class));
        }

        @Test
        void should_throwNoException_when_RequestHeaderAnnotationIsGiven() {
            ruleWithConditionUnderTest.check(
                    new ClassFileImporter(List.of(new ImportOption.OnlyIncludeTests())).importClasses(RequestHeaderAnnotationIsGivenOnParameter.class));
        }

        @Test
        void should_throwNoException_when_methodHasZeroParameters() {
            ruleWithConditionUnderTest.check(
                    new ClassFileImporter(List.of(new ImportOption.OnlyIncludeTests())).importClasses(NoParametersAreGiven.class));
        }

        @Test
        void should_throwNoException_when_eachParameterHasOneOfTheRequiredAnnotation() {
            ruleWithConditionUnderTest.check(
                    new ClassFileImporter(List.of(new ImportOption.OnlyIncludeTests())).importClasses(EachParameterHasOneOfTheRequiredAnnotation.class));
        }

        @Test
        void should_throwException_when_noRequiredAnnotationIsGiven() {
            Assertions.assertThatThrownBy(() -> ruleWithConditionUnderTest.check(
                    new ClassFileImporter(List.of(new ImportOption.OnlyIncludeTests())).importClasses(NoAnnotationIsGivenOnAnyParameter.class)))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("methodUnderTest")
                    .hasMessageContaining("#0")
                    .hasMessageContaining("#1")
                    .hasMessageContaining(NoAnnotationIsGivenOnAnyParameter.class.getName());
        }
    }

}

class PathVariableAnnotationIsGivenOnParameter {

    @VisibleForTesting
    public void methodUnderTest(@PathVariable final String parameter) {

    }
}

class RequestHeaderAnnotationIsGivenOnParameter {

    @VisibleForTesting
    public void methodUnderTest(@RequestHeader final String parameter) {

    }
}

class RequestParamAnnotationIsGivenOnParameter {

    @VisibleForTesting
    public void methodUnderTest(@RequestParam final String parameter) {

    }
}

class RequestBodyAnnotationIsGivenOnParameter {

    @VisibleForTesting
    public void methodUnderTest(@RequestBody final String parameter) {

    }
}

class NoAnnotationIsGivenOnAnyParameter {

    @VisibleForTesting
    public void methodUnderTest(final String parameter1, final String parameter2) {

    }
}

class NoParametersAreGiven {

    @VisibleForTesting
    public void methodUnderTest() {

    }
}

class EachParameterHasOneOfTheRequiredAnnotation {

    @VisibleForTesting
    public void methodUnderTest(@PathVariable final String pathVariableParameter, @RequestParam final String requestParamParameter,
            @RequestBody final String requestBodyParameter, @RequestHeader final String requestHeaderParameter) {

    }
}
