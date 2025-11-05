import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { useWahlbezirkTestDataFactory } from "@tests/utils/wahlbezirk/WahlbezirkTestDataFactory.ts";
import { usePflegeWaehlerverzeichnisTestDataFactory } from "@tests/utils/wahlhandlung/PflegeWaehlerverzeichnisTestDataFactory.ts";
import { useWahlvorbereitungTestDataFactory } from "@tests/utils/wahlhandlung/WahlvorbereitungTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { ref } from "vue";

import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  postUrnenwahlSchliessungsuhrzeit: vi.fn(),
  postEroeffnungsuhrzeit: vi.fn(),
  postUrnenwahlvorbereitung: vi.fn(),
  postBriefwahlvorbereitung: vi.fn(),
  getUngueltigeWahlscheine: vi.fn(),
  getWaehlerverzeichnis: vi.fn(),
  postWaehlerverzeichnis: vi.fn(),
  getWaehlerverzeichnisNummerOrUndefinedById: vi.fn(),
  getUrnenwahlvorbereitung: vi.fn(),
  getBriefwahlvorbereitung: vi.fn(),
}));

const { createPflegeWaehlerverzeichnis } =
  usePflegeWaehlerverzeichnisTestDataFactory();

const mockedDefaultWaehlerverzeichnis = createPflegeWaehlerverzeichnis();

vi.mock("@/composables/basisdaten/ungueltigeWahlscheineService.ts", () => ({
  useUngueltigeWahlscheineService: () => ({
    getUngueltigeWahlscheine: mockDefinitions.getUngueltigeWahlscheine,
  }),
}));
vi.mock("@/composables/wahlhandlung/wahlvorbereitungService", () => ({
  useWahlvorbereitungService: () => ({
    postUrnenwahlSchliessungsuhrzeit:
      mockDefinitions.postUrnenwahlSchliessungsuhrzeit,
    postEroeffnungsuhrzeit: mockDefinitions.postEroeffnungsuhrzeit,
    postUrnenwahlvorbereitung: mockDefinitions.postUrnenwahlvorbereitung,
    postBriefwahlvorbereitung: mockDefinitions.postBriefwahlvorbereitung,
    getUrnenwahlvorbereitung: mockDefinitions.getUrnenwahlvorbereitung,
    getBriefwahlvorbereitung: mockDefinitions.getBriefwahlvorbereitung,
  }),
}));
vi.mock("@/composables/wahlhandlung/waehlerverzeichnisService.ts", () => ({
  useWaehlerverzeichnisService: () => ({
    createDefaultPflegeWaehlerverzeichnis: () =>
      mockedDefaultWaehlerverzeichnis,
    getWaehlerverzeichnis: mockDefinitions.getWaehlerverzeichnis,
    postWaehlerverzeichnis: mockDefinitions.postWaehlerverzeichnis,
  }),
}));
vi.mock("@/stores/wahlenStore.ts", () => ({
  useWahlenStore: () => ({
    wahlenState: ref({
      wahlen: [prepareWahl().wahlID("wahlID").build()],
    }),
    waehlerverzeichnisActions: {
      getWaehlerverzeichnisNummerOrUndefinedById:
        mockDefinitions.getWaehlerverzeichnisNummerOrUndefinedById,
    },
  }),
}));

const mockedNow = new Date();
const { prepareUser } = useUserTestDataFactory();
const { createUngueltigerWahlschein, prepareUngueltigerWahlschein } =
  useWahlbezirkTestDataFactory();
const { prepareUrnenwahlvorbereitung } = useWahlvorbereitungTestDataFactory();
const { prepareWahl } = useWahlTestDataFactory();

