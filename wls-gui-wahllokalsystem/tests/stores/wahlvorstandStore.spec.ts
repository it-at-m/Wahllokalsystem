import type { User } from "@/types/User.ts";
import type { Wahlvorstandsmitglied } from "@/types/wahlvorstand/Wahlvorstandsmitglied.ts";

import { createTestingPinia } from "@pinia/testing";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useWahlvorstandTestDataFactory } from "@tests/utils/wahlvorstand/WahlvorstandTestDataFactory.ts";
import { createPinia, setActivePinia, storeToRefs } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import {
  MIN_WAHLVORSTAND_ANWESEND_NACH_SCHLIESSUNG,
  MIN_WAHLVORSTAND_ANWESEND_VOR_SCHLIESSUNG,
} from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { WahlvorstandsmitgliedFunktionEnum } from "@/types/wahlvorstand/WahlvorstandsmitgliedFunktion.ts";

const mockDefinitions = vi.hoisted(() => ({
  isSchriftfuehrer: vi.fn(),
  isWahlvorsteher: vi.fn(),
  saveWahlvorstand: vi.fn(),
  getWahlvorstand: vi.fn(),
}));

vi.mock("@/types/wahlvorstand/WahlvorstandsmitgliedFunktion", () => ({
  WahlvorstandsmitgliedFunktionEnum: {
    W: "W",
    Sb: "SB",
    Swb: "SWB",
    Ssb: "SSB",
    B: "B",
  },
  isSchriftfuehrer: mockDefinitions.isSchriftfuehrer,
  isWahlvorsteher: mockDefinitions.isWahlvorsteher,
}));

vi.mock("@/composables/wahlvorstand/wahlvorstandService", () => ({
  useWahlvorstandService: () => ({
    saveWahlvorstand: mockDefinitions.saveWahlvorstand,
    getWahlvorstand: mockDefinitions.getWahlvorstand,
  }),
}));

const mockedNow = new Date();
const { prepareUser } = useUserTestDataFactory();
const {
  createWahlvorstand,
  prepareWahlvorstand,
  prepareWahlvorstandsmitglied,
} = useWahlvorstandTestDataFactory();

