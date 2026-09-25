import { usePersistedStimmzettelTestDataFactory } from "@tests/utils/dse/PersistedStimmzettelTestDataFactory.ts";
import { afterEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import { useMbwStimmzettelFilterService } from "@/composables/dse/mbwStimmzettelFilterService.ts";

const mockDefinitions = vi.hoisted(() => ({
  matchesMBWStapelA: vi.fn(),
  matchesMBWStapelB: vi.fn(),
  matchesMBWStapelBC: vi.fn(),
  matchesMBWStapelDUngueltig: vi.fn(),
  matchesMBWStapelEUngueltig: vi.fn(),
}));

vi.mock(
  import("@/composables/dse/stimmzettelerfassung/PersistedStimmzettelTools.ts"),
  () => ({
    usePersistedStimmzettelTools: () => mockDefinitions,
  })
);

describe("mbwStimmzettelFilterService.ts", () => {
  const {
    createPersistedStimmzettel,
    preparePersistedStimmzettel,
    preparePersistedStimmzettelWahlvorschlag,
  } = usePersistedStimmzettelTestDataFactory();

  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
  });

  describe("stapelA", () => {
    it("should_useStapelAMatcher_when_stimmzettelAreFiltered", () => {
      const stimmzettel1 = createPersistedStimmzettel();
      const stimmzettel2 = createPersistedStimmzettel();
      const stimmzettel3 = createPersistedStimmzettel();
      const unitUnderTest = useMbwStimmzettelFilterService(
        ref([stimmzettel1, stimmzettel2, stimmzettel3])
      );

      mockDefinitions.matchesMBWStapelA.mockReturnValueOnce(true);
      mockDefinitions.matchesMBWStapelA.mockReturnValueOnce(false);
      mockDefinitions.matchesMBWStapelA.mockReturnValueOnce(true);

      expect(unitUnderTest.stapelA.value).toStrictEqual([
        stimmzettel1,
        stimmzettel3,
      ]);
    });
  });

  describe("stapelB", () => {
    it("should_useStapelBMatcher_when_stimmzettelAreFiltered", () => {
      const stimmzettel1 = createPersistedStimmzettel();
      const stimmzettel2 = createPersistedStimmzettel();
      const stimmzettel3 = createPersistedStimmzettel();
      const unitUnderTest = useMbwStimmzettelFilterService(
        ref([stimmzettel1, stimmzettel2, stimmzettel3])
      );

      mockDefinitions.matchesMBWStapelB.mockReturnValueOnce(true);
      mockDefinitions.matchesMBWStapelB.mockReturnValueOnce(false);
      mockDefinitions.matchesMBWStapelB.mockReturnValueOnce(true);

      expect(unitUnderTest.stapelB.value).toStrictEqual([
        stimmzettel1,
        stimmzettel3,
      ]);
    });
  });

  describe("stapelBC", () => {
    it("should_useStapelBCMatcher_when_stimmzettelAreFiltered", () => {
      const stimmzettel1 = createPersistedStimmzettel();
      const stimmzettel2 = createPersistedStimmzettel();
      const stimmzettel3 = createPersistedStimmzettel();
      const unitUnderTest = useMbwStimmzettelFilterService(
        ref([stimmzettel1, stimmzettel2, stimmzettel3])
      );

      mockDefinitions.matchesMBWStapelBC.mockReturnValueOnce(true);
      mockDefinitions.matchesMBWStapelBC.mockReturnValueOnce(false);
      mockDefinitions.matchesMBWStapelBC.mockReturnValueOnce(true);

      expect(unitUnderTest.stapelBC.value).toStrictEqual([
        stimmzettel1,
        stimmzettel3,
      ]);
    });
  });

  describe("stapelDUngueltig", () => {
    it("should_useStapelDUngueltigMatcher_when_stimmzettelAreFiltered", () => {
      const stimmzettel1 = createPersistedStimmzettel();
      const stimmzettel2 = createPersistedStimmzettel();
      const stimmzettel3 = createPersistedStimmzettel();
      const unitUnderTest = useMbwStimmzettelFilterService(
        ref([stimmzettel1, stimmzettel2, stimmzettel3])
      );

      mockDefinitions.matchesMBWStapelDUngueltig.mockReturnValueOnce(true);
      mockDefinitions.matchesMBWStapelDUngueltig.mockReturnValueOnce(false);
      mockDefinitions.matchesMBWStapelDUngueltig.mockReturnValueOnce(true);

      expect(unitUnderTest.stapelDUngueltig.value).toStrictEqual([
        stimmzettel1,
        stimmzettel3,
      ]);
    });
  });

  describe("stapelEUngueltig", () => {
    it("should_useStapelEUngueltigMatcher_when_stimmzettelAreFiltered", () => {
      const stimmzettel1 = createPersistedStimmzettel();
      const stimmzettel2 = createPersistedStimmzettel();
      const stimmzettel3 = createPersistedStimmzettel();
      const unitUnderTest = useMbwStimmzettelFilterService(
        ref([stimmzettel1, stimmzettel2, stimmzettel3])
      );

      mockDefinitions.matchesMBWStapelEUngueltig.mockReturnValueOnce(true);
      mockDefinitions.matchesMBWStapelEUngueltig.mockReturnValueOnce(false);
      mockDefinitions.matchesMBWStapelEUngueltig.mockReturnValueOnce(true);

      expect(unitUnderTest.stapelEUngueltig.value).toStrictEqual([
        stimmzettel1,
        stimmzettel3,
      ]);
    });
  });

  describe("stapelASumGroupedByWahlvorschlag", () => {
    it("should_sumWahlvorschlaege_when_stapelAContainsMultipleStimmzettel", () => {
      const stimmzettelA = preparePersistedStimmzettel()
        .wahlvorschlaege([
          preparePersistedStimmzettelWahlvorschlag()
            .wahlvorschlagID("wahlvorschlag-1")
            .build(),
          preparePersistedStimmzettelWahlvorschlag()
            .wahlvorschlagID("wahlvorschlag-2")
            .build(),
        ])
        .build();
      const stimmzettelB = preparePersistedStimmzettel()
        .wahlvorschlaege([
          preparePersistedStimmzettelWahlvorschlag()
            .wahlvorschlagID("wahlvorschlag-1")
            .build(),
        ])
        .build();
      mockDefinitions.matchesMBWStapelA.mockReturnValue(true);
      const unitUnderTest = useMbwStimmzettelFilterService(
        ref([stimmzettelA, stimmzettelB])
      );

      const sum = unitUnderTest.stapelASumGroupedByWahlvorschlag.value;

      expect(sum.getOrDefault("wahlvorschlag-1")).toStrictEqual(2);
      expect(sum.getOrDefault("wahlvorschlag-2")).toStrictEqual(1);
    });
  });

  describe("stapelBSumGroupedByWahlvorschlag", () => {
    it("should_sumWahlvorschlaege_when_stapelBContainsMultipleStimmzettel", () => {
      const stimmzettelA = preparePersistedStimmzettel()
        .wahlvorschlaege([
          preparePersistedStimmzettelWahlvorschlag()
            .wahlvorschlagID("wahlvorschlag-1")
            .build(),
          preparePersistedStimmzettelWahlvorschlag()
            .wahlvorschlagID("wahlvorschlag-2")
            .build(),
        ])
        .build();
      const stimmzettelB = preparePersistedStimmzettel()
        .wahlvorschlaege([
          preparePersistedStimmzettelWahlvorschlag()
            .wahlvorschlagID("wahlvorschlag-1")
            .build(),
        ])
        .build();
      mockDefinitions.matchesMBWStapelB.mockReturnValue(true);
      const unitUnderTest = useMbwStimmzettelFilterService(
        ref([stimmzettelA, stimmzettelB])
      );

      const sum = unitUnderTest.stapelBSumGroupedByWahlvorschlag.value;

      expect(sum.getOrDefault("wahlvorschlag-1")).toStrictEqual(2);
      expect(sum.getOrDefault("wahlvorschlag-2")).toStrictEqual(1);
    });
  });
});
