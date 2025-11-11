import type { WahlvorschlagWithScorableKandidaten } from "@/types/ergebnisermittlung/WahlvorschlagWithScorableKandidaten.ts";
import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";

import { spyOn } from "@storybook/test";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/ergebnisseTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import { all } from "axios";
import {
  afterAll,
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";

import { useMwbStapelBCUtils } from "@/composables/ergebnisermittlung/mwbStapelBCUtils.ts";

const mockDefinitions = vi.hoisted(() => ({
  getErgebnisse: vi.fn(),
  postErgebnisse: vi.fn(),
  getWahlvorschlaege: vi.fn(),
  mapToWahlvorschlagWithScorableKandidaten: vi.fn(),
}));

vi.mock(
  "@/composables/ergebnisermittlung/wahlvorschlagWithScorableKandidatenMapper.ts",
  () => ({
    useWahlvorschlagWithScorableKandidatenMapper: () => ({
      toWahlvorschlagWithScorableKandidaten:
        mockDefinitions.mapToWahlvorschlagWithScorableKandidaten,
    }),
  })
);

vi.mock("@/composables/ergebnismeldung/ergebnisService.ts", () => ({
  useErgebnisService: () => ({
    getErgebnisse: mockDefinitions.getErgebnisse,
    postErgebnisse: mockDefinitions.postErgebnisse,
  }),
}));

vi.mock("@/composables/wahlvorschlaege/wahlvorschlaegeService.ts", () => ({
  useWahlvorschlaegeService: () => ({
    getWahlvorschlaege: mockDefinitions.getWahlvorschlaege,
  }),
}));

const { generateRandomNumber, generateRandomString } =
  useCommonTestDataFactory();
const { createKandidat, prepareWahlvorschlag, prepareWahlvorschlaege } =
  useWahlvorschlaegeTestDataFactory();
const { createErgebnis, createErgebnisse, prepareErgebnisse } =
  useErgebnisseTestDataFactory();

describe("mwbStapelBCUtils.ts", () => {
  let unitUnderTest: ReturnType<typeof useMwbStapelBCUtils>;
  let wahlbezirkID: string;
  let wahlID: string;

  beforeEach(() => {
    wahlbezirkID = generateRandomString(10);
    wahlID = generateRandomString(10);
    unitUnderTest = useMwbStapelBCUtils(wahlbezirkID, wahlID);
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe("loadWahlvorschlaegeAndErgebnisse", () => {
    it("should_setScorable_when_wahlvorschlaegeAndErgebnisseAreGiven", async () => {
      expect(unitUnderTest.scorableWahlvorschlaege.value).toStrictEqual([]);
      expect(unitUnderTest.isLoading.value).toStrictEqual(false);

      const spyOnValueSetterOfIsLoading = spyOn(
        unitUnderTest.isLoading,
        "value",
        "set"
      );

      const mockedWahlvorschlaege = createWahlvorschlaege();
      mockDefinitions.getWahlvorschlaege.mockResolvedValue(
        mockedWahlvorschlaege
      );
      const mockedErgebnisse = createErgebnisse();
      mockDefinitions.getErgebnisse.mockReturnValue(mockedErgebnisse);

      const mockedMappingResult = createWahlvorschlagWithScorableKandidaten();
      mockDefinitions.mapToWahlvorschlagWithScorableKandidaten.mockReturnValue(
        mockedMappingResult
      );

      await unitUnderTest.loadWahlvorschlaegeAndErgebnisse();

      expect(spyOnValueSetterOfIsLoading.mock.calls).toStrictEqual([
        [true],
        [false],
      ]);

      expect(unitUnderTest.scorableWahlvorschlaege.value).toStrictEqual([
        mockedMappingResult,
        mockedMappingResult,
        mockedMappingResult,
      ]);
      [...mockedWahlvorschlaege.wahlvorschlaege].forEach((wahlvorschlag) => {
        expect(
          mockDefinitions.mapToWahlvorschlagWithScorableKandidaten
        ).toHaveBeenCalledWith(wahlvorschlag, mockedErgebnisse);
      });

      spyOnValueSetterOfIsLoading.mockRestore();
    });

    it("should_sortWahlvorschlaegeByOrdnungszahlAndKandidatenByListenposition_when_wahlvorschlaegeAreGiven", async () => {
      const mockedWahlvorschlaege = createWahlvorschlaege();
      mockDefinitions.getWahlvorschlaege.mockResolvedValue(
        mockedWahlvorschlaege
      );
      const mockedErgebnisse = createErgebnisse();
      mockDefinitions.getErgebnisse.mockReturnValue(mockedErgebnisse);

      const mockedMappingResult = createWahlvorschlagWithScorableKandidaten();
      mockDefinitions.mapToWahlvorschlagWithScorableKandidaten.mockReturnValue(
        mockedMappingResult
      );

      await unitUnderTest.loadWahlvorschlaegeAndErgebnisse();

      unitUnderTest.scorableWahlvorschlaege.value.forEach(
        (wahlvorschlag, index, allWahlvorschlaege) => {
          //check wahlvorschlag is in correct order by ordnungszahl
          if (index < allWahlvorschlaege.length - 1) {
            expect(wahlvorschlag.ordnungszahl).toBeLessThanOrEqual(
              allWahlvorschlaege[index + 1].ordnungszahl
            );
          }

          //check that kandidaten are sorted by listenposition
          wahlvorschlag.scorableKandidaten.forEach(
            (kandidatAndErgebnis, index, allKandidatenAndErgebnisse) => {
              if (index < allKandidatenAndErgebnisse.length - 1) {
                expect(
                  kandidatAndErgebnis.kandidat.listenposition
                ).toBeLessThanOrEqual(
                  allKandidatenAndErgebnisse[index + 1].kandidat.listenposition
                );
              }
            }
          );
        }
      );
    });

    it("should_setScorable_when_wahlvorschlaegeButNoErgebnisseAreGiven", async () => {
      expect(unitUnderTest.scorableWahlvorschlaege.value).toStrictEqual([]);
      expect(unitUnderTest.isLoading.value).toStrictEqual(false);

      const spyOnValueSetterOfIsLoading = spyOn(
        unitUnderTest.isLoading,
        "value",
        "set"
      );

      const mockedWahlvorschlaege = createWahlvorschlaege();
      mockDefinitions.getWahlvorschlaege.mockResolvedValue(
        mockedWahlvorschlaege
      );
      const mockedErgebnisse = prepareErgebnisse().ergebnisse([]).build();
      mockDefinitions.getErgebnisse.mockReturnValue(mockedErgebnisse);

      const mockedMappingResult = createWahlvorschlagWithScorableKandidaten();
      mockDefinitions.mapToWahlvorschlagWithScorableKandidaten.mockReturnValue(
        mockedMappingResult
      );

      await unitUnderTest.loadWahlvorschlaegeAndErgebnisse();

      expect(spyOnValueSetterOfIsLoading.mock.calls).toStrictEqual([
        [true],
        [false],
      ]);

      expect(unitUnderTest.scorableWahlvorschlaege.value).toStrictEqual([
        mockedMappingResult,
        mockedMappingResult,
        mockedMappingResult,
      ]);
      [...mockedWahlvorschlaege.wahlvorschlaege].forEach((wahlvorschlag) => {
        expect(
          mockDefinitions.mapToWahlvorschlagWithScorableKandidaten
        ).toHaveBeenCalledWith(wahlvorschlag, mockedErgebnisse);
      });

      spyOnValueSetterOfIsLoading.mockRestore();
    });

    it("should_updateAndEndWithFalseForIsLoading_when_getWahlvorschlaegeFails", async () => {
      const mockedGetWahlvorschlaegeError = new Error(
        "mocked get wahlvorschlaege failed"
      );
      mockDefinitions.getWahlvorschlaege.mockRejectedValue(
        mockedGetWahlvorschlaegeError
      );

      const spyOnValueSetterOfIsLoading = spyOn(
        unitUnderTest.isLoading,
        "value",
        "set"
      );

      expect(unitUnderTest.isLoading.value).toStrictEqual(false);
      await unitUnderTest.loadWahlvorschlaegeAndErgebnisse();
      expect(unitUnderTest.isLoading.value).toStrictEqual(false);

      expect(spyOnValueSetterOfIsLoading.mock.calls).toStrictEqual([
        [true],
        [false],
      ]);

      spyOnValueSetterOfIsLoading.mockRestore();
    });

    it("should_updateAndEndWithFalseForIsLoading_when_getErgebnisseFails", async () => {
      const mockedGetErgebnisseError = new Error(
        "mocked get ergebnisse failed"
      );
      mockDefinitions.getErgebnisse.mockRejectedValue(mockedGetErgebnisseError);
      mockDefinitions.getWahlvorschlaege.mockResolvedValue(
        createWahlvorschlaege()
      );

      const spyOnValueSetterOfIsLoading = spyOn(
        unitUnderTest.isLoading,
        "value",
        "set"
      );

      expect(unitUnderTest.isLoading.value).toStrictEqual(false);
      await unitUnderTest.loadWahlvorschlaegeAndErgebnisse();
      expect(unitUnderTest.isLoading.value).toStrictEqual(false);

      expect(spyOnValueSetterOfIsLoading.mock.calls).toStrictEqual([
        [true],
        [false],
      ]);

      spyOnValueSetterOfIsLoading.mockRestore();
    });
  });

  describe("saveErgebnisse", () => {});

  function createWahlvorschlaege() {
    return prepareWahlvorschlaege()
      .wahlvorschlaege(
        new Set([
          prepareWahlvorschlag()
            .kandidaten(
              new Set([createKandidat(), createKandidat(), createKandidat()])
            )
            .build(),
          prepareWahlvorschlag()
            .kandidaten(
              new Set([createKandidat(), createKandidat(), createKandidat()])
            )
            .build(),
          prepareWahlvorschlag()
            .kandidaten(
              new Set([createKandidat(), createKandidat(), createKandidat()])
            )
            .build(),
        ])
      )
      .build();
  }

  function createWahlvorschlagWithScorableKandidaten(): WahlvorschlagWithScorableKandidaten {
    return {
      scorableKandidaten: [
        { kandidat: createKandidat(), ergebnis: createErgebnis() },
      ],
      ordnungszahl: generateRandomNumber(3),
      kurzname: generateRandomString(10),
      identifikator: generateRandomString(10),
    };
  }
});