describe("wahlbezirkStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useWahlbezirkStore>;

  beforeEach(() => {
    setActivePinia(createPinia());
    vi.useFakeTimers({
      now: mockedNow,
    });
    unitUnderTest = useWahlbezirkStore();
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  describe("getUngueltigerWahlscheinByWahlscheinnummer", () => {
    it("should_returnUngueltigerWahlschein_when_wahlscheinWithNummerExists", () => {
      unitUnderTest.ungueltigeWahlscheineState.ungueltigeWahlscheine = [
        prepareUngueltigerWahlschein().wahlscheinnummer("1").build(),
        prepareUngueltigerWahlschein().wahlscheinnummer("2").build(),
        prepareUngueltigerWahlschein().wahlscheinnummer("3").build(),
        prepareUngueltigerWahlschein().wahlscheinnummer("4").build(),
      ];

      const result =
        unitUnderTest.ungueltigeWahlscheineActions.getUngueltigerWahlscheinByWahlscheinnummer(
          "2"
        );

      expect(result).toStrictEqual(
        unitUnderTest.ungueltigeWahlscheineState.ungueltigeWahlscheine[1]
      );
    });

    it("should_returnNull_when_wahlscheinWithNummerDoesNotExists", () => {
      unitUnderTest.ungueltigeWahlscheineState.ungueltigeWahlscheine = [
        prepareUngueltigerWahlschein().wahlscheinnummer("1").build(),
        prepareUngueltigerWahlschein().wahlscheinnummer("2").build(),
        prepareUngueltigerWahlschein().wahlscheinnummer("3").build(),
        prepareUngueltigerWahlschein().wahlscheinnummer("4").build(),
      ];

      const result =
        unitUnderTest.ungueltigeWahlscheineActions.getUngueltigerWahlscheinByWahlscheinnummer(
          "5"
        );

      expect(result).toBeNull();
    });

    it("should_returnNull_when_wahlscheineArrayIsEmpty", () => {
      unitUnderTest.ungueltigeWahlscheineState.ungueltigeWahlscheine = [];

      const result =
        unitUnderTest.ungueltigeWahlscheineActions.getUngueltigerWahlscheinByWahlscheinnummer(
          "1"
        );

      expect(result).toBeNull();
    });
  });

  describe("initUngueltigeWahlscheine", () => {
    it.each([{ sendNotification: true }, { sendNotification: false }])(
      'should_callServiceAndSaveResponseWithSendNotification"$sendNotification"_when_currentUserHasWahltagIDAndWahlbezirksArt',
      async (argument) => {
        const wahltagID = "wahltagID";
        const wahlbezirksArt = WahlbezirksArtEnum.UWB;
        useUserStore().setUser(
          prepareUser()
            .wahltagID(wahltagID)
            .wahlbezirksArt(wahlbezirksArt)
            .build()
        );

        await unitUnderTest.ungueltigeWahlscheineActions.initUngueltigeWahlscheine(
          argument.sendNotification
        );

        expect(
          mockDefinitions.getUngueltigeWahlscheine.mock.calls
        ).toStrictEqual([
          [wahltagID, wahlbezirksArt, argument.sendNotification],
        ]);
      }
    );
  });

  describe("loadPflegeWaehlerverzeichnis", () => {
    it("should_loadPflegeWaehlerverzeichnis_when_userHasWaehlerverzeichnisNummer", () => {
      const wahlbezirkID = "wahlbezirkID";
      useUserStore().setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const waehlerverzeichnisNummer = 12;
      mockDefinitions.getWaehlerverzeichnisNummerOrUndefinedById.mockReturnValue(
        waehlerverzeichnisNummer
      );

      unitUnderTest.pflegeWaehlerverzeichnisActions.loadPflegeWaehlerverzeichnis();

      expect(mockDefinitions.getWaehlerverzeichnis.mock.calls).toStrictEqual([
        [wahlbezirkID, waehlerverzeichnisNummer, true],
      ]);
    });

    it("should_notLoadPflegeWaehlerverzeichnis_when_userHasNoWaehlerverzeichnisNummer", () => {
      const wahlbezirkID = "wahlbezirkID";
      useUserStore().setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      mockDefinitions.getWaehlerverzeichnisNummerOrUndefinedById.mockReturnValue(
        undefined
      );

      unitUnderTest.pflegeWaehlerverzeichnisActions.loadPflegeWaehlerverzeichnis();

      expect(
        mockDefinitions.getWaehlerverzeichnis.mock.calls.length
      ).toStrictEqual(0);
    });
  });

  describe("loadUngueltigeWahlscheine", () => {
    it("should_setUngueltigeWahlscheine_when_serviceReturnedUngueltigeWahlscheine", async () => {
      const wahltagID = "wahltagID";
      const wahlbezirksArt = WahlbezirksArtEnum.UWB;
      useUserStore().setUser(
        prepareUser()
          .wahltagID(wahltagID)
          .wahlbezirksArt(wahlbezirksArt)
          .build()
      );

      unitUnderTest.ungueltigeWahlscheineState.ungueltigeWahlscheine = [
        createUngueltigerWahlschein(),
      ];

      const mockedServiceResponse = [
        createUngueltigerWahlschein(),
        createUngueltigerWahlschein(),
      ];
      mockDefinitions.getUngueltigeWahlscheine.mockReturnValue(
        mockedServiceResponse
      );

      await unitUnderTest.ungueltigeWahlscheineActions.loadUngueltigeWahlscheine();

      expect(
        unitUnderTest.ungueltigeWahlscheineState.ungueltigeWahlscheine
      ).toStrictEqual(mockedServiceResponse);
      expect(mockDefinitions.getUngueltigeWahlscheine.mock.calls).toStrictEqual(
        [[wahltagID, wahlbezirksArt, true]]
      );
    });

    it("should_clearUngueltigeWahlscheine_when_serviceThrowError", async () => {
      const wahltagID = "wahltagID";
      const wahlbezirksArt = WahlbezirksArtEnum.UWB;
      useUserStore().setUser(
        prepareUser()
          .wahltagID(wahltagID)
          .wahlbezirksArt(wahlbezirksArt)
          .build()
      );

      unitUnderTest.ungueltigeWahlscheineState.ungueltigeWahlscheine = [
        createUngueltigerWahlschein(),
      ];

      mockDefinitions.getUngueltigeWahlscheine.mockRejectedValue(
        new Error("mocked service error")
      );

      await unitUnderTest.ungueltigeWahlscheineActions.loadUngueltigeWahlscheine();

      expect(
        unitUnderTest.ungueltigeWahlscheineState.ungueltigeWahlscheine
      ).toStrictEqual([]);
    });

    it("should_updateIsLoadingFlag_when_calledAndSucceeded", async () => {
      const wahltagID = "wahltagID";
      const wahlbezirksArt = WahlbezirksArtEnum.UWB;
      useUserStore().setUser(
        prepareUser()
          .wahltagID(wahltagID)
          .wahlbezirksArt(wahlbezirksArt)
          .build()
      );

      const timeout = 100;
      mockDefinitions.getUngueltigeWahlscheine.mockReturnValue(
        new Promise((resolve) => {
          setTimeout(() => {
            resolve([createUngueltigerWahlschein()]);
          }, timeout);
        })
      );

      expect(
        unitUnderTest.ungueltigeWahlscheineState.ungueltigeWahlscheineIsLoading
      ).toStrictEqual(false);
      const promise =
        unitUnderTest.ungueltigeWahlscheineActions.loadUngueltigeWahlscheine();

      expect(
        unitUnderTest.ungueltigeWahlscheineState.ungueltigeWahlscheineIsLoading
      ).toStrictEqual(true);
      vi.advanceTimersByTime(timeout);
      await promise;
      expect(
        unitUnderTest.ungueltigeWahlscheineState.ungueltigeWahlscheineIsLoading
      ).toStrictEqual(false);
    });

    it("should_updateIsLoadingFlag_when_calledAndFailed", async () => {
      const wahltagID = "wahltagID";
      const wahlbezirksArt = WahlbezirksArtEnum.UWB;
      useUserStore().setUser(
        prepareUser()
          .wahltagID(wahltagID)
          .wahlbezirksArt(wahlbezirksArt)
          .build()
      );

      const timeout = 100;
      mockDefinitions.getUngueltigeWahlscheine.mockReturnValue(
        new Promise((resolve, reject) => {
          setTimeout(() => {
            reject(new Error("mocked service error"));
          }, timeout);
        })
      );

      expect(
        unitUnderTest.ungueltigeWahlscheineState.ungueltigeWahlscheineIsLoading
      ).toStrictEqual(false);
      const promise =
        unitUnderTest.ungueltigeWahlscheineActions.loadUngueltigeWahlscheine();

      expect(
        unitUnderTest.ungueltigeWahlscheineState.ungueltigeWahlscheineIsLoading
      ).toStrictEqual(true);
      vi.advanceTimersByTime(timeout);
      await promise;
      expect(
        unitUnderTest.ungueltigeWahlscheineState.ungueltigeWahlscheineIsLoading
      ).toStrictEqual(false);
    });

    it.each([true, false])(
      "should_resetAndSetFailedFlagToTrue_when_serviceThrowErrorAndFlagWasInitially'%o'",
      async (initValueForUngueltigeWahlscheineLoadingFailed) => {
        const wahltagID = "wahltagID";
        const wahlbezirksArt = WahlbezirksArtEnum.UWB;
        useUserStore().setUser(
          prepareUser()
            .wahltagID(wahltagID)
            .wahlbezirksArt(wahlbezirksArt)
            .build()
        );
        unitUnderTest.ungueltigeWahlscheineState.ungueltigeWahlscheineLoadingFailed =
          initValueForUngueltigeWahlscheineLoadingFailed;

        mockDefinitions.getUngueltigeWahlscheine.mockRejectedValue(
          new Error("mocked service error")
        );

        await unitUnderTest.ungueltigeWahlscheineActions.loadUngueltigeWahlscheine();

        expect(
          unitUnderTest.ungueltigeWahlscheineState
            .ungueltigeWahlscheineLoadingFailed
        ).toStrictEqual(true);
      }
    );

    it.each([true, false])(
      "should_resetAndSetFailedFlagToFalse_when_serviceSucceededAndFlagWasInitially'%o'",
      async (initValueForUngueltigeWahlscheineLoadingFailed) => {
        const wahltagID = "wahltagID";
        const wahlbezirksArt = WahlbezirksArtEnum.UWB;
        useUserStore().setUser(
          prepareUser()
            .wahltagID(wahltagID)
            .wahlbezirksArt(wahlbezirksArt)
            .build()
        );
        unitUnderTest.ungueltigeWahlscheineState.ungueltigeWahlscheineLoadingFailed =
          initValueForUngueltigeWahlscheineLoadingFailed;

        mockDefinitions.getUngueltigeWahlscheine.mockResolvedValueOnce([
          createUngueltigerWahlschein(),
        ]);

        await unitUnderTest.ungueltigeWahlscheineActions.loadUngueltigeWahlscheine();

        expect(
          unitUnderTest.ungueltigeWahlscheineState
            .ungueltigeWahlscheineLoadingFailed
        ).toStrictEqual(false);
      }
    );
  });

  describe("sendEroeffnungsuhrzeit", () => {
    it("should_updateIsSavingAndSetSentValue_when_succeeded", async () => {
      const eroeffnungsuhrzeit = mockedNow;
      unitUnderTest.eroeffnungsuhrzeitState.eroeffnungsuhrzeit =
        eroeffnungsuhrzeit;

      const wahlbezirkID = "wahlbezirkID";
      useUserStore().setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const timeout = 100;
      mockDefinitions.postEroeffnungsuhrzeit.mockReturnValue(
        new Promise((resolve) => {
          setTimeout(() => {
            resolve({});
          }, timeout);
        })
      );

      expect(
        unitUnderTest.eroeffnungsuhrzeitState.eroeffnungsuhrzeitIsSaving
      ).toStrictEqual(false);
      const sendEroeffnungsuhrzeitPromise =
        unitUnderTest.eroeffnungsuhrzeitActions.sendEroeffnungsuhrzeit();
      expect(
        unitUnderTest.eroeffnungsuhrzeitState.eroeffnungsuhrzeitIsSaving
      ).toStrictEqual(true);

      vi.advanceTimersByTime(timeout);
      await sendEroeffnungsuhrzeitPromise;

      expect(mockDefinitions.postEroeffnungsuhrzeit.mock.calls).toStrictEqual([
        [wahlbezirkID, eroeffnungsuhrzeit],
      ]);
      expect(
        unitUnderTest.eroeffnungsuhrzeitState.eroeffnungsuhrzeitIsSaving
      ).toStrictEqual(false);
      expect(
        unitUnderTest.eroeffnungsuhrzeitState.eroeffnungsuhrzeit?.getTime()
      ).toStrictEqual(eroeffnungsuhrzeit.getTime());
    });

    it("should_notCallService_when_noEroeffnungsuhrzeitIsGiven", async () => {
      unitUnderTest.eroeffnungsuhrzeitState.eroeffnungsuhrzeit = undefined;
      useUserStore().setUser(
        prepareUser().wahlbezirkID("wahlbezirkID").build()
      );

      expect(
        unitUnderTest.eroeffnungsuhrzeitState.eroeffnungsuhrzeitIsSaving
      ).toStrictEqual(false);
      const sendEroeffnungsuhrzeitPromise =
        unitUnderTest.eroeffnungsuhrzeitActions.sendEroeffnungsuhrzeit();

      vi.advanceTimersByTime(100);
      await sendEroeffnungsuhrzeitPromise;

      expect(
        mockDefinitions.postEroeffnungsuhrzeit.mock.calls.length
      ).toStrictEqual(0);
      expect(
        unitUnderTest.eroeffnungsuhrzeitState.eroeffnungsuhrzeitIsSaving
      ).toStrictEqual(false);
    });
  });

  describe("sendPflegeWaehlerverzeichnis", () => {
    it("should_useServiceAndUpdateIsSaving_when_waehlerverzeichnisNummerInUserIsGiven", async () => {
      const userWahlbezirkID = "wahlbezirkID";
      useUserStore().setUser(
        prepareUser().wahlbezirkID(userWahlbezirkID).build()
      );

      const pflegeWaehlerverzeichnis = createPflegeWaehlerverzeichnis();
      unitUnderTest.pflegeWaehlerverzeichnisState.pflegeWaehlerverzeichnis =
        pflegeWaehlerverzeichnis;

      const mockedWaehlerverzeichnisNummer = "wvzNummer";
      mockDefinitions.getWaehlerverzeichnisNummerOrUndefinedById.mockReturnValue(
        mockedWaehlerverzeichnisNummer
      );

      const timeout = 100;
      mockDefinitions.postWaehlerverzeichnis.mockReturnValue(
        new Promise((resolve) => {
          setTimeout(() => {
            resolve({});
          }, timeout);
        })
      );

      expect(
        unitUnderTest.pflegeWaehlerverzeichnisState
          .pflegeWaehlerverzeichnisIsSaving
      ).toStrictEqual(false);
      const sendPflegeWaehlerverzeichnisPromise =
        unitUnderTest.pflegeWaehlerverzeichnisActions.sendPflegeWaehlerverzeichnis();
      expect(
        unitUnderTest.pflegeWaehlerverzeichnisState
          .pflegeWaehlerverzeichnisIsSaving
      ).toStrictEqual(true);

      vi.advanceTimersByTime(timeout);
      await sendPflegeWaehlerverzeichnisPromise;

      expect(
        unitUnderTest.pflegeWaehlerverzeichnisState
          .pflegeWaehlerverzeichnisIsSaving
      ).toStrictEqual(false);
      expect(mockDefinitions.postWaehlerverzeichnis.mock.calls).toStrictEqual([
        [
          userWahlbezirkID,
          mockedWaehlerverzeichnisNummer,
          pflegeWaehlerverzeichnis,
        ],
      ]);
    });

    it("should_useServiceAndUpdateIsSaving_when_waehlerverzeichnisNummerInUserIsGivenAndServiceThrewException", async () => {
      const userWahlbezirkID = "wahlbezirkID";
      useUserStore().setUser(
        prepareUser().wahlbezirkID(userWahlbezirkID).build()
      );

      const pflegeWaehlerverzeichnis = createPflegeWaehlerverzeichnis();
      unitUnderTest.pflegeWaehlerverzeichnisState.pflegeWaehlerverzeichnis =
        pflegeWaehlerverzeichnis;

      const mockedWaehlerverzeichnisNummer = "wvzNummer";
      mockDefinitions.getWaehlerverzeichnisNummerOrUndefinedById.mockReturnValue(
        mockedWaehlerverzeichnisNummer
      );

      const timeout = 100;
      mockDefinitions.postWaehlerverzeichnis.mockReturnValue(
        new Promise((resolve, reject) => {
          setTimeout(() => {
            reject("mocked service call failed");
          }, timeout);
        })
      );

      expect(
        unitUnderTest.pflegeWaehlerverzeichnisState
          .pflegeWaehlerverzeichnisIsSaving
      ).toStrictEqual(false);
      const sendPflegeWaehlerverzeichnisPromise =
        unitUnderTest.pflegeWaehlerverzeichnisActions.sendPflegeWaehlerverzeichnis();
      expect(
        unitUnderTest.pflegeWaehlerverzeichnisState
          .pflegeWaehlerverzeichnisIsSaving
      ).toStrictEqual(true);

      vi.advanceTimersByTime(timeout);
      await expect(sendPflegeWaehlerverzeichnisPromise).rejects.toThrow();

      expect(
        unitUnderTest.pflegeWaehlerverzeichnisState
          .pflegeWaehlerverzeichnisIsSaving
      ).toStrictEqual(false);
      expect(mockDefinitions.postWaehlerverzeichnis.mock.calls).toStrictEqual([
        [
          userWahlbezirkID,
          mockedWaehlerverzeichnisNummer,
          pflegeWaehlerverzeichnis,
        ],
      ]);
    });

    it("should_notCallService_when_waehlerverzeichnisNummerInUserIsNotGiven", async () => {
      mockDefinitions.getWaehlerverzeichnisNummerOrUndefinedById.mockReturnValue(
        undefined
      );

      await unitUnderTest.pflegeWaehlerverzeichnisActions.sendPflegeWaehlerverzeichnis();

      expect(
        mockDefinitions.postWaehlerverzeichnis.mock.calls.length
      ).toStrictEqual(0);
    });
  });

  describe("sendSchliessungsuhrzeit", () => {
    it("should_updateIsSavingAndSetSentValue_when_succeeded", async () => {
      const schliessungsuhrzeit = mockedNow;
      unitUnderTest.schliessungsuhrzeitState.schliessungsuhrzeit =
        schliessungsuhrzeit;

      const wahlbezirkID = "wahlbezirkID";
      useUserStore().setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const timeout = 100;
      mockDefinitions.postUrnenwahlSchliessungsuhrzeit.mockReturnValue(
        new Promise((resolve) => {
          setTimeout(() => {
            resolve({});
          }, timeout);
        })
      );

      expect(
        unitUnderTest.schliessungsuhrzeitState.schliessungsuhrzeitIsSaving
      ).toStrictEqual(false);
      const sendSchliessungsuhrzeitPromise =
        unitUnderTest.schliessungsuhrzeitActions.sendSchliessungsuhrzeit();
      expect(
        unitUnderTest.schliessungsuhrzeitState.schliessungsuhrzeitIsSaving
      ).toStrictEqual(true);

      vi.advanceTimersByTime(timeout);
      await sendSchliessungsuhrzeitPromise;

      expect(
        mockDefinitions.postUrnenwahlSchliessungsuhrzeit.mock.calls
      ).toStrictEqual([[wahlbezirkID, schliessungsuhrzeit]]);
      expect(
        unitUnderTest.schliessungsuhrzeitState.schliessungsuhrzeitIsSaving
      ).toStrictEqual(false);
      expect(
        unitUnderTest.schliessungsuhrzeitState.schliessungsuhrzeit?.getTime()
      ).toStrictEqual(schliessungsuhrzeit.getTime());
    });

    it("should_notUpdateSchliessungsUhrzeitSent_when_postUrnenwahlSchliessungsuhrzeitFails", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      unitUnderTest.schliessungsuhrzeitState.schliessungsuhrzeit = mockedNow;

      const mockedError = new Error("Speicherfehler!");
      mockDefinitions.postUrnenwahlSchliessungsuhrzeit.mockImplementationOnce(
        () => {
          throw mockedError;
        }
      );

      try {
        await unitUnderTest.schliessungsuhrzeitActions.sendSchliessungsuhrzeit();
      } catch (error) {
        expect(error).equals(mockedError);
        expect(
          unitUnderTest.schliessungsuhrzeitState.schliessungsuhrzeitSent
        ).toBe(undefined);
        expect(
          mockDefinitions.postUrnenwahlSchliessungsuhrzeit
        ).toHaveBeenCalledWith(wahlbezirkID, mockedNow);
      }
    });
  });

  describe("sendUrnenwahlvorbereitung", () => {
    it("should_sendUrnenwahlvorbereitungAndUpdateUrnenwahlVorbereitung_when_wahlbezirkIDIsGiven", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const mockedUrnenwahlvorbereitung = {
        wahlbezirkID: "wahlbezirkID1",
        anzahlWahltische: 1,
        anzahlNebenraeume: 0,
        anzahlWahlkabinen: 0,
        urneVersiegelt: true,
        urnenAnzahl: [
          { wahlID: "wahlID1", anzahl: 1 },
          { wahlID: "wahlID2", anzahl: 1 },
        ],
      };
      unitUnderTest.urnenwahlVorbereitungState.urnenwahlVorbereitung =
        mockedUrnenwahlvorbereitung;

      await unitUnderTest.urnenwahlVorbereitungActions.sendUrnenwahlvorbereitung();

      expect(mockDefinitions.postUrnenwahlvorbereitung).toHaveBeenCalledWith(
        wahlbezirkID,
        mockedUrnenwahlvorbereitung
      );
    });

    it("should_notUpdateUrnenwahlVorbereitung_when_postUrnenwahlvorbereitungFails", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const mockedUrnenwahlvorbereitung = {
        wahlbezirkID: "wahlbezirkID1",
        anzahlWahltische: 1,
        anzahlNebenraeume: 0,
        anzahlWahlkabinen: 0,
        urneVersiegelt: true,
        urnenAnzahl: [
          { wahlID: "wahlID1", anzahl: 1 },
          { wahlID: "wahlID2", anzahl: 1 },
        ],
      };
      unitUnderTest.urnenwahlVorbereitungState.urnenwahlVorbereitung =
        mockedUrnenwahlvorbereitung;

      const mockedError = new Error("Speicherfehler!");
      mockDefinitions.postUrnenwahlvorbereitung.mockImplementationOnce(() => {
        throw mockedError;
      });

      try {
        await unitUnderTest.urnenwahlVorbereitungActions.sendUrnenwahlvorbereitung();
      } catch (error) {
        expect(error).equals(mockedError);
        expect(mockDefinitions.postUrnenwahlvorbereitung).toHaveBeenCalledWith(
          wahlbezirkID,
          mockedUrnenwahlvorbereitung
        );
      }
    });
  });

  describe("initUrnenwahlvorbereitung", () => {
    it.each([{ sendNotification: true }, { sendNotification: false }])(
      'should_getUrnenwahlvorbereitungWithSendNotification"$sendNotification"_when_notificationParameterIsUsed',
      async (argument) => {
        const userStore = useUserStore();
        const wahlbezirkID = "wahlbezirkID";
        userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

        mockDefinitions.getUrnenwahlvorbereitung.mockReturnValue(
          prepareUrnenwahlvorbereitung().build()
        );

        unitUnderTest.urnenwahlVorbereitungActions.initUrnenwahlvorbereitung(
          argument.sendNotification
        );

        expect(
          mockDefinitions.getUrnenwahlvorbereitung.mock.calls
        ).toStrictEqual([[wahlbezirkID, argument.sendNotification]]);
      }
    );

    it("should_initUrnenwahlvorbereitung_when_urnenAnzahlIsEmpty", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      mockDefinitions.getUrnenwahlvorbereitung.mockReturnValue(
        prepareUrnenwahlvorbereitung().urnenAnzahl([]).build()
      );

      await unitUnderTest.urnenwahlVorbereitungActions.initUrnenwahlvorbereitung();

      expect(
        unitUnderTest.urnenwahlVorbereitungState.urnenwahlVorbereitung
          .urnenAnzahl.length
      ).toBeGreaterThan(0);
      expect(
        unitUnderTest.urnenwahlVorbereitungState.urnenwahlVorbereitung
          .urnenAnzahl
      ).toStrictEqual([
        {
          wahlID: "wahlID",
          anzahl: null,
        },
      ]);
    });

    it("should_initUrnenwahlvorbereitung_when_urnenAnzahlIsNotEmpty", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const urnenAnzahl = [
        {
          wahlID: "wahlID",
          anzahl: 5,
        },
      ];

      mockDefinitions.getUrnenwahlvorbereitung.mockReturnValue(
        prepareUrnenwahlvorbereitung().urnenAnzahl(urnenAnzahl).build()
      );

      await unitUnderTest.urnenwahlVorbereitungActions.initUrnenwahlvorbereitung();

      expect(
        unitUnderTest.urnenwahlVorbereitungState.urnenwahlVorbereitung
          .urnenAnzahl.length
      ).toBeGreaterThan(0);
      expect(
        unitUnderTest.urnenwahlVorbereitungState.urnenwahlVorbereitung
          .urnenAnzahl
      ).toStrictEqual(urnenAnzahl);
    });
  });

  describe("sendBriefwahlvorbereitung", () => {
    it("should_sendBriefwahlvorbereitungAndUpdateBriefwahlVorbereitung_when_wahlbezirkIDIsGiven", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const timeout = 100;
      mockDefinitions.postBriefwahlvorbereitung.mockReturnValue(
        new Promise((resolve) => {
          setTimeout(() => {
            resolve({});
          }, timeout);
        })
      );

      const mockedBriefwahlvorbereitung = {
        wahlbezirkID: "wahlbezirkID1",
        urneVersiegelt: true,
        urnenAnzahl: [
          { wahlID: "wahlID1", anzahl: 1 },
          { wahlID: "wahlID2", anzahl: 1 },
        ],
      };
      unitUnderTest.briefwahlVorbereitungState.briefwahlVorbereitung =
        mockedBriefwahlvorbereitung;

      expect(
        unitUnderTest.briefwahlVorbereitungState.briefWahlVorbereitungIsSaving
      ).toStrictEqual(false);
      const sendBriefwahlvorbereitungPromise =
        unitUnderTest.briefwahlVorbereitungActions.sendBriefwahlvorbereitung();
      expect(
        unitUnderTest.briefwahlVorbereitungState.briefWahlVorbereitungIsSaving
      ).toStrictEqual(true);

      vi.advanceTimersByTime(timeout);
      await sendBriefwahlvorbereitungPromise;

      expect(mockDefinitions.postBriefwahlvorbereitung).toHaveBeenCalledWith(
        wahlbezirkID,
        mockedBriefwahlvorbereitung
      );
      expect(
        unitUnderTest.briefwahlVorbereitungState.briefWahlVorbereitungIsSaving
      ).toStrictEqual(false);
      expect(
        unitUnderTest.briefwahlVorbereitungState.briefwahlVorbereitung
      ).toEqual(mockedBriefwahlvorbereitung);
    });

    it("should_notUpdateBriefwahlVorbereitung_when_postBriefwahlvorbereitungFails", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const mockedBriefwahlvorbereitung = {
        wahlbezirkID: "wahlbezirkID1",
        urneVersiegelt: true,
        urnenAnzahl: [
          { wahlID: "wahlID1", anzahl: 1 },
          { wahlID: "wahlID2", anzahl: 1 },
        ],
      };
      unitUnderTest.briefwahlVorbereitungState.briefwahlVorbereitung =
        mockedBriefwahlvorbereitung;

      const mockedError = new Error("Speicherfehler!");
      mockDefinitions.postBriefwahlvorbereitung.mockImplementationOnce(() => {
        throw mockedError;
      });

      try {
        await unitUnderTest.briefwahlVorbereitungActions.sendBriefwahlvorbereitung();
      } catch (error) {
        expect(error).equals(mockedError);
        expect(mockDefinitions.postBriefwahlvorbereitung).toHaveBeenCalledWith(
          wahlbezirkID,
          mockedBriefwahlvorbereitung
        );
      }
    });
  });

  describe("initBriefwahlvorbereitung", () => {
    it.each([{ sendNotification: true }, { sendNotification: false }])(
      'should_getBriefwahlvorbereitungWithSendNotification"$sendNotification"_when_notificationParameterIsUsed',
      async (argument) => {
        const userStore = useUserStore();
        const wahlbezirkID = "wahlbezirkID";
        userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

        mockDefinitions.getBriefwahlvorbereitung.mockReturnValue(
          prepareUrnenwahlvorbereitung().build()
        );

        unitUnderTest.briefwahlVorbereitungActions.initBriefwahlvorbereitung(
          argument.sendNotification
        );

        expect(
          mockDefinitions.getBriefwahlvorbereitung.mock.calls
        ).toStrictEqual([[wahlbezirkID, argument.sendNotification]]);
      }
    );

    it("should_initBriefwahlvorbereitung_when_urnenAnzahlIsEmpty", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      mockDefinitions.getBriefwahlvorbereitung.mockReturnValue(
        prepareUrnenwahlvorbereitung().urnenAnzahl([]).build()
      );

      await unitUnderTest.briefwahlVorbereitungActions.initBriefwahlvorbereitung();

      expect(
        unitUnderTest.briefwahlVorbereitungState.briefwahlVorbereitung
          .urnenAnzahl.length
      ).toBeGreaterThan(0);
      expect(
        unitUnderTest.briefwahlVorbereitungState.briefwahlVorbereitung
          .urnenAnzahl
      ).toStrictEqual([
        {
          wahlID: "wahlID",
          anzahl: null,
        },
      ]);
    });

    it("should_initBriefwahlvorbereitung_when_urnenAnzahlIsNotEmpty", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const urnenAnzahl = [
        {
          wahlID: "wahlID",
          anzahl: 5,
        },
      ];

      mockDefinitions.getBriefwahlvorbereitung.mockReturnValue(
        prepareUrnenwahlvorbereitung().urnenAnzahl(urnenAnzahl).build()
      );

      await unitUnderTest.briefwahlVorbereitungActions.initBriefwahlvorbereitung();

      expect(
        unitUnderTest.briefwahlVorbereitungState.briefwahlVorbereitung
          .urnenAnzahl.length
      ).toBeGreaterThan(0);
      expect(
        unitUnderTest.briefwahlVorbereitungState.briefwahlVorbereitung
          .urnenAnzahl
      ).toStrictEqual(urnenAnzahl);
    });
  });
});
