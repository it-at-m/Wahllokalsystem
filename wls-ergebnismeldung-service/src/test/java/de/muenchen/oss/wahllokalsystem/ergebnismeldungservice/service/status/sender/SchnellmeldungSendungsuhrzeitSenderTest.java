package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.sender;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.MeldungModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.ValidierungsstatusModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Collection;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SchnellmeldungSendungsuhrzeitSenderTest {

    @Mock
    StatusClient statusClient;

    @InjectMocks
    SchnellmeldungSendungsuhrzeitSender unitUnderTest;

    @Nested
    class SubmitStatus {

        @ParameterizedTest
        @MethodSource("anyValidierungsStatusExceptNICHT_VALIDIERT")
        void should_callStatusClientPostSchnellmeldungSendungsuhrzeit_when_onlyNewStatusIsGivenWithValidierungsstatusNotNICHT_VALIDIERT(
                final ValidierungsstatusModel validierungsstatus) {
            val id = new BezirkUndWahlID();
            val newStatus = createStatusModelWithSchnellmeldung(createMeldungWithValidierungsstatus(validierungsstatus));

            unitUnderTest.submitStatus(id, newStatus, null);

            Mockito.verify(statusClient).postSchnellmeldungSendungsuhrzeit(eq(id), notNull());
        }

        @ParameterizedTest(name = "validierungsstatus - old: {0} new: {1}")
        @MethodSource("pairsOfUnequalValidierungsstatus")
        void should_callStatusClientPostSchnellmeldungSendungsuhrzeit_when_validierungsStatusChanged(final ArgumentsAccessor arguments) {
            val id = new BezirkUndWahlID();
            val newStatus = createStatusModelWithSchnellmeldung(
                    createMeldungWithValidierungsstatus(arguments.get(1, ValidierungsstatusModel.class)));
            val oldStatus = createStatusModelWithSchnellmeldung(
                    createMeldungWithValidierungsstatus(arguments.get(0, ValidierungsstatusModel.class)));

            unitUnderTest.submitStatus(id, newStatus, oldStatus);

            Mockito.verify(statusClient).postSchnellmeldungSendungsuhrzeit(eq(id), notNull());
        }

        @ParameterizedTest(name = "submit called cause {2}")
        @MethodSource("getArgumentsWhereSubmitIsNotCalledWithTestcaseNameAppendix")
        void should_dontCallStatusClientPostSchnellmeldungSendungsuhrzeit_when_requirementsAreNotMet(final ArgumentsAccessor arguments) {
            val id = new BezirkUndWahlID();
            val newStatus = arguments.get(0, StatusModel.class);
            val oldStatus = arguments.get(1, StatusModel.class);

            unitUnderTest.submitStatus(id, newStatus, oldStatus);

            Mockito.verifyNoInteractions(statusClient);
        }

        @ParameterizedTest()
        @EnumSource(ValidierungsstatusModel.class)
        void should_dontCallStatusClientPostSchnellmeldungSendungsuhrzeit_when_oldAndNewValidierungsstatusAreEqual(
                final ValidierungsstatusModel validierungsstatus) {
            val id = new BezirkUndWahlID();
            val newStatus = createStatusModelWithSchnellmeldung(createMeldungWithValidierungsstatus(validierungsstatus));
            val oldStatus = createStatusModelWithSchnellmeldung(createMeldungWithValidierungsstatus(validierungsstatus));

            unitUnderTest.submitStatus(id, newStatus, oldStatus);

            Mockito.verifyNoInteractions(statusClient);
        }

        public static Stream<Arguments> anyValidierungsStatusExceptNICHT_VALIDIERT() {
            return Stream.of(ValidierungsstatusModel.values()).filter(validierungsstatus -> validierungsstatus != ValidierungsstatusModel.NICHT_VALIDIERT)
                    .map(Arguments::of);
        }

        public static Stream<Arguments> pairsOfUnequalValidierungsstatus() {
            return Stream.of(ValidierungsstatusModel.values())
                    .map(validierungsstatusOneOfPair -> Stream.of(ValidierungsstatusModel.values())
                            .filter(validierungsstatusTwoOfPair -> validierungsstatusOneOfPair != validierungsstatusTwoOfPair)
                            .map(validierungsstatusTwoOfPair -> Arguments.of(validierungsstatusOneOfPair, validierungsstatusTwoOfPair))
                            .toList())
                    .flatMap(Collection::stream);
        }

        /**
         * @return order of arguments: newStatus, oldStatus, TestcaseNameAppendix
         */
        public static Stream<Arguments> getArgumentsWhereSubmitIsNotCalledWithTestcaseNameAppendix() {
            return Stream.of(
                    Arguments.of(createStatusModelWithSchnellmeldung(null),
                            null,
                            "only new status - schnellmeldung is null"),
                    Arguments.of(createStatusModelWithSchnellmeldung(createMeldungWithValidierungsstatus(null)),
                            null,
                            "only new status - validierungsstatus is null"),
                    Arguments.of(createStatusModelWithSchnellmeldung(createMeldungWithValidierungsstatus(ValidierungsstatusModel.NICHT_VALIDIERT)),
                            null,
                            "only new status - validierungsstatus is NICHT_VALIDIERT"),
                    Arguments.of(createStatusModelWithSchnellmeldung(createMeldungWithValidierungsstatus(null)),
                            createStatusModelWithSchnellmeldung(createMeldungWithValidierungsstatus(null)),
                            "new and old - both with validierungsstatus null"));
        }

        private static StatusModel createStatusModelWithSchnellmeldung(final MeldungModel meldung) {
            return new StatusModel(null, meldung, null);
        }

        private static MeldungModel createMeldungWithValidierungsstatus(
                final ValidierungsstatusModel validierungsstatusModel) {
            return new MeldungModel(validierungsstatusModel, false, null, null);
        }

    }

}
