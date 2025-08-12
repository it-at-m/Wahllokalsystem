import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useWahlbezirkTestDataFactory } from "@tests/utils/wahlbezirk/WahlbezirkTestDataFactory.ts";
import { usePflegeWaehlerverzeichnisTestDataFactory } from "@tests/utils/wahlhandlung/PflegeWaehlerverzeichnisTestDataFactory.ts";
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
  mockedWahlen: vi.fn(),
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
    wahlen: ref(mockDefinitions.mockedWahlen),
    getWaehlerverzeichnisNummerOrUndefinedById:
      mockDefinitions.getWaehlerverzeichnisNummerOrUndefinedById,
  }),
}));

const mockedNow = new Date();
const { prepareUser } = useUserTestDataFactory();
const { createUngueltigerWahlschein, prepareUngueltigerWahlschein } =
  useWahlbezirkTestDataFactory();

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
      unitUnderTest.ungueltigeWahlscheine = [
        prepareUngueltigerWahlschein().wahlscheinnummer("1").build(),
        prepareUngueltigerWahlschein().wahlscheinnummer("2").build(),
        prepareUngueltigerWahlschein().wahlscheinnummer("3").build(),
        prepareUngueltigerWahlschein().wahlscheinnummer("4").build(),
      ];

      const result =
        unitUnderTest.getUngueltigerWahlscheinByWahlscheinnummer("2");

      expect(result).toStrictEqual(unitUnderTest.ungueltigeWahlscheine[1]);
    });

    it("should_returnNull_when_wahlscheinWithNummerDoesNotExists", () => {
      unitUnderTest.ungueltigeWahlscheine = [
        prepareUngueltigerWahlschein().wahlscheinnummer("1").build(),
        prepareUngueltigerWahlschein().wahlscheinnummer("2").build(),
        prepareUngueltigerWahlschein().wahlscheinnummer("3").build(),
        prepareUngueltigerWahlschein().wahlscheinnummer("4").build(),
      ];

      const result =
        unitUnderTest.getUngueltigerWahlscheinByWahlscheinnummer("5");

      expect(result).toBeNull();
    });

    it("should_returnNull_when_wahlscheineArrayIsEmpty", () => {
      unitUnderTest.ungueltigeWahlscheine = [];

      const result =
        unitUnderTest.getUngueltigerWahlscheinByWahlscheinnummer("1");

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

        await unitUnderTest.initUngueltigeWahlscheine(
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

      unitUnderTest.loadPflegeWaehlerverzeichnis();

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

      unitUnderTest.loadPflegeWaehlerverzeichnis();

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

      unitUnderTest.ungueltigeWahlscheine = [createUngueltigerWahlschein()];

      const mockedServiceResponse = [
        createUngueltigerWahlschein(),
        createUngueltigerWahlschein(),
      ];
      mockDefinitions.getUngueltigeWahlscheine.mockReturnValue(
        mockedServiceResponse
      );

      await unitUnderTest.loadUngueltigeWahlscheine();

      expect(unitUnderTest.ungueltigeWahlscheine).toStrictEqual(
        mockedServiceResponse
      );
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

      unitUnderTest.ungueltigeWahlscheine = [createUngueltigerWahlschein()];

      mockDefinitions.getUngueltigeWahlscheine.mockRejectedValue(
        new Error("mocked service error")
      );

      await unitUnderTest.loadUngueltigeWahlscheine();

      expect(unitUnderTest.ungueltigeWahlscheine).toStrictEqual([]);
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
            resolve(createUngueltigerWahlschein());
          }, timeout);
        })
      );

      expect(unitUnderTest.ungueltigeWahlscheineIsLoading).toStrictEqual(false);
      const promise = unitUnderTest.loadUngueltigeWahlscheine();

      expect(unitUnderTest.ungueltigeWahlscheineIsLoading).toStrictEqual(true);
      vi.advanceTimersByTime(timeout);
      await promise;
      expect(unitUnderTest.ungueltigeWahlscheineIsLoading).toStrictEqual(false);
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
            reject(createUngueltigerWahlschein());
          }, timeout);
        })
      );

      expect(unitUnderTest.ungueltigeWahlscheineIsLoading).toStrictEqual(false);
      const promise = unitUnderTest.loadUngueltigeWahlscheine();

      expect(unitUnderTest.ungueltigeWahlscheineIsLoading).toStrictEqual(true);
      vi.advanceTimersByTime(timeout);
      await promise;
      expect(unitUnderTest.ungueltigeWahlscheineIsLoading).toStrictEqual(false);
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
        unitUnderTest.ungueltigeWahlscheineLoadingFailed =
          initValueForUngueltigeWahlscheineLoadingFailed;

        mockDefinitions.getUngueltigeWahlscheine.mockRejectedValue(
          new Error("mocked service error")
        );

        await unitUnderTest.loadUngueltigeWahlscheine();

        expect(unitUnderTest.ungueltigeWahlscheineLoadingFailed).toStrictEqual(
          true
        );
      }
    );

    it.each([true, false])(
      "should_resetAndSetFailedFlagToTrue_when_serviceSucceededAndFlagWasInitially'%o'",
      async (initValueForUngueltigeWahlscheineLoadingFailed) => {
        const wahltagID = "wahltagID";
        const wahlbezirksArt = WahlbezirksArtEnum.UWB;
        useUserStore().setUser(
          prepareUser()
            .wahltagID(wahltagID)
            .wahlbezirksArt(wahlbezirksArt)
            .build()
        );
        unitUnderTest.ungueltigeWahlscheineLoadingFailed =
          initValueForUngueltigeWahlscheineLoadingFailed;

        mockDefinitions.getUngueltigeWahlscheine.mockReturnValue(
          createUngueltigerWahlschein()
        );

        await unitUnderTest.loadUngueltigeWahlscheine();

        expect(unitUnderTest.ungueltigeWahlscheineLoadingFailed).toStrictEqual(
          false
        );
      }
    );
  });

  describe("sendEroeffnungsuhrzeit", () => {
    it("should_updateIsSavingAndSetSentValue_when_succeeded", async () => {
      const eroeffnungsuhrzeit = mockedNow;
      unitUnderTest.eroeffnungsuhrzeit = eroeffnungsuhrzeit;

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

      expect(unitUnderTest.eroeffnungsuhrzeitIsSaving).toStrictEqual(false);
      const sendEroeffnungsuhrzeitPromise =
        unitUnderTest.sendEroeffnungsuhrzeit();
      expect(unitUnderTest.eroeffnungsuhrzeitIsSaving).toStrictEqual(true);

      vi.advanceTimersByTime(timeout);
      await sendEroeffnungsuhrzeitPromise;

      expect(mockDefinitions.postEroeffnungsuhrzeit.mock.calls).toStrictEqual([
        [wahlbezirkID, eroeffnungsuhrzeit],
      ]);
      expect(unitUnderTest.eroeffnungsuhrzeitIsSaving).toStrictEqual(false);
      expect(unitUnderTest.eroeffnungsuhrzeit?.getTime()).toStrictEqual(
        eroeffnungsuhrzeit.getTime()
      );
    });

    it("should_notCallService_when_noEroeffnungsuhrzeitIsGiven", async () => {
      unitUnderTest.eroeffnungsuhrzeit = undefined;
      useUserStore().setUser(
        prepareUser().wahlbezirkID("wahlbezirkID").build()
      );

      expect(unitUnderTest.eroeffnungsuhrzeitIsSaving).toStrictEqual(false);
      const sendEroeffnungsuhrzeitPromise =
        unitUnderTest.sendEroeffnungsuhrzeit();

      vi.advanceTimersByTime(100);
      await sendEroeffnungsuhrzeitPromise;

      expect(
        mockDefinitions.postEroeffnungsuhrzeit.mock.calls.length
      ).toStrictEqual(0);
      expect(unitUnderTest.eroeffnungsuhrzeitIsSaving).toStrictEqual(false);
    });
  });

  describe("sendPflegeWaehlerverzeichnis", () => {
    it("should_useServiceAndUpdateIsSaving_when_waehlerverzeichnisNummerInUserIsGiven", async () => {
      const userWahlbezirkID = "wahlbezirkID";
      useUserStore().setUser(
        prepareUser().wahlbezirkID(userWahlbezirkID).build()
      );

      const pflegeWaehlerverzeichnis = createPflegeWaehlerverzeichnis();
      unitUnderTest.pflegeWaehlerverzeichnis = pflegeWaehlerverzeichnis;

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

      expect(unitUnderTest.pflegeWaehlerverzeichnisIsSaving).toStrictEqual(
        false
      );
      const sendPflegeWaehlerverzeichnisPromise =
        unitUnderTest.sendPflegeWaehlerverzeichnis();
      expect(unitUnderTest.pflegeWaehlerverzeichnisIsSaving).toStrictEqual(
        true
      );

      vi.advanceTimersByTime(timeout);
      await sendPflegeWaehlerverzeichnisPromise;

      expect(unitUnderTest.pflegeWaehlerverzeichnisIsSaving).toStrictEqual(
        false
      );
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
      unitUnderTest.pflegeWaehlerverzeichnis = pflegeWaehlerverzeichnis;

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

      expect(unitUnderTest.pflegeWaehlerverzeichnisIsSaving).toStrictEqual(
        false
      );
      const sendPflegeWaehlerverzeichnisPromise =
        unitUnderTest.sendPflegeWaehlerverzeichnis();
      expect(unitUnderTest.pflegeWaehlerverzeichnisIsSaving).toStrictEqual(
        true
      );

      vi.advanceTimersByTime(timeout);
      await expect(sendPflegeWaehlerverzeichnisPromise).rejects.toThrow();

      expect(unitUnderTest.pflegeWaehlerverzeichnisIsSaving).toStrictEqual(
        false
      );
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

      await unitUnderTest.sendPflegeWaehlerverzeichnis();

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
      unitUnderTest.urnenwahlVorbereitung = mockedUrnenwahlvorbereitung;

      await unitUnderTest.sendUrnenwahlvorbereitung();

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
      unitUnderTest.urnenwahlVorbereitung = mockedUrnenwahlvorbereitung;

      const mockedError = new Error("Speicherfehler!");
      mockDefinitions.postUrnenwahlvorbereitung.mockImplementationOnce(() => {
        throw mockedError;
      });

      try {
        await unitUnderTest.sendUrnenwahlvorbereitung();
      } catch (error) {
        expect(error).equals(mockedError);
        expect(mockDefinitions.postUrnenwahlvorbereitung).toHaveBeenCalledWith(
          wahlbezirkID,
          mockedUrnenwahlvorbereitung
        );
      }
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
      unitUnderTest.briefwahlVorbereitung = mockedBriefwahlvorbereitung;

      expect(unitUnderTest.briefWahlVorbereitungIsSaving).toStrictEqual(false);
      const sendBriefwahlvorbereitungPromise =
        unitUnderTest.sendBriefwahlvorbereitung();
      expect(unitUnderTest.briefWahlVorbereitungIsSaving).toStrictEqual(true);

      vi.advanceTimersByTime(timeout);
      await sendBriefwahlvorbereitungPromise;

      expect(mockDefinitions.postBriefwahlvorbereitung).toHaveBeenCalledWith(
        wahlbezirkID,
        mockedBriefwahlvorbereitung
      );
      expect(unitUnderTest.briefWahlVorbereitungIsSaving).toStrictEqual(false);
      expect(unitUnderTest.briefwahlVorbereitung).toEqual(
        mockedBriefwahlvorbereitung
      );
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
      unitUnderTest.briefwahlVorbereitung = mockedBriefwahlvorbereitung;

      const mockedError = new Error("Speicherfehler!");
      mockDefinitions.postBriefwahlvorbereitung.mockImplementationOnce(() => {
        throw mockedError;
      });

      try {
        await unitUnderTest.sendBriefwahlvorbereitung();
      } catch (error) {
        expect(error).equals(mockedError);
        expect(mockDefinitions.postBriefwahlvorbereitung).toHaveBeenCalledWith(
          wahlbezirkID,
          mockedBriefwahlvorbereitung
        );
      }
    });
  });
});
