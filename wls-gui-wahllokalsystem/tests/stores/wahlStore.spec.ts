import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { User } from "@/types/User.ts";

const mockDefinitions = vi.hoisted(() => ({
  getWahlen: vi.fn(),
}));

vi.mock("@/composables/wahl/wahlService.ts", () => ({
  useWahlService: () => ({
    getWahlen: mockDefinitions.getWahlen,
  }),
}));

const { createWahl } = useWahlTestDataFactory();
const { generateRandomString } = useCommonTestDataFactory();

describe("wahlenStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useWahlenStore>;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useWahlenStore();
  });

  it("should_loadWahlen_when_calledWithCorrectWahltagID", async () => {
    const wahltagID = generateRandomString(10);
    const userStore = useUserStore();
    const user = new User();
    user.wahltagID = wahltagID;
    userStore.setUser(user);

    const expectedWahlArray = [createWahl()];
    const wahl = Promise.resolve(expectedWahlArray);
    mockDefinitions.getWahlen.mockReturnValue(wahl);

    await unitUnderTest.loadWahlen();

    expect(unitUnderTest.wahlen).toStrictEqual(expectedWahlArray);
  });

  it("should_notLoadWahlen_when_calledWithNullWahltagId", async () => {
    await unitUnderTest.loadWahlen();

    expect(unitUnderTest.wahlen).toStrictEqual(undefined);
  });
});
