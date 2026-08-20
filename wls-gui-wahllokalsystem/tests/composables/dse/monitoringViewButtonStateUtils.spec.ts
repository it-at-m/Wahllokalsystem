import type { StimmzettelerfassungStatus } from "@/types/dse/StimmzettelerfassungStatus.ts";
import type { StimmzettelerfassungTeamStatus } from "@/types/dse/StimmzettelerfassungTeamStatus.ts";

import { afterEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import { useMonitoringViewButtonStateUtils } from "@/composables/dse/monitoringViewButtonStateUtils.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/StimmzettelerfassungStatusEnum.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";

describe("monitoringViewButtonStateUtils.ts", () => {
  afterEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  const teamEnumValues = Object.values(StimmzettelerfassungTeamStatusEnum);
  const workflowEnumValues = Object.values(StimmzettelerfassungStatusEnum);

  type TeamStatus = StimmzettelerfassungTeamStatus | null;
  type WorkflowStatus = StimmzettelerfassungStatus | null;
  type TestCases = [string, TeamStatus, WorkflowStatus, boolean][];

  describe("wiederOeffnenButtonIsDisabled", () => {
    const cases: TestCases = [
      // team REGISTRIERT
      [
        "REGISTRIERT + null",
        { status: StimmzettelerfassungTeamStatusEnum.REGISTRIERT },
        null,
        true,
      ],
      [
        "REGISTRIERT + SteBearbeitung",
        { status: StimmzettelerfassungTeamStatusEnum.REGISTRIERT },
        { status: StimmzettelerfassungStatusEnum.SteBearbeitung },
        true,
      ],
      [
        "REGISTRIERT + SteAbgeschlossen",
        { status: StimmzettelerfassungTeamStatusEnum.REGISTRIERT },
        { status: StimmzettelerfassungStatusEnum.SteAbgeschlossen },
        true,
      ],
      [
        "REGISTRIERT + BeAbgeschlossen",
        { status: StimmzettelerfassungTeamStatusEnum.REGISTRIERT },
        { status: StimmzettelerfassungStatusEnum.BeAbgeschlossen },
        true,
      ],

      // team IN_BEARBEITUNG
      [
        "IN_BEARBEITUNG + null",
        { status: StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG },
        null,
        true,
      ],
      [
        "IN_BEARBEITUNG + SteBearbeitung",
        { status: StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG },
        { status: StimmzettelerfassungStatusEnum.SteBearbeitung },
        true,
      ],
      [
        "IN_BEARBEITUNG + SteAbgeschlossen",
        { status: StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG },
        { status: StimmzettelerfassungStatusEnum.SteAbgeschlossen },
        true,
      ],
      [
        "IN_BEARBEITUNG + BeAbgeschlossen",
        { status: StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG },
        { status: StimmzettelerfassungStatusEnum.BeAbgeschlossen },
        true,
      ],

      // team UNTERBROCHEN
      [
        "UNTERBROCHEN + null",
        { status: StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN },
        null,
        true,
      ],
      [
        "UNTERBROCHEN + SteBearbeitung",
        { status: StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN },
        { status: StimmzettelerfassungStatusEnum.SteBearbeitung },
        true,
      ],
      [
        "UNTERBROCHEN + SteAbgeschlossen",
        { status: StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN },
        { status: StimmzettelerfassungStatusEnum.SteAbgeschlossen },
        true,
      ],
      [
        "UNTERBROCHEN + BeAbgeschlossen",
        { status: StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN },
        { status: StimmzettelerfassungStatusEnum.BeAbgeschlossen },
        true,
      ],

      // team ABGESCHLOSSEN
      [
        "ABGESCHLOSSEN + null",
        { status: StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN },
        null,
        false,
      ],
      [
        "ABGESCHLOSSEN + SteBearbeitung",
        { status: StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN },
        { status: StimmzettelerfassungStatusEnum.SteBearbeitung },
        false,
      ],
      [
        "ABGESCHLOSSEN + SteAbgeschlossen",
        { status: StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN },
        { status: StimmzettelerfassungStatusEnum.SteAbgeschlossen },
        false,
      ],
      [
        "ABGESCHLOSSEN + BeAbgeschlossen",
        { status: StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN },
        { status: StimmzettelerfassungStatusEnum.BeAbgeschlossen },
        true,
      ],

      // team: null
      ["null + null", null, null, true],
      [
        "null + SteBearbeitung",
        null,
        { status: StimmzettelerfassungStatusEnum.SteBearbeitung },
        true,
      ],
      [
        "null + SteAbgeschlossen",
        null,
        { status: StimmzettelerfassungStatusEnum.SteAbgeschlossen },
        true,
      ],
      [
        "null + BeAbgeschlossen",
        null,
        { status: StimmzettelerfassungStatusEnum.BeAbgeschlossen },
        true,
      ],
    ];

    it.each(cases)(
      "should_haveCorrectDisabledState_when_%s",
      (_label, teamStatus, workflowStatus, expectedDisabled) => {
        const workflowRef = ref<WorkflowStatus>(workflowStatus);
        const teamStatusRef = ref<TeamStatus>(teamStatus);
        const unitUnderTest = useMonitoringViewButtonStateUtils(
          teamStatusRef,
          workflowRef
        );
        expect(unitUnderTest.wiederOeffnenButtonIsDisabled.value).toBe(
          expectedDisabled
        );
      }
    );

    it("should_coverAllTeamEnumValues_when_TestCasesAreDefined", () => {
      const testedStatuses = Array.from(
        new Set(
          cases
            .map(([, team]) => team?.status)
            .filter(
              (status): status is StimmzettelerfassungTeamStatusEnum =>
                status !== undefined
            )
        )
      ).sort();
      expect(testedStatuses).toStrictEqual(teamEnumValues.slice().sort());
    });

    it("should_coverAllWorkflowEnumValues_whenTestCasesAreDefined", () => {
      const testedWorkflowStatuses = Array.from(
        new Set(
          cases
            .map(([, , workflow]) => workflow?.status)
            .filter(
              (status): status is StimmzettelerfassungStatusEnum =>
                status !== undefined
            )
        )
      ).sort();
      expect(testedWorkflowStatuses).toStrictEqual(
        workflowEnumValues.slice().sort()
      );
    });
  });
});
