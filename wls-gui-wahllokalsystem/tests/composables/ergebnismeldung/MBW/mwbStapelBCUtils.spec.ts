import type { WahlvorschlagWithKandidatenErgebnissen } from "@/types/ergebnisermittlung/WahlvorschlagWithKandidatenErgebnissen.ts";

import { spyOn } from "@storybook/test";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useErgebnisseTestDataFactory } from "@tests/utils/ergebnismeldung/common/ergebnisseTestDataFactory.ts";
import { useWahlvorschlaegeTestDataFactory } from "@tests/utils/wahlvorschlaege/WahlvorschlaegeTestDataFactory.ts";
import {
  afterAll,
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";

import { useMwbStapelBCUtils } from "@/composables/ergebnismeldung/MBW/mwbStapelBCUtils.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getErgebnisse: vi.fn(),
  postErgebnisse: vi.fn(),
  getWahlvorschlaege: vi.fn(),
  mapToWahlvorschlagWithKandidatenErgebnissen: vi.fn(),
  mapToErgebnisse: vi.fn(),
}));

vi.mock(
  "@/composables/ergebnismeldung/common/wahlvorschlagWithKandidatenErgebnissenMapper.ts",
  () => ({
    useWahlvorschlagWithKandidatenErgebnissenMapper: () => ({
      toWahlvorschlagWithKandidatenErgebnissen:
        mockDefinitions.mapToWahlvorschlagWithKandidatenErgebnissen,
      toErgebnisse: mockDefinitions.mapToErgebnisse,
    }),
  })
);

