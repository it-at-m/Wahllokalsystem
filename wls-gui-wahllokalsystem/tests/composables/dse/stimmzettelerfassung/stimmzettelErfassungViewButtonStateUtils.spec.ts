import type { StimmzettelerfassungTeamStatus } from "@/types/dse/StimmzettelerfassungTeamStatus.ts";

import { afterEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import { useStimmzettelErfassungViewButtonStateUtils } from "@/composables/dse/stimmzettelerfassung/stimmzettelErfassungViewButtonStateUtils.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";

describe("stimmzettelErfassungViewButtonStateUtils.ts", () => {
  afterEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  const enumValues = Object.values(StimmzettelerfassungTeamStatusEnum);

  type TestCases = [string, StimmzettelerfassungTeamStatus | null, boolean][];

  describe("startenBtnActive", () => {
    const startenBtnActiveCases: TestCases = [
      [
        "REGISTRIERT",
        { status: StimmzettelerfassungTeamStatusEnum.REGISTRIERT },
        true,
      ],
      [
        "UNTERBROCHEN",
        { status: StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN },
        true,
      ],
      [
        "IN_BEARBEITUNG",
        { status: StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG },
        false,
      ],
      [
        "ABGESCHLOSSEN",
        { status: StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN },
        false,
      ],
      ["null", null, false],
    ];

    it.each(startenBtnActiveCases)(
      "should_haveCorrectState_when_statusIs%s",
      (_label, teamStatus, expected) => {
        const teamStatusRef = ref<StimmzettelerfassungTeamStatus | null>(
          teamStatus
        );
        const unitUnderTest =
          useStimmzettelErfassungViewButtonStateUtils(teamStatusRef);

        expect(unitUnderTest.startenBtnActive.value).toBe(expected);
      }
    );

    it("should_coverAllEnumValues_whenTestCasesAreDefined", () => {
      verifyThatTestCasesCoverAllEnumValues(startenBtnActiveCases);
    });
  });

  describe("beendenBtnActive", () => {
    const beendenBtnActiveCases: TestCases = [
      [
        "IN_BEARBEITUNG",
        { status: StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG },
        true,
      ],
      [
        "REGISTRIERT",
        { status: StimmzettelerfassungTeamStatusEnum.REGISTRIERT },
        false,
      ],
      [
        "UNTERBROCHEN",
        { status: StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN },
        false,
      ],
      [
        "ABGESCHLOSSEN",
        { status: StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN },
        false,
      ],
      ["null", null, false],
    ];

    it.each(beendenBtnActiveCases)(
      "should_haveCorrectState_when_statusIs%s",
      (_label, teamStatus, expected) => {
        const teamStatusRef = ref<StimmzettelerfassungTeamStatus | null>(
          teamStatus
        );
        const unitUnderTest =
          useStimmzettelErfassungViewButtonStateUtils(teamStatusRef);

        expect(unitUnderTest.beendenBtnActive.value).toBe(expected);
      }
    );

    it("should_coverAllEnumValues_whenTestCasesAreDefined", () => {
      verifyThatTestCasesCoverAllEnumValues(beendenBtnActiveCases);
    });
  });

  describe("unterbrechenBtnIsDisabled", () => {
    const unterbrechenBtnIsDisabledCases: TestCases = [
      [
        "IN_BEARBEITUNG",
        { status: StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG },
        false,
      ],
      [
        "REGISTRIERT",
        { status: StimmzettelerfassungTeamStatusEnum.REGISTRIERT },
        true,
      ],
      [
        "UNTERBROCHEN",
        { status: StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN },
        true,
      ],
      [
        "ABGESCHLOSSEN",
        { status: StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN },
        true,
      ],
      ["null", null, true],
    ];

    it.each(unterbrechenBtnIsDisabledCases)(
      "should_haveCorrectState_when_statusIs%s",
      (_label, teamStatus, expected) => {
        const teamStatusRef = ref<StimmzettelerfassungTeamStatus | null>(
          teamStatus
        );
        const unitUnderTest =
          useStimmzettelErfassungViewButtonStateUtils(teamStatusRef);

        expect(unitUnderTest.unterbrechenBtnIsDisabled.value).toBe(expected);
      }
    );

    it("should_coverAllEnumValues_whenTestCasesAreDefined", () => {
      verifyThatTestCasesCoverAllEnumValues(unterbrechenBtnIsDisabledCases);
    });
  });

  function verifyThatTestCasesCoverAllEnumValues(testCases: TestCases) {
    const testedStatuses = Array.from(
      new Set(
        testCases
          .map(([, teamStatus]) => teamStatus?.status)
          .filter(
            (status): status is StimmzettelerfassungTeamStatusEnum =>
              status !== undefined
          )
      )
    ).sort();
    expect(testedStatuses).toStrictEqual(enumValues.slice().sort());
  }
});
