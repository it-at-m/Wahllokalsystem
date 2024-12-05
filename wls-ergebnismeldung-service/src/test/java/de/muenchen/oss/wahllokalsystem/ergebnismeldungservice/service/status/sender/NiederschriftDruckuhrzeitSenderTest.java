package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.sender;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.MeldungModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NiederschriftDruckuhrzeitSenderTest {

    @Mock
    StatusClient statusClient;

    @InjectMocks
    NiederschriftDruckuhrzeitSender unitUnderTest;

    @Nested
    class SubmitStatus {

        @ParameterizedTest(name = "submit called cause {2}")
        @MethodSource("getArgumentsWhereSubmitIsCalledWithTestcaseNameAppendix")
        void should_callStatusClientPostNiederschriftDruckuhrzeit_when_requirementsAreMet(final ArgumentsAccessor arguments) {
            val id = new BezirkUndWahlID();
            val newStatus = arguments.get(0, StatusModel.class);
            val oldStatus = arguments.get(1, StatusModel.class);

            unitUnderTest.submitStatus(id, newStatus, oldStatus);

            Mockito.verify(statusClient).postNiederschriftDruckuhrzeit(eq(id), notNull());
        }

        @ParameterizedTest(name = "submit called cause {2}")
        @MethodSource("getArgumentsWhereSubmitIsNotCalledWithTestcaseNameAppendix")
        void should_dontCallStatusClientPostNiederschriftDruckuhrzeit_when_requirementsAreNotMet(final ArgumentsAccessor arguments) {
            val id = new BezirkUndWahlID();
            val newStatus = arguments.get(0, StatusModel.class);
            val oldStatus = arguments.get(1, StatusModel.class);

            unitUnderTest.submitStatus(id, newStatus, oldStatus);

            Mockito.verifyNoInteractions(statusClient);
        }

        /**
         * @return order of arguments: newStatus, oldStatus, TestcaseNameAppendix
         */
        public static Stream<Arguments> getArgumentsWhereSubmitIsCalledWithTestcaseNameAppendix() {
            return Stream.of(
                    Arguments.of(createStatusModelWithNiederschrift(createMeldungWithGedrucktFlag(false)),
                            createStatusModelWithNiederschrift(createMeldungWithGedrucktFlag(true)),
                            "gedruckt flag changed from true to false"),
                    Arguments.of(createStatusModelWithNiederschrift(createMeldungWithGedrucktFlag(true)),
                            createStatusModelWithNiederschrift(createMeldungWithGedrucktFlag(false)),
                            "gedruckt flag changed from false to true"),
                    Arguments.of(createStatusModelWithNiederschrift(createMeldungWithGedrucktFlag(true)),
                            null,
                            "only new status - with gedruckt is true")

            );
        }

        /**
         * @return order of arguments: newStatus, oldStatus, TestcaseNameAppendix
         */
        public static Stream<Arguments> getArgumentsWhereSubmitIsNotCalledWithTestcaseNameAppendix() {
            return Stream.of(
                    Arguments.of(createStatusModelWithNiederschrift(createMeldungWithGedrucktFlag(false)),
                            createStatusModelWithNiederschrift(createMeldungWithGedrucktFlag(false)),
                            "new and old gedruckt flag is equals with false"),
                    Arguments.of(createStatusModelWithNiederschrift(createMeldungWithGedrucktFlag(true)),
                            createStatusModelWithNiederschrift(createMeldungWithGedrucktFlag(true)),
                            "new and old gedruckt flag is equals with true"),
                    Arguments.of(createStatusModelWithNiederschrift(createMeldungWithGedrucktFlag(false)),
                            null,
                            "only new status - with gedruckt is false")

            );
        }

        private static StatusModel createStatusModelWithNiederschrift(final MeldungModel meldung) {
            return new StatusModel(null, null, meldung);
        }

        private static MeldungModel createMeldungWithGedrucktFlag(final boolean isGedruckt) {
            return new MeldungModel(null, isGedruckt, null, null);
        }

    }

}