vi.mock("@/composables/ergebnismeldung/common/ergebnisService.ts", () => ({
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
const { createErgebnis, createErgebnisse, prepareErgebnis, prepareErgebnisse } =
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
    it("should_setWahlvorschlagWithKandidatenErgebnissen_when_wahlvorschlaegeAndErgebnisseAreGiven", async () => {
      expect(
        unitUnderTest.wahlvorschlaegeWithKandidatenErgebnissen.value
      ).toStrictEqual([]);
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

      const mockedMappingResult =
        createWahlvorschlagWithKandidatenErgebnissen();
      mockDefinitions.mapToWahlvorschlagWithKandidatenErgebnissen.mockReturnValue(
        mockedMappingResult
      );

      await unitUnderTest.loadWahlvorschlaegeAndErgebnisse();

      expect(spyOnValueSetterOfIsLoading.mock.calls).toStrictEqual([
        [true],
        [false],
      ]);

      expect(
        unitUnderTest.wahlvorschlaegeWithKandidatenErgebnissen.value
      ).toStrictEqual([
        mockedMappingResult,
        mockedMappingResult,
        mockedMappingResult,
      ]);
      [...mockedWahlvorschlaege.wahlvorschlaege].forEach((wahlvorschlag) => {
        expect(
          mockDefinitions.mapToWahlvorschlagWithKandidatenErgebnissen
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

      const mockedMappingResult =
        createWahlvorschlagWithKandidatenErgebnissen();
      mockDefinitions.mapToWahlvorschlagWithKandidatenErgebnissen.mockReturnValue(
        mockedMappingResult
      );

      await unitUnderTest.loadWahlvorschlaegeAndErgebnisse();

      unitUnderTest.wahlvorschlaegeWithKandidatenErgebnissen.value.forEach(
        (wahlvorschlag, index, allWahlvorschlaege) => {
          //check wahlvorschlag is in correct order by ordnungszahl
          if (index < allWahlvorschlaege.length - 1) {
            expect(wahlvorschlag.ordnungszahl).lessThanOrEqual(
              // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
              allWahlvorschlaege[index + 1]!.ordnungszahl
            );
          }

          //check that kandidaten are sorted by listenposition
          wahlvorschlag.kandidatenErgebnisse.forEach(
            (kandidatAndErgebnis, index, allKandidatenAndErgebnisse) => {
              if (index < allKandidatenAndErgebnisse.length - 1) {
                expect(
                  kandidatAndErgebnis.kandidat.listenposition
                ).lessThanOrEqual(
                  // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
                  allKandidatenAndErgebnisse[index + 1]!.kandidat.listenposition
                );
              }
            }
          );
        }
      );
    });

    it("should_setWahlvorschlaegeWithKandidatenErgebnisse_when_wahlvorschlaegeButNoErgebnisseAreGiven", async () => {
      expect(
        unitUnderTest.wahlvorschlaegeWithKandidatenErgebnissen.value
      ).toStrictEqual([]);
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

      const mockedMappingResult =
        createWahlvorschlagWithKandidatenErgebnissen();
      mockDefinitions.mapToWahlvorschlagWithKandidatenErgebnissen.mockReturnValue(
        mockedMappingResult
      );

      await unitUnderTest.loadWahlvorschlaegeAndErgebnisse();

      expect(spyOnValueSetterOfIsLoading.mock.calls).toStrictEqual([
        [true],
        [false],
      ]);

      expect(
        unitUnderTest.wahlvorschlaegeWithKandidatenErgebnissen.value
      ).toStrictEqual([
        mockedMappingResult,
        mockedMappingResult,
        mockedMappingResult,
      ]);
      [...mockedWahlvorschlaege.wahlvorschlaege].forEach((wahlvorschlag) => {
        expect(
          mockDefinitions.mapToWahlvorschlagWithKandidatenErgebnissen
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

  describe("saveErgebnisse", () => {
    it("should_sendErgebnisseWithValue_when_ergebnisseAreGiven", async () => {
      const ergebnisToSave1 = createErgebnis();
      const ergebnisToSave2 = createErgebnis();
      const ergebnisToSave3 = createErgebnis();
      const ergebnisToSave4 = createErgebnis();
      unitUnderTest.wahlvorschlaegeWithKandidatenErgebnissen.value = [
        {
          kandidatenErgebnisse: [
            { kandidat: createKandidat(), ergebnis: ergebnisToSave1 },
            { kandidat: createKandidat(), ergebnis: ergebnisToSave2 },
            {
              kandidat: createKandidat(),
              ergebnis: prepareErgebnis().ergebnis(null).build(),
            },
            { kandidat: createKandidat(), ergebnis: ergebnisToSave3 },
          ],
          ordnungszahl: generateRandomNumber(2),
          identifikator: generateRandomString(10),
          kurzname: generateRandomString(10),
        },
        {
          kandidatenErgebnisse: [
            {
              kandidat: createKandidat(),
              ergebnis: prepareErgebnis().ergebnis(null).build(),
            },
            { kandidat: createKandidat(), ergebnis: ergebnisToSave4 },
          ],
          ordnungszahl: generateRandomNumber(2),
          identifikator: generateRandomString(10),
          kurzname: generateRandomString(10),
        },
      ];

      const mockedMappedErgebnisse = createErgebnisse();
      mockDefinitions.mapToErgebnisse.mockReturnValue(mockedMappedErgebnisse);

      await unitUnderTest.saveErgebnisse();

      expect(mockDefinitions.mapToErgebnisse).toHaveBeenCalledWith(
        [ergebnisToSave1, ergebnisToSave2, ergebnisToSave3, ergebnisToSave4],
        wahlbezirkID,
        wahlID,
        StapelArtEnum.MbwBC
      );
    });

    it("should_updateIsSaving_when_callIsSuccessful", async () => {
      const spyOnIsSavingValueSetter = spyOn(
        unitUnderTest.isSaving,
        "value",
        "set"
      );

      expect(unitUnderTest.isSaving.value).toStrictEqual(false);
      await unitUnderTest.saveErgebnisse();

      expect(unitUnderTest.isSaving.value).toStrictEqual(false);
      expect(spyOnIsSavingValueSetter.mock.calls).toStrictEqual([
        [true],
        [false],
      ]);

      spyOnIsSavingValueSetter.mockRestore();
    });

    it("should_updateIsSaving_when_callFailed", async () => {
      const spyOnIsSavingValueSetter = spyOn(
        unitUnderTest.isSaving,
        "value",
        "set"
      );

      mockDefinitions.postErgebnisse.mockRejectedValue(
        new Error("mocked post ergebnisse failed")
      );

      expect(unitUnderTest.isSaving.value).toStrictEqual(false);
      await unitUnderTest.saveErgebnisse();

      expect(unitUnderTest.isSaving.value).toStrictEqual(false);
      expect(spyOnIsSavingValueSetter.mock.calls).toStrictEqual([
        [true],
        [false],
      ]);

      spyOnIsSavingValueSetter.mockRestore();
    });
  });

  function createWahlvorschlaege() {
    return prepareWahlvorschlaege()
      .wahlvorschlaege([
        prepareWahlvorschlag()
          .kandidaten([createKandidat(), createKandidat(), createKandidat()])
          .build(),
        prepareWahlvorschlag()
          .kandidaten([createKandidat(), createKandidat(), createKandidat()])
          .build(),
        prepareWahlvorschlag()
          .kandidaten([createKandidat(), createKandidat(), createKandidat()])
          .build(),
      ])
      .build();
  }

  function createWahlvorschlagWithKandidatenErgebnissen(): WahlvorschlagWithKandidatenErgebnissen {
    return {
      kandidatenErgebnisse: [
        { kandidat: createKandidat(), ergebnis: createErgebnis() },
      ],
      ordnungszahl: generateRandomNumber(3),
      kurzname: generateRandomString(10),
      identifikator: generateRandomString(10),
    };
  }
});
