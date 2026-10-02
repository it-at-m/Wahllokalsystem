import { useBeschlussfassungDialogDetailsTestDataFactory } from "@tests/utils/dse/BeschlussfassungDialogDetailsTestDataFactory.ts";
import { useBeschlussgrundOptionTestDataFactory } from "@tests/utils/dse/BeschlussgrundOptionTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useBeschlussfassungDialogDetailsTools } from "@/composables/dse/beschlussfassung/beschlussfassungDialogDetailsTools.ts";

const mockDefinitions = vi.hoisted(() => ({
  getBeschlussgrundEnumValueAsString: vi.fn((v: string) => `Mapped(${v})`),
}));

vi.mock("@/composables/dse/beschlussfassung/beschlussgrundTools.ts", () => {
  return {
    useBeschlussgrundTools: () => ({
      getBeschlussgrundEnumValueAsString:
        mockDefinitions.getBeschlussgrundEnumValueAsString,
    }),
  };
});

describe("useBeschlussfassungDialogDetailsTools.ts", () => {
  let unitUnderTest: ReturnType<typeof useBeschlussfassungDialogDetailsTools>;
  const { prepareBeschlussfassungDialogDetails } =
    useBeschlussfassungDialogDetailsTestDataFactory();
  const { prepareBeschlussgrundOption } =
    useBeschlussgrundOptionTestDataFactory();

  beforeEach(() => {
    unitUnderTest = useBeschlussfassungDialogDetailsTools();
  });

  describe("createEmptyBeschlussfassungDialogDetails", () => {
    it("should_returnEmptyDetails_when_called", () => {
      const result = unitUnderTest.createEmptyBeschlussfassungDialogDetails();

      expect(result).toStrictEqual({
        isStimmzettelGueltig: null,
        beschlussgruende: [],
        andererGrund: "",
        andererGrundChecked: false,
        beschlussText: "",
      });
    });
  });

  describe("mergeGruendeAndReturnBeschlusstext", () => {
    it("should_returnEmptyString_when_noGruendeSelectedAndAndererGrundNotChecked", () => {
      const details = prepareBeschlussfassungDialogDetails()
        .beschlussgruende([
          prepareBeschlussgrundOption().selected(false).build(),
        ])
        .andererGrund("")
        .andererGrundChecked(false)
        .build();

      const result = unitUnderTest.mergeGruendeAndReturnBeschlusstext(details);
      expect(result).toBe("");
    });

    it("should_joinSelectedGruendeWithComma_when_onlyGruendeSelected", () => {
      const details = prepareBeschlussfassungDialogDetails()
        .beschlussgruende([
          prepareBeschlussgrundOption().grund("A").selected(true).build(),
          prepareBeschlussgrundOption().grund("B").selected(true).build(),
          prepareBeschlussgrundOption().grund("C").selected(false).build(),
        ])
        .andererGrund("")
        .andererGrundChecked(false)
        .build();

      const result = unitUnderTest.mergeGruendeAndReturnBeschlusstext(details);
      expect(result).toBe("Mapped(A), Mapped(B)");
    });

    it("should_returnAndererGrund_when_onlyAndererGrundChecked", () => {
      const details = prepareBeschlussfassungDialogDetails()
        .beschlussgruende([
          prepareBeschlussgrundOption().grund("A").selected(false).build(),
          prepareBeschlussgrundOption().grund("B").selected(false).build(),
        ])
        .andererGrund("1234")
        .andererGrundChecked(true)
        .build();

      const result = unitUnderTest.mergeGruendeAndReturnBeschlusstext(details);
      expect(result).toBe("1234");
    });

    it("should_joinGruendeAndAndererGrund_when_bothPresent", () => {
      const details = prepareBeschlussfassungDialogDetails()
        .beschlussgruende([
          prepareBeschlussgrundOption().grund("A").selected(true).build(),
          prepareBeschlussgrundOption().grund("B").selected(false).build(),
        ])
        .andererGrund("1234")
        .andererGrundChecked(true)
        .build();

      const result = unitUnderTest.mergeGruendeAndReturnBeschlusstext(details);
      expect(result).toBe("Mapped(A), 1234");
    });

    it("should_ignoreAndererGrund_when_flagIsFalse", () => {
      const details = prepareBeschlussfassungDialogDetails()
        .beschlussgruende([
          prepareBeschlussgrundOption().grund("A").selected(true).build(),
        ])
        .andererGrund("1234")
        .andererGrundChecked(false)
        .build();

      const result = unitUnderTest.mergeGruendeAndReturnBeschlusstext(details);
      expect(result).toBe("Mapped(A)");
    });
  });
});
