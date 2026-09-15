import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useWahlvorschlagTools } from "@/composables/dse/stimmzettelerfassung/wahlvorschlagTools.ts";

const mockDefinitions = await vi.hoisted(async () => ({
  sortKandidaten: vi.fn(),
}));

vi.mock(
  import("@/composables/dse/stimmzettelerfassung/kandidatTools.ts"),
  async (importOriginal) => {
    const original = await importOriginal();

    return {
      useKandidatTools: () => ({
        ...original.useKandidatTools(),
        sortKandidaten: mockDefinitions.sortKandidaten,
      }),
    };
  }
);

const {
  preparePersistedStimmzettelWahlvorschlag,
  createPersistedStimmzettelKandidat,
} = useStimmzettelTestDataFactory();

describe("useWahlvorschlagTools.ts", () => {
  let unitUnderTest: ReturnType<typeof useWahlvorschlagTools>;

  beforeEach(() => {
    unitUnderTest = useWahlvorschlagTools();
    vi.clearAllMocks();
  });

  describe("sortWahlvorschlaege", () => {
    const wvA = preparePersistedStimmzettelWahlvorschlag()
      .wahlvorschlagID("a")
      .selected(true)
      .kandidaten([
        createPersistedStimmzettelKandidat(),
        createPersistedStimmzettelKandidat(),
      ])
      .build();

    const wvB = preparePersistedStimmzettelWahlvorschlag()
      .wahlvorschlagID("b")
      .selected(false)
      .kandidaten([createPersistedStimmzettelKandidat()])
      .build();

    const wvC = preparePersistedStimmzettelWahlvorschlag()
      .wahlvorschlagID("c")
      .selected(true)
      .kandidaten([
        createPersistedStimmzettelKandidat(),
        createPersistedStimmzettelKandidat(),
        createPersistedStimmzettelKandidat(),
      ])
      .build();

    it.each([
      { text: "NotSorted", wahlvorschlaege: [wvB, wvA, wvC] },
      { text: "Sorted", wahlvorschlaege: [wvA, wvB, wvC] },
    ])(
      `should_returnSortedWahlvorschlaege_when_givenListOfWahlvorschlaegeThatIs'$text'`,
      ({ wahlvorschlaege }) => {
        const sortedKandidatenA = [createPersistedStimmzettelKandidat()];
        const sortedKandidatenB = [
          createPersistedStimmzettelKandidat(),
          createPersistedStimmzettelKandidat(),
        ];
        const sortedKandidatenC: ReturnType<
          typeof createPersistedStimmzettelKandidat
        >[] = [];

        mockDefinitions.sortKandidaten.mockImplementation((arr) => {
          if (arr === wvA.kandidaten) return sortedKandidatenA;
          if (arr === wvB.kandidaten) return sortedKandidatenB;
          if (arr === wvC.kandidaten) return sortedKandidatenC;
          return [];
        });

        const expectedResult = [
          {
            wahlvorschlagID: "a",
            selected: true,
            kandidaten: sortedKandidatenA,
          },
          {
            wahlvorschlagID: "b",
            selected: false,
            kandidaten: sortedKandidatenB,
          },
          {
            wahlvorschlagID: "c",
            selected: true,
            kandidaten: sortedKandidatenC,
          },
        ];

        const result = unitUnderTest.sortWahlvorschlaege(wahlvorschlaege);

        expect(result).toStrictEqual(expectedResult);
        expect(mockDefinitions.sortKandidaten).toHaveBeenCalledTimes(3);
      }
    );

    it("should_returnEmptyList_when_givenEmptyList", () => {
      const result = unitUnderTest.sortWahlvorschlaege([]);

      expect(result).toStrictEqual([]);
      expect(mockDefinitions.sortKandidaten).not.toHaveBeenCalled();
    });
  });
});