describe("wahlvorstandStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useWahlvorstandStore>;

  beforeEach(() => {
    // creates a fresh pinia and makes it active
    // so it's automatically picked up by any useStore() call
    // without having to pass it to it: `useStore(pinia)`
    setActivePinia(createPinia());
    vi.useFakeTimers({
      now: mockedNow,
    });
    unitUnderTest = useWahlvorstandStore();
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  describe("isSchriftfuehrerAnwesend", () => {
    it("should_returnFalse_when_noMitgliedExists", () => {
      unitUnderTest.wahlvorstand.wahlvorstandsmitglieder = [];

      expect(unitUnderTest.isSchriftfuehrerAnwesend).toStrictEqual(false);
    });

    it("should_returnTrue_when_atLeastOneMitgliedMatches", () => {
      mockDefinitions.isSchriftfuehrer.mockReturnValue(true);

      unitUnderTest.wahlvorstand.wahlvorstandsmitglieder = [
        prepareWahlvorstandsmitglied()
          .funktion(WahlvorstandsmitgliedFunktionEnum.Sb)
          .anwesend(true)
          .build(),
        prepareWahlvorstandsmitglied()
          .funktion(WahlvorstandsmitgliedFunktionEnum.W)
          .anwesend(false)
          .build(),
      ];

      expect(unitUnderTest.isSchriftfuehrerAnwesend).toStrictEqual(true);

      expect(mockDefinitions.isSchriftfuehrer.mock.calls[0]?.[0]).toStrictEqual(
        WahlvorstandsmitgliedFunktionEnum.Sb
      );
    });

    it("should_returnFalse_when_whenMitgliedWithFunktionExistsButIsNotAnwesend", () => {
      mockDefinitions.isSchriftfuehrer.mockReturnValue(true);

      unitUnderTest.wahlvorstand.wahlvorstandsmitglieder = [
        prepareWahlvorstandsmitglied()
          .funktion(WahlvorstandsmitgliedFunktionEnum.Sb)
          .anwesend(false)
          .build(),
        prepareWahlvorstandsmitglied()
          .funktion(WahlvorstandsmitgliedFunktionEnum.W)
          .anwesend(false)
          .build(),
      ];

      expect(unitUnderTest.isSchriftfuehrerAnwesend).toStrictEqual(false);

      expect(mockDefinitions.isSchriftfuehrer.mock.calls[0]?.[0]).toStrictEqual(
        WahlvorstandsmitgliedFunktionEnum.Sb
      );
      expect(mockDefinitions.isSchriftfuehrer.mock.calls[1]?.[0]).toStrictEqual(
        WahlvorstandsmitgliedFunktionEnum.W
      );
    });

    it("should_returnFalse_when_noMitgliedMatchesFunktion", () => {
      mockDefinitions.isSchriftfuehrer.mockReturnValue(false);

      unitUnderTest.wahlvorstand.wahlvorstandsmitglieder = [
        prepareWahlvorstandsmitglied()
          .funktion(WahlvorstandsmitgliedFunktionEnum.Sb)
          .anwesend(true)
          .build(),
        prepareWahlvorstandsmitglied()
          .funktion(WahlvorstandsmitgliedFunktionEnum.W)
          .anwesend(true)
          .build(),
      ];
      expect(unitUnderTest.isSchriftfuehrerAnwesend).toStrictEqual(false);
      expect(mockDefinitions.isSchriftfuehrer.mock.calls[0]?.[0]).toStrictEqual(
        WahlvorstandsmitgliedFunktionEnum.Sb
      );
      expect(mockDefinitions.isSchriftfuehrer.mock.calls[1]?.[0]).toStrictEqual(
        WahlvorstandsmitgliedFunktionEnum.W
      );
    });
  });

  describe("isWahlvorsteherAnwesend", () => {
    it.each([
      { funktion: WahlvorstandsmitgliedFunktionEnum.W, expected: true },
      { funktion: WahlvorstandsmitgliedFunktionEnum.Swb, expected: true },
    ])(
      "should_returnTrue_when_atLeastOneMitgliedWithFunktion'$funktion'IsAnwesend",
      ({ funktion, expected }) => {
        mockDefinitions.isWahlvorsteher.mockReturnValue(true);

        unitUnderTest.wahlvorstand.wahlvorstandsmitglieder = [
          prepareWahlvorstandsmitglied()
            .funktion(funktion)
            .anwesend(true)
            .build(),
        ];

        expect(unitUnderTest.isWahlvorsteherAnwesend).toStrictEqual(expected);
        expect(mockDefinitions.isWahlvorsteher).toHaveBeenCalledWith(funktion);
      }
    );

    it.each([
      { funktion: WahlvorstandsmitgliedFunktionEnum.W, expected: false },
      { funktion: WahlvorstandsmitgliedFunktionEnum.Swb, expected: false },
    ])(
      "should_returnFalse_when_whenMitgliedWithFunktion'$funktion'ExistsButIsNotAnwesend",
      ({ funktion, expected }) => {
        unitUnderTest.wahlvorstand.wahlvorstandsmitglieder = [
          prepareWahlvorstandsmitglied()
            .funktion(funktion)
            .anwesend(false)
            .build(),
        ];

        expect(unitUnderTest.isWahlvorsteherAnwesend).toStrictEqual(expected);
        expect(
          mockDefinitions.isWahlvorsteher.mock.calls[0]?.[0]
        ).toStrictEqual(funktion);
      }
    );

    it.each([
      { funktion: WahlvorstandsmitgliedFunktionEnum.Sb, expected: false },
      { funktion: WahlvorstandsmitgliedFunktionEnum.Ssb, expected: false },
      { funktion: WahlvorstandsmitgliedFunktionEnum.B, expected: false },
    ])(
      "should_returnFalse_when_mitgliedWithFunktion'$funktion'IsAnwesendButDoesNotMatch",
      ({ funktion, expected }) => {
        mockDefinitions.isWahlvorsteher.mockReturnValue(false);

        unitUnderTest.wahlvorstand.wahlvorstandsmitglieder = [
          prepareWahlvorstandsmitglied()
            .funktion(funktion)
            .anwesend(true)
            .build(),
        ];

        expect(unitUnderTest.isWahlvorsteherAnwesend).toStrictEqual(expected);
        expect(
          mockDefinitions.isWahlvorsteher.mock.calls[0]?.[0]
        ).toStrictEqual(funktion);
      }
    );
  });

  describe("isMindestanwesenheitErreicht", () => {
    it("should_returnFalse_when_noMitgliedExists", () => {
      unitUnderTest.wahlvorstand.wahlvorstandsmitglieder = [];

      expect(unitUnderTest.isMindestanwesenheitErreicht).toStrictEqual(false);
    });

    it.each([
      {
        schliessungsuhrzeit: undefined,
        anwesend: MIN_WAHLVORSTAND_ANWESEND_VOR_SCHLIESSUNG - 1,
        expected: false,
      },
      {
        schliessungsuhrzeit: new Date("2025-03-31T13:31:37"),
        anwesend: MIN_WAHLVORSTAND_ANWESEND_NACH_SCHLIESSUNG - 1,
        expected: false,
      },
      {
        schliessungsuhrzeit: undefined,
        anwesend: MIN_WAHLVORSTAND_ANWESEND_VOR_SCHLIESSUNG,
        expected: true,
      },
      {
        schliessungsuhrzeit: undefined,
        anwesend: MIN_WAHLVORSTAND_ANWESEND_VOR_SCHLIESSUNG + 1,
        expected: true,
      },
      {
        schliessungsuhrzeit: new Date("2025-03-31T13:31:37"),
        anwesend: MIN_WAHLVORSTAND_ANWESEND_NACH_SCHLIESSUNG,
        expected: true,
      },
      {
        schliessungsuhrzeit: new Date("2025-03-31T13:31:37"),
        anwesend: MIN_WAHLVORSTAND_ANWESEND_NACH_SCHLIESSUNG + 1,
        expected: true,
      },
    ])(
      "should_return'$expected'_when_schliessungsuhrzeitIs'$schliessungsuhrzeit'And'$anwesend'MitgliederAreAnwesend",
      ({ expected, schliessungsuhrzeit, anwesend }) => {
        const { schliessungsuhrzeitState } = storeToRefs(useWahlbezirkStore());
        schliessungsuhrzeitState.value.schliessungsuhrzeitSent =
          schliessungsuhrzeit;

        _addAnwesendeWahlvorstandsmitglieder(anwesend);

        expect(unitUnderTest.isMindestanwesenheitErreicht).toStrictEqual(
          expected
        );
      }
    );
  });

  describe("isWahlvorstandAusreichendAnwesend", () => {
    let unitUnderTest: ReturnType<typeof useWahlvorstandStore>;
    beforeEach(() => {
      const testPinia = createTestingPinia({
        createSpy: vi.fn,
      });
      unitUnderTest = useWahlvorstandStore(testPinia);
    });

    it("should_returnTrue_when_schriftfuehrerAndWahlvorsteherAreAnwesend", () => {
      // @ts-expect-error: cannot set readonly
      unitUnderTest.isWahlvorsteherAnwesend = true;
      // @ts-expect-error: cannot set readonly
      unitUnderTest.isSchriftfuehrerAnwesend = true;
      // @ts-expect-error: cannot set readonly
      unitUnderTest.isMindestanwesenheitErreicht = true;

      expect(unitUnderTest.isWahlvorstandAusreichendAnwesend).toStrictEqual(
        true
      );
    });

    it("should_returnFalse_when_schriftfuehrerIsNotAnwesendAndWahlvorsteherIsAnwesend", () => {
      // @ts-expect-error: cannot set readonly
      unitUnderTest.isWahlvorsteherAnwesend = true;
      // @ts-expect-error: cannot set readonly
      unitUnderTest.isSchriftfuehrerAnwesend = false;
      // @ts-expect-error: cannot set readonly
      unitUnderTest.isMindestanwesenheitErreicht = true;

      expect(unitUnderTest.isWahlvorstandAusreichendAnwesend).toStrictEqual(
        false
      );
    });

    it("should_returnFalse_when_schriftfuehrerIsAnwesendAndWahlvorsteherIsNotAnwesend", () => {
      // @ts-expect-error: cannot set readonly
      unitUnderTest.isWahlvorsteherAnwesend = false;
      // @ts-expect-error: cannot set readonly
      unitUnderTest.isSchriftfuehrerAnwesend = true;
      // @ts-expect-error: cannot set readonly
      unitUnderTest.isMindestanwesenheitErreicht = true;

      expect(unitUnderTest.isWahlvorstandAusreichendAnwesend).toStrictEqual(
        false
      );
    });

    it("should_returnFalse_when_schriftfuehrerIsNotAnwesendAndWahlvorsteherIsNotAnwesend", () => {
      // @ts-expect-error: cannot set readonly
      unitUnderTest.isWahlvorsteherAnwesend = false;
      // @ts-expect-error: cannot set readonly
      unitUnderTest.isSchriftfuehrerAnwesend = false;
      // @ts-expect-error: cannot set readonly
      unitUnderTest.isMindestanwesenheitErreicht = true;

      expect(unitUnderTest.isWahlvorstandAusreichendAnwesend).toStrictEqual(
        false
      );
    });

    it("should_returnFalse_when_mindestanwesenheitIsNotGiven", () => {
      // @ts-expect-error: cannot set readonly
      unitUnderTest.isWahlvorsteherAnwesend = true;
      // @ts-expect-error: cannot set readonly
      unitUnderTest.isSchriftfuehrerAnwesend = true;
      // @ts-expect-error: cannot set readonly
      unitUnderTest.isMindestanwesenheitErreicht = false;

      expect(unitUnderTest.isWahlvorstandAusreichendAnwesend).toStrictEqual(
        false
      );
    });
  });

  describe("sendWahlvorstand", () => {
    it("should_sendWahlvorstand_when_wahlbezirkIDIsGiven", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(_createUser(wahlbezirkID));

      const mockedDatetime = new Date();

      mockDefinitions.saveWahlvorstand.mockReturnValue(
        Promise.resolve({ updateDatetime: mockedDatetime })
      );

      await unitUnderTest.sendWahlvorstand();

      expect(mockDefinitions.saveWahlvorstand).toHaveBeenCalledWith(
        wahlbezirkID,
        unitUnderTest.wahlvorstand
      );
    });

    it("should_setLastSend_when_wahlvorstandIsSent", async () => {
      const userStore = useUserStore();
      userStore.setUser(_createUser("wahlbezirkID"));

      expect(unitUnderTest.lastSending).toBeNull();

      const mockedDatetime = new Date();

      mockDefinitions.saveWahlvorstand.mockReturnValue(
        Promise.resolve({ updateDatetime: mockedDatetime })
      );

      await unitUnderTest.sendWahlvorstand();

      expect(unitUnderTest.lastSending).toStrictEqual(mockedNow);
    });

    it("should_setWahlvorstandErfasst_when_wahlvorstandIsSent", async () => {
      const userStore = useUserStore();
      userStore.setUser(_createUser("wahlbezirkID"));

      const mockedDatetime = new Date();

      mockDefinitions.saveWahlvorstand.mockReturnValue(
        Promise.resolve({ updateDatetime: mockedDatetime })
      );

      expect(useWorkflowStore().isWahlvorstandErfasst).toStrictEqual(false);
      await unitUnderTest.sendWahlvorstand();

      expect(useWorkflowStore().isWahlvorstandErfasst).toStrictEqual(true);
    });
  });

  describe("initWahlvorstand", () => {
    it("should_loadWahlvorstand_when_called", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(_createUser(wahlbezirkID));

      const mockedGetWahlvorstand = createWahlvorstand(0);
      mockDefinitions.getWahlvorstand.mockReturnValue(mockedGetWahlvorstand);

      await unitUnderTest.initWahlvorstand();

      expect(unitUnderTest.wahlvorstand).toStrictEqual(mockedGetWahlvorstand);
      expect(mockDefinitions.getWahlvorstand.mock.calls).toStrictEqual([
        [wahlbezirkID, { forceUpdate: true, sendNotification: true }],
      ]);
    });

    it("should_notLoadWahlvorstand_when_serviceCallFailed", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(_createUser(wahlbezirkID));
      mockDefinitions.getWahlvorstand.mockRejectedValueOnce(
        new Error("service call failed")
      );

      await expect(() =>
        unitUnderTest.initWahlvorstand()
      ).rejects.toThrowError();
    });

    it("should_setWahlvorstandErfasstFalse_when_called", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(_createUser(wahlbezirkID));
      useWorkflowStore().isWahlvorstandErfasst = true;

      const mockedGetWahlvorstand = createWahlvorstand(0);
      mockDefinitions.getWahlvorstand.mockReturnValue(mockedGetWahlvorstand);

      await unitUnderTest.initWahlvorstand();

      expect(useWorkflowStore().isWahlvorstandErfasst).toStrictEqual(false);
    });
  });

  describe("forceLoadWahlvorstand", () => {
    it("should_setWahlvorstand_when_userHasWahlbezirkID", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(_createUser(wahlbezirkID));

      const mockedGetWahlvorstand = createWahlvorstand(0);
      mockDefinitions.getWahlvorstand.mockReturnValue(mockedGetWahlvorstand);

      await unitUnderTest.forceLoadWahlvorstand();

      expect(unitUnderTest.wahlvorstand).toStrictEqual(mockedGetWahlvorstand);
      expect(mockDefinitions.getWahlvorstand.mock.calls).toStrictEqual([
        [wahlbezirkID, { forceUpdate: true, sendNotification: true }],
      ]);
    });

    it("should_setLastLoading_when_wahlvorstandIsLoaded", async () => {
      const userStore = useUserStore();
      userStore.setUser(_createUser("wahlbezirkID"));

      useWorkflowStore().isWahlvorstandErfasst = true;

      const mockedGetWahlvorstand = createWahlvorstand(0);
      mockDefinitions.getWahlvorstand.mockReturnValue(mockedGetWahlvorstand);

      expect(unitUnderTest.lastLoading).toBeNull();

      await unitUnderTest.forceLoadWahlvorstand();

      expect(unitUnderTest.lastLoading).toStrictEqual(mockedNow);
      expect(useWorkflowStore().isWahlvorstandErfasst).toStrictEqual(false);
    });

    it("should_notUpdateLastLoading_when_getWahlvorstandFails", async () => {
      const userStore = useUserStore();
      userStore.setUser(_createUser("wahlbezirkID"));

      useWorkflowStore().isWahlvorstandErfasst = true;

      mockDefinitions.getWahlvorstand.mockImplementationOnce(() => {
        throw new Error("API Error");
      });

      expect(unitUnderTest.lastLoading).toBeNull();

      await expect(unitUnderTest.forceLoadWahlvorstand()).rejects.toThrow(
        "API Error"
      );

      expect(unitUnderTest.lastLoading).toBeNull();
      expect(useWorkflowStore().isWahlvorstandErfasst).toStrictEqual(true);
    });
  });

  describe("changeAnwesendOfMitglied", () => {
    it("should_setGivenAnwesenheitOfMitglied_when_mitgliedWithIdExists", () => {
      const newAnwesenheit = true;
      const mitgliedID = "mitgliedID";

      const mitgliedToChange = prepareWahlvorstandsmitglied()
        .identifikator(mitgliedID)
        .anwesend(!newAnwesenheit)
        .build();

      unitUnderTest.wahlvorstand.wahlvorstandsmitglieder = [
        prepareWahlvorstandsmitglied()
          .identifikator(mitgliedID + "andere")
          .anwesend(false)
          .build(),
        mitgliedToChange,
        prepareWahlvorstandsmitglied()
          .identifikator(mitgliedID + "andere2")
          .anwesend(false)
          .build(),
      ];

      unitUnderTest.changeAnwesendOfMitglied(newAnwesenheit, mitgliedID);

      expect(mitgliedToChange.anwesend).toStrictEqual(newAnwesenheit);
    });

    it("should_notUpdateAnwesenheitOfMitglied_when_mitgliedWithIdDoesNotExists", () => {
      const newAnwesenheit = true;
      const mitgliedID = "mitgliedID";

      unitUnderTest.wahlvorstand.wahlvorstandsmitglieder = [
        prepareWahlvorstandsmitglied()
          .identifikator(mitgliedID + "andere")
          .anwesend(false)
          .build(),
        prepareWahlvorstandsmitglied()
          .identifikator(mitgliedID + "andere2")
          .anwesend(false)
          .build(),
        prepareWahlvorstandsmitglied()
          .identifikator(mitgliedID + "andere3")
          .anwesend(false)
          .build(),
      ];

      unitUnderTest.changeAnwesendOfMitglied(newAnwesenheit, mitgliedID);

      unitUnderTest.wahlvorstand.wahlvorstandsmitglieder.forEach((mitglied) =>
        expect(mitglied.anwesend).toStrictEqual(false)
      );
    });
  });

  describe("isLoading", () => {
    it("should_updateIsLoading_when_loadWahlvorstandIsCalled", async () => {
      const timeout = 100;
      const userStore = useUserStore();
      userStore.setUser(_createUser("wahlbezirkID"));

      // Verzögerung API-Aufruf simulieren, um die Asynchronität zu testen
      mockDefinitions.getWahlvorstand.mockReturnValue(
        new Promise((resolve) => {
          setTimeout(() => {
            resolve({});
          }, timeout);
        })
      );

      // vor dem API-Aufruf
      expect(unitUnderTest.isLoading).toBe(false);

      const promise = unitUnderTest.forceLoadWahlvorstand();

      // während des API-Aufrufs
      expect(unitUnderTest.isLoading).toBe(true);

      // Zeit vorstellen, um die Promise aufzulösen
      vi.advanceTimersByTime(timeout);
      await promise;

      // nach dem API-Aufruf
      expect(unitUnderTest.isLoading).toBe(false);
    });

    it("should_updateIsLoading_when_loadWahlvorstandFails", async () => {
      const timeout = 100;
      const userStore = useUserStore();
      userStore.setUser(_createUser("wahlbezirkID"));

      // Verzögerung API-Aufruf simulieren, um die Asynchronität zu testen
      mockDefinitions.getWahlvorstand.mockReturnValue(
        new Promise((resolve, reject) => {
          setTimeout(() => {
            reject("Mocked API Error");
          }, timeout);
        })
      );

      // vor dem API-Aufruf
      expect(unitUnderTest.isLoading).toBe(false);

      const promise = unitUnderTest.forceLoadWahlvorstand();

      // während des API-Aufrufs
      expect(unitUnderTest.isLoading).toBe(true);

      // Zeit vorstellen, um die Promise aufzulösen
      vi.advanceTimersByTime(timeout);
      await expect(promise).rejects.toThrow("Mocked API Error");

      // nach dem API-Aufruf
      expect(unitUnderTest.isLoading).toBe(false);
    });
  });

  describe("isSaving", () => {
    it("should_updateIsSaving_when_sendWahlvorstandIsCalled", async () => {
      const timeout = 100;
      const userStore = useUserStore();
      userStore.setUser(_createUser("wahlbezirkID"));

      // Verzögerung API-Aufruf simulieren, um die Asynchronität zu testen
      mockDefinitions.saveWahlvorstand.mockReturnValue(
        new Promise((resolve) => {
          setTimeout(() => {
            resolve({});
          }, timeout);
        })
      );

      // vor dem API-Aufruf
      expect(unitUnderTest.isSaving).toBe(false);

      const promise = unitUnderTest.sendWahlvorstand();

      // während des API-Aufrufs
      expect(unitUnderTest.isSaving).toBe(true);

      // Zeit vorstellen, um die Promise aufzulösen
      vi.advanceTimersByTime(timeout);
      await promise;

      // nach dem API-Aufruf
      expect(unitUnderTest.isSaving).toBe(false);
    });

    it("should_updateIsSaving_when_sendWahlvorstandFails", async () => {
      const timeout = 100;
      const userStore = useUserStore();
      userStore.setUser(_createUser("wahlbezirkID"));

      // Verzögerung API-Aufruf simulieren, um die Asynchronität zu testen
      mockDefinitions.saveWahlvorstand.mockReturnValue(
        new Promise((resolve, reject) => {
          setTimeout(() => {
            reject("Mocked API Error");
          }, timeout);
        })
      );

      // vor dem API-Aufruf
      expect(unitUnderTest.isSaving).toBe(false);

      const promise = unitUnderTest.sendWahlvorstand();

      // während des API-Aufrufs
      expect(unitUnderTest.isSaving).toBe(true);

      // Zeit vorstellen, um die Promise aufzulösen
      vi.advanceTimersByTime(timeout);
      await expect(promise).rejects.toThrow("Mocked API Error");

      // nach dem API-Aufruf
      expect(unitUnderTest.isSaving).toBe(false);
    });
  });

  describe("resetAllAnwesenheiten", () => {
    it("should_setAnwesendFalseForAllWahlvorstandsmitglieder_when_wahlvorstandsmitgliederAreGiven", () => {
      unitUnderTest.wahlvorstand = prepareWahlvorstand()
        .wahlvorstandsmitglieder([
          prepareWahlvorstandsmitglied().anwesend(true).build(),
          prepareWahlvorstandsmitglied().anwesend(false).build(),
          prepareWahlvorstandsmitglied().anwesend(true).build(),
          prepareWahlvorstandsmitglied().anwesend(true).build(),
          prepareWahlvorstandsmitglied().anwesend(false).build(),
        ])
        .build();
      useWorkflowStore().isWahlvorstandErfasst = true;

      unitUnderTest.resetAllAnwesenheiten();

      expect(unitUnderTest.wahlvorstand.wahlvorstandsmitglieder).toSatisfy(
        (mitglieder: Wahlvorstandsmitglied[]) =>
          mitglieder.every((mitglied) => !mitglied.anwesend)
      );
      expect(useWorkflowStore().isWahlvorstandErfasst).toStrictEqual(false);
    });
  });

  function _addAnwesendeWahlvorstandsmitglieder(zahl: number) {
    for (let i = 1; i <= zahl; i++) {
      unitUnderTest.wahlvorstand.wahlvorstandsmitglieder.push(
        prepareWahlvorstandsmitglied().anwesend(true).build()
      );
    }
  }
});

function _createUser(wahlbezirkID: string): User {
  return prepareUser().wahlbezirkID(wahlbezirkID).build();
}
