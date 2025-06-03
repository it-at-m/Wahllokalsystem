import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  postUrnenwahlSchliessungsuhrzeit: vi.fn(),
  postEroeffnungsuhrzeit: vi.fn(),
}));

vi.mock("@/composables/wahlvorbereitung/wahlvorbereitungService", () => ({
  useWahlvorbereitungService: () => ({
    postUrnenwahlSchliessungsuhrzeit:
      mockDefinitions.postUrnenwahlSchliessungsuhrzeit,
    postEroeffnungsuhrzeit: mockDefinitions.postEroeffnungsuhrzeit,
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

  describe("sendEroeffnungsuhrzeit", () => {
    it("should_updateIsSavingAndSetSentValue_when_succeeded", async () => {
      const eroeffnungsuhrzeit = new Date();
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

    it("should_notCallService_when_noCurrentUserWahlbezirkIDIsGiven", async () => {
      unitUnderTest.eroeffnungsuhrzeit = new Date();
      useUserStore().setUser(prepareUser().wahlbezirkID(undefined).build());

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
    it("should_sendSchliessungsuhrzeitAndUpdateSchliessungsuhrzetSent_when_inputAndWahlbezirkIDIsGiven", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const time = mockedNow.toISOString();

      await unitUnderTest.sendSchliessungsuhrzeit(time);

      expect(
        mockDefinitions.postUrnenwahlSchliessungsuhrzeit
      ).toHaveBeenCalledWith(wahlbezirkID, mockedNow);
      expect(unitUnderTest.schliessungsUhrzeitSent).toEqual(mockedNow);
    });

    it("should_notSendSchliessungsuhrzeitAndUpdateSchliessungsuhrzeitSent_when_wahlbezirkIDIsNotGiven", async () => {
      const userStore = useUserStore();
      userStore.setUser(prepareUser().wahlbezirkID(undefined).build());

      const time = mockedNow.toISOString();

      await unitUnderTest.sendSchliessungsuhrzeit(time);
      expect(mockDefinitions.postUrnenwahlSchliessungsuhrzeit).toBeCalledTimes(
        0
      );
      expect(unitUnderTest.schliessungsUhrzeitSent).toBe(undefined);
    });

    it("should_notUpdateSchliessungsUhrzeitSent_when_postUrnenwahlSchliessungsuhrzeitFails", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      userStore.setUser(prepareUser().wahlbezirkID(wahlbezirkID).build());

      const time = mockedNow.toISOString();

      const mockedError = new Error("Speicherfehler!");
      mockDefinitions.postUrnenwahlSchliessungsuhrzeit.mockImplementationOnce(
        () => {
          throw mockedError;
        }
      );

      try {
        await unitUnderTest.sendSchliessungsuhrzeit(time);
      } catch (error) {
        expect(error).equals(mockedError);
        expect(unitUnderTest.schliessungsUhrzeitSent).toBe(undefined);
        expect(
          mockDefinitions.postUrnenwahlSchliessungsuhrzeit
        ).toHaveBeenCalledWith(wahlbezirkID, mockedNow);
      }
    });
  });
});
