import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import { useMbwSchnellmeldungViewUtils } from "@/composables/ergebnismeldung/MBW/theMbwDseSchnellmeldungViewUtils.ts";

const mockDefinitions = await vi.hoisted(async () => {
  const activatedCallbacks: (() => Promise<void> | void)[] = [];

  return {
    registerActivated: (callback: () => Promise<void> | void) =>
      activatedCallbacks.push(callback),
    runActivatedCallbacks: async () => {
      const callbacks = activatedCallbacks.splice(0, activatedCallbacks.length);
      for (const callback of callbacks) {
        await callback();
      }
    },
    loadStimmzettelOfWahlbezirk: vi.fn(),
    getWahlvorschlaege: vi.fn(),
  };
});

vi.mock("vue", async (importOriginal) => {
  const actual = (await importOriginal()) as object;
  return {
    ...actual,
    onActivated: (callback: () => Promise<void> | void) =>
      mockDefinitions.registerActivated(callback),
  };
});

vi.mock("@/composables/dse/allStimmzettelOfWahlbezirkState.ts", () => ({
  useAllStimmzettelOfWahlbezirkState: () => ({
    stimmzettelOfWahlbezirk: ref([]),
    loadStimmzettelOfWahlbezirk: mockDefinitions.loadStimmzettelOfWahlbezirk,
  }),
}));

vi.mock("@/composables/dse/mbwStimmzettelFilterService.ts", () => ({
  useMbwStimmzettelFilterService: () => ({
    stapelASumGroupedByWahlvorschlag: ref([]),
    stapelBSumGroupedByWahlvorschlag: ref([]),
    stapelDUngueltig: ref([]),
    stapelEUngueltig: ref([]),
  }),
}));

vi.mock(
  "@/composables/ergebnismeldung/MBW/mbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper.ts",
  () => ({
    useMbwErgebnisseAndWahlvorschlagStapelSumReactiveMapper: () => ({
      wahlvorschlaegeErgebnisseStapelAAndB: ref([]),
    }),
  })
);

vi.mock("@/composables/wahlvorschlaege/wahlvorschlaegeService.ts", () => ({
  useWahlvorschlaegeService: () => ({
    getWahlvorschlaege: mockDefinitions.getWahlvorschlaege,
  }),
}));

const { createWahlvorschlaege } = useWahlvorschlaegeTestDataFactory();

describe("theMbwDseSchnellmeldungViewUtils.ts", () => {
  beforeEach(() => {
    useMbwSchnellmeldungViewUtils("wahlID", "wahlbezirkID");
  });

  afterEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("onActivated", () => {
    it("should_loadStimmzettelOfWahlbezirkAndGetWahlvorschlaege_when_onActivated", async () => {
      mockDefinitions.getWahlvorschlaege.mockResolvedValue(
        createWahlvorschlaege()
      );

      await mockDefinitions.runActivatedCallbacks();

      expect(
        mockDefinitions.loadStimmzettelOfWahlbezirk
      ).toHaveBeenCalledOnce();
      expect(mockDefinitions.getWahlvorschlaege).toHaveBeenCalledOnce();
    });
  });
});
