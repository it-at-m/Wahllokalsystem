import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  postUrnenwahlSchliessungsuhrzeit: vi.fn(),
}));

vi.mock("@/composables/wahlvorbereitung/wahlvorbereitungService", () => ({
  useWahlvorbereitungService: () => ({
    postUrnenwahlSchliessungsuhrzeit:
      mockDefinitions.postUrnenwahlSchliessungsuhrzeit,
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
