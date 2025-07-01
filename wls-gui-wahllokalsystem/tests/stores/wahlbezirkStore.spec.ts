import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  postUrnenwahlSchliessungsuhrzeit: vi.fn(),
  postEroeffnungsuhrzeit: vi.fn(),
  postUrnenwahlvorbereitung: vi.fn(),
  getUngueltigeWahlscheine: vi.fn(),
}));

vi.mock("@/composables/basisdaten/ungueltigeWahlscheineService.ts", () => ({
  useUngueltigeWahlscheineService: () => ({
    getUngueltigeWahlscheine: mockDefinitions.getUngueltigeWahlscheine,
  }),
}));
vi.mock("@/composables/wahlvorbereitung/wahlvorbereitungService", () => ({
  useWahlvorbereitungService: () => ({
    postUrnenwahlSchliessungsuhrzeit:
      mockDefinitions.postUrnenwahlSchliessungsuhrzeit,
    postEroeffnungsuhrzeit: mockDefinitions.postEroeffnungsuhrzeit,
    postUrnenwahlvorbereitung: mockDefinitions.postUrnenwahlvorbereitung,
  }),
}));

const mockedNow = new Date();
const { prepareUser } = useUserTestDataFactory();

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

  describe("sendSchliessungsuhrzeit", () => {
    it("should_updateIsSavingAndSetSentValue_when_succeeded", async () => {
      const schliessungsuhrzeit = mockedNow;
      unitUnderTest.schliessungsuhrzeit = schliessungsuhrzeit;

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

      expect(unitUnderTest.schliessungsuhrzeitIsSaving).toStrictEqual(false);
      const sendSchliessungsuhrzeitPromise =
        unitUnderTest.sendSchliessungsuhrzeit();
      expect(unitUnderTest.schliessungsuhrzeitIsSaving).toStrictEqual(true);

      vi.advanceTimersByTime(timeout);
      await sendSchliessungsuhrzeitPromise;

      expect(
        mockDefinitions.postUrnenwahlSchliessungsuhrzeit.mock.calls
      ).toStrictEqual([[wahlbezirkID, schliessungsuhrzeit]]);
      expect(unitUnderTest.schliessungsuhrzeitIsSaving).toStrictEqual(false);
      expect(unitUnderTest.schliessungsuhrzeit?.getTime()).toStrictEqual(
        schliessungsuhrzeit.getTime()
      );
    });

    it("should_notUpdateSchliessungsUhrzeitSent_when_postUrnenwahlSchliessungsuhrzeitFails", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      unitUnderTest.schliessungsuhrzeit = mockedNow;

      const mockedError = new Error("Speicherfehler!");
      mockDefinitions.postUrnenwahlSchliessungsuhrzeit.mockImplementationOnce(
        () => {
          throw mockedError;
        }
      );

      try {
        await unitUnderTest.sendSchliessungsuhrzeit();
      } catch (error) {
        expect(error).equals(mockedError);
        expect(unitUnderTest.schliessungsuhrzeitSent).toBe(undefined);
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

      const urnenwahlvorbereitung = {
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

      await unitUnderTest.sendUrnenwahlvorbereitung(urnenwahlvorbereitung);

      expect(mockDefinitions.postUrnenwahlvorbereitung).toHaveBeenCalledWith(
        wahlbezirkID,
        urnenwahlvorbereitung
      );
      expect(unitUnderTest.urnenwahlVorbereitung).toEqual(
        urnenwahlvorbereitung
      );
    });

    it("should_notSendUrnenwahlvorbereitungAndNotUpdateUrnenwahlVorbereitung_when_wahlbezirkIDIsNotGiven", async () => {
      const userStore = useUserStore();
      userStore.setUser(prepareUser().wahlbezirkID(undefined).build());

      const urnenwahlvorbereitung = {
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

      await unitUnderTest.sendUrnenwahlvorbereitung(urnenwahlvorbereitung);

      expect(mockDefinitions.postUrnenwahlvorbereitung).toBeCalledTimes(0);
    });

    it("should_notUpdateUrnenwahlVorbereitung_when_postUrnenwahlvorbereitungFails", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const urnenwahlvorbereitung = {
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

      const mockedError = new Error("Speicherfehler!");
      mockDefinitions.postUrnenwahlvorbereitung.mockImplementationOnce(() => {
        throw mockedError;
      });

      try {
        await unitUnderTest.sendUrnenwahlvorbereitung(urnenwahlvorbereitung);
      } catch (error) {
        expect(error).equals(mockedError);
        expect(mockDefinitions.postUrnenwahlvorbereitung).toHaveBeenCalledWith(
          wahlbezirkID,
          urnenwahlvorbereitung
        );
      }
    });
  });
});
