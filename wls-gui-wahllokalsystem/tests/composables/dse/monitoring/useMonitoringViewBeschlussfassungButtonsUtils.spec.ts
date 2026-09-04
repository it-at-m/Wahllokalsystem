import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useStimmzettelerfassungStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungStatusTestDataFactory.ts";
import { useStimmzettelerfassungTeamStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungTeamStatusTestDataFactory.ts";
import { describe, expect, it } from "vitest";
import { ref } from "vue";

import { useMonitoringViewBeschlussfassungButtonsUtils } from "@/composables/dse/monitoring/monitoringViewBeschlussfassungButtonsUtils.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEnum.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/stimmzettelerfassungWorkflowStatus/StimmzettelerfassungStatusEnum.ts";

const {
  createStimmzettelerfassungTeamStatusEntry,
  prepareStimmzettelerfassungTeamStatusEntry,
} = useStimmzettelerfassungTeamStatusTestDataFactory();
const { createStimmzettelerfassungStatus, prepareStimmzettelerfassungStatus } =
  useStimmzettelerfassungStatusTestDataFactory();
const { generateRandomBoolean, getRandomItem } = useCommonTestDataFactory();

describe("useMonitoringViewBeschlussfassungButtonsUtils", () => {
  const teamNotDoneStates = Object.values(
    StimmzettelerfassungTeamStatusEnum
  ).filter(
    (enumValue) =>
      enumValue !== StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN
  );

  const workflowStateWhereBeschlussfassungWasAlreadyStarted = Object.values(
    StimmzettelerfassungStatusEnum
  ).filter(
    (enumValue) =>
      enumValue === StimmzettelerfassungStatusEnum.SteAbgeschlossen ||
      enumValue === StimmzettelerfassungStatusEnum.BeAbgeschlossen
  );
  const workflowStateWhereBeschlussfassungWasNotAlreadyStarted = Object.values(
    StimmzettelerfassungStatusEnum
  ).filter(
    (enumValue) =>
      workflowStateWhereBeschlussfassungWasAlreadyStarted.find(
        (startedStatus) => startedStatus === enumValue
      ) === undefined
  );

  describe("isBeschlussfassungBtnActive", () => {
    it("should_returnFalse_when_noTeamStatusAreGiven", () => {
      const { isBeschlussfassungBtnActive } =
        useMonitoringViewBeschlussfassungButtonsUtils(
          ref(generateRandomBoolean()),
          ref(generateRandomBoolean()),
          ref([]),
          ref(createStimmzettelerfassungStatus())
        );

      expect(isBeschlussfassungBtnActive.value).toStrictEqual(false);
    });

    it("should_returnTrue_when_allTeamsHaveDoneTheirWorkWithOneTeam", () => {
      const teamStatusEntry = prepareStimmzettelerfassungTeamStatusEntry()
        .status(StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN)
        .build();

      const { isBeschlussfassungBtnActive } =
        useMonitoringViewBeschlussfassungButtonsUtils(
          ref(generateRandomBoolean()),
          ref(generateRandomBoolean()),
          ref([teamStatusEntry]),
          ref(createStimmzettelerfassungStatus())
        );

      expect(isBeschlussfassungBtnActive.value).toStrictEqual(true);
    });

    it("should_returnTrue_when_allTeamsHaveDoneTheirWorkWithMultipleTeams", () => {
      const teamStatusEntry1 = prepareStimmzettelerfassungTeamStatusEntry()
        .status(StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN)
        .build();
      const teamStatusEntry2 = prepareStimmzettelerfassungTeamStatusEntry()
        .status(StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN)
        .build();

      const { isBeschlussfassungBtnActive } =
        useMonitoringViewBeschlussfassungButtonsUtils(
          ref(generateRandomBoolean()),
          ref(generateRandomBoolean()),
          ref([teamStatusEntry1, teamStatusEntry2]),
          ref(createStimmzettelerfassungStatus())
        );

      expect(isBeschlussfassungBtnActive.value).toStrictEqual(true);
    });

    it.each([teamNotDoneStates])(
      "should_returnTrue_when_atLeastOneTeamIsNotDone'%s'",
      (notDoneStatus) => {
        const teamStatusEntry = prepareStimmzettelerfassungTeamStatusEntry()
          .status(notDoneStatus)
          .build();

        const { isBeschlussfassungBtnActive } =
          useMonitoringViewBeschlussfassungButtonsUtils(
            ref(generateRandomBoolean()),
            ref(generateRandomBoolean()),
            ref([teamStatusEntry]),
            ref(createStimmzettelerfassungStatus())
          );

        expect(isBeschlussfassungBtnActive.value).toStrictEqual(false);
      }
    );
  });

  describe("isMoveOnToBeschlussfassungDisabled", () => {
    it("should_returnFalse_when_nothingIsLoadingAndRegisteredTeamIsDoneAndStimmzettelerfassungStatusIsNotBeAbgeschlossen", () => {
      const teamStatusEntry = prepareStimmzettelerfassungTeamStatusEntry()
        .status(StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN)
        .build();

      const { isMoveOnToBeschlussfassungDisabled } =
        useMonitoringViewBeschlussfassungButtonsUtils(
          ref(false),
          ref(false),
          ref([teamStatusEntry]),
          ref(_createNotBeAbgeschlossenStimmzettelerfassungStatus())
        );

      expect(isMoveOnToBeschlussfassungDisabled.value).toStrictEqual(false);
    });

    it("should_returnTrue_when_nothingIsLoadingAndAllRegisteredTeamsAreDoneButStimmzettelerfassungStatusIsBeAbgeschlossen", () => {
      const teamStatusEntry = prepareStimmzettelerfassungTeamStatusEntry()
        .status(StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN)
        .build();

      const stimmzettelerfassungStatus = prepareStimmzettelerfassungStatus()
        .status(StimmzettelerfassungStatusEnum.BeAbgeschlossen)
        .build();

      const { isMoveOnToBeschlussfassungDisabled } =
        useMonitoringViewBeschlussfassungButtonsUtils(
          ref(false),
          ref(false),
          ref([teamStatusEntry]),
          ref(stimmzettelerfassungStatus)
        );

      expect(isMoveOnToBeschlussfassungDisabled.value).toStrictEqual(true);
    });

    it("should_returnFalse_when_nothingIsLoadingAndAllRegisteredTeamsAreDone", () => {
      const teamStatusEntry1 = prepareStimmzettelerfassungTeamStatusEntry()
        .status(StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN)
        .build();
      const teamStatusEntry2 = prepareStimmzettelerfassungTeamStatusEntry()
        .status(StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN)
        .build();

      const { isMoveOnToBeschlussfassungDisabled } =
        useMonitoringViewBeschlussfassungButtonsUtils(
          ref(false),
          ref(false),
          ref([teamStatusEntry1, teamStatusEntry2]),
          ref(_createNotBeAbgeschlossenStimmzettelerfassungStatus())
        );

      expect(isMoveOnToBeschlussfassungDisabled.value).toStrictEqual(false);
    });

    it("should_returnTrue_when_teamStatusListIsLoading", () => {
      const teamStatusEntry = prepareStimmzettelerfassungTeamStatusEntry()
        .status(StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN)
        .build();

      const { isMoveOnToBeschlussfassungDisabled } =
        useMonitoringViewBeschlussfassungButtonsUtils(
          ref(true),
          ref(false),
          ref([teamStatusEntry]),
          ref(_createNotBeAbgeschlossenStimmzettelerfassungStatus())
        );

      expect(isMoveOnToBeschlussfassungDisabled.value).toStrictEqual(true);
    });

    it("should_returnTrue_when_workflowStatusIsLoading", () => {
      const teamStatusEntry = prepareStimmzettelerfassungTeamStatusEntry()
        .status(StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN)
        .build();

      const { isMoveOnToBeschlussfassungDisabled } =
        useMonitoringViewBeschlussfassungButtonsUtils(
          ref(false),
          ref(true),
          ref([teamStatusEntry]),
          ref(_createNotBeAbgeschlossenStimmzettelerfassungStatus())
        );

      expect(isMoveOnToBeschlussfassungDisabled.value).toStrictEqual(true);
    });

    it("should_returnTrue_when_noTeamsWithStatusExists", () => {
      const { isMoveOnToBeschlussfassungDisabled } =
        useMonitoringViewBeschlussfassungButtonsUtils(
          ref(false),
          ref(false),
          ref([]),
          ref(_createNotBeAbgeschlossenStimmzettelerfassungStatus())
        );

      expect(isMoveOnToBeschlussfassungDisabled.value).toStrictEqual(true);
    });

    it.each(teamNotDoneStates)(
      "should_returnTrue_when_atTheOnlyRegisteredTeamIsNotDone'%s'",
      (notDoneStatus) => {
        const teamStatusEntry = prepareStimmzettelerfassungTeamStatusEntry()
          .status(notDoneStatus)
          .build();

        const { isMoveOnToBeschlussfassungDisabled } =
          useMonitoringViewBeschlussfassungButtonsUtils(
            ref(false),
            ref(false),
            ref([teamStatusEntry]),
            ref(_createNotBeAbgeschlossenStimmzettelerfassungStatus())
          );

        expect(isMoveOnToBeschlussfassungDisabled.value).toStrictEqual(true);
      }
    );

    it.each(teamNotDoneStates)(
      "should_returnTrue_when_atLeastOneTeamIsNotDone'%s'",
      (notDoneStatus) => {
        const teamStatusEntryForTeamThatIsDone =
          prepareStimmzettelerfassungTeamStatusEntry()
            .status(StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN)
            .build();
        const teamStatusEntryForTeamThatIsNotDone =
          prepareStimmzettelerfassungTeamStatusEntry()
            .status(notDoneStatus)
            .build();

        const { isMoveOnToBeschlussfassungDisabled } =
          useMonitoringViewBeschlussfassungButtonsUtils(
            ref(false),
            ref(false),
            ref([
              teamStatusEntryForTeamThatIsDone,
              teamStatusEntryForTeamThatIsNotDone,
            ]),
            ref(_createNotBeAbgeschlossenStimmzettelerfassungStatus())
          );

        expect(isMoveOnToBeschlussfassungDisabled.value).toStrictEqual(true);
      }
    );

    function _createNotBeAbgeschlossenStimmzettelerfassungStatus() {
      const statusValuesWithoutBeAbgeschlossen = Object.values(
        StimmzettelerfassungStatusEnum
      ).filter(
        (enumValue) =>
          enumValue !== StimmzettelerfassungStatusEnum.BeAbgeschlossen
      );
      return prepareStimmzettelerfassungStatus()
        .status(getRandomItem(statusValuesWithoutBeAbgeschlossen))
        .build();
    }
  });

  describe("isBeschlussfassungContinueBtnVisible", () => {
    it.each(workflowStateWhereBeschlussfassungWasAlreadyStarted)(
      "should_returnTrue_when_beschlussFassungWasAlreadyStartedByStatus'%s'",
      (status) => {
        const erfassungsStatus = prepareStimmzettelerfassungStatus()
          .status(status)
          .build();

        const { isBeschlussfassungContinueBtnVisible } =
          useMonitoringViewBeschlussfassungButtonsUtils(
            ref(generateRandomBoolean()),
            ref(generateRandomBoolean()),
            ref([createStimmzettelerfassungTeamStatusEntry()]),
            ref(erfassungsStatus)
          );

        expect(isBeschlussfassungContinueBtnVisible.value).toStrictEqual(true);
      }
    );

    it.each(workflowStateWhereBeschlussfassungWasNotAlreadyStarted)(
      "should_returnFalse_when_beschlussFassungWasNotAlreadyStartedByStatus'%s'",
      (status) => {
        const erfassungsStatus = prepareStimmzettelerfassungStatus()
          .status(status)
          .build();

        const { isBeschlussfassungContinueBtnVisible } =
          useMonitoringViewBeschlussfassungButtonsUtils(
            ref(generateRandomBoolean()),
            ref(generateRandomBoolean()),
            ref([createStimmzettelerfassungTeamStatusEntry()]),
            ref(erfassungsStatus)
          );

        expect(isBeschlussfassungContinueBtnVisible.value).toStrictEqual(false);
      }
    );
  });

  describe("isBeschlussfassungStartenBtnVisible", () => {
    it.each(workflowStateWhereBeschlussfassungWasAlreadyStarted)(
      "should_returnFalse_when_beschlussFassungWasAlreadyStartedByStatus'%s'",
      (status) => {
        const erfassungsStatus = prepareStimmzettelerfassungStatus()
          .status(status)
          .build();

        const { isBeschlussfassungStartenBtnVisible } =
          useMonitoringViewBeschlussfassungButtonsUtils(
            ref(generateRandomBoolean()),
            ref(generateRandomBoolean()),
            ref([createStimmzettelerfassungTeamStatusEntry()]),
            ref(erfassungsStatus)
          );

        expect(isBeschlussfassungStartenBtnVisible.value).toStrictEqual(false);
      }
    );

    it.each(workflowStateWhereBeschlussfassungWasNotAlreadyStarted)(
      "should_returnTrue_when_beschlussFassungWasNotAlreadyStartedByStatus'%s'",
      (status) => {
        const erfassungsStatus = prepareStimmzettelerfassungStatus()
          .status(status)
          .build();

        const { isBeschlussfassungStartenBtnVisible } =
          useMonitoringViewBeschlussfassungButtonsUtils(
            ref(generateRandomBoolean()),
            ref(generateRandomBoolean()),
            ref([createStimmzettelerfassungTeamStatusEntry()]),
            ref(erfassungsStatus)
          );

        expect(isBeschlussfassungStartenBtnVisible.value).toStrictEqual(true);
      }
    );

    it("should_returnTrue_when_noStatusExists", () => {
      const { isBeschlussfassungStartenBtnVisible } =
        useMonitoringViewBeschlussfassungButtonsUtils(
          ref(generateRandomBoolean()),
          ref(generateRandomBoolean()),
          ref([createStimmzettelerfassungTeamStatusEntry()]),
          ref(null)
        );

      expect(isBeschlussfassungStartenBtnVisible.value).toStrictEqual(true);
    });
  });
});
