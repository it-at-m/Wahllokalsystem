import { useSchliessungsuhrzeitTestDataFactory } from "@tests/utils/wahlvorbereitung/SchliessungsuhrzeitTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { User } from "@/types/User";

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

describe("wahlbezirkStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useWahlbezirkStore>;

  const { createSchliessungsuhrzeit } = useSchliessungsuhrzeitTestDataFactory();

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
    it("should_sendSchliessungsuhrzeit_when_inputAndWahlbezirkIDIsGiven", async () => {
      const userStore = useUserStore();
      const wahlbezirkID = "wahlbezirkID";
      const user = new User();
      user.wahlbezirkID = wahlbezirkID;
      userStore.setUser(user);

      const time = mockedNow.toISOString();

      await unitUnderTest.sendSchliessungsuhrzeit(time);
      expect(
        mockDefinitions.postUrnenwahlSchliessungsuhrzeit
      ).toHaveBeenCalledWith(wahlbezirkID, createSchliessungsuhrzeit(time));
    });

    it("should_notSendSchliessungsuhrzeit_when_wahlbezirkIDIsNotGiven", async () => {
      const userStore = useUserStore();
      const user = new User();
      user.wahlbezirkID = undefined;
      userStore.setUser(user);

      const time = mockedNow.toISOString();

      await unitUnderTest.sendSchliessungsuhrzeit(time);
      expect(mockDefinitions.postUrnenwahlSchliessungsuhrzeit).toBeCalledTimes(
        0
      );
    });
  });
});
