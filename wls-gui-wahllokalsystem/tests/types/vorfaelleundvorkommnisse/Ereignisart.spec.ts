import { createTestingPinia } from "@pinia/testing";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useUserStore } from "@/stores/userStore.ts";
import {
  EreignisartEnum,
  getEreignisArtForDateRelatedToSchliessungsuhrzeit,
} from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

describe("Ereignisart.ts", () => {
  let userStore: ReturnType<typeof useUserStore>;
  const { prepareUser } = useUserTestDataFactory();

  beforeEach(() => {
    const testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
    userStore = useUserStore(testPinia);
  });

  describe("getEreignisArtForDateRelatedToSchliessungsuhrzeitForUWB", () => {
    beforeEach(() => {
      userStore.setUser(
        prepareUser().wahlbezirksArt(WahlbezirksArtEnum.UWB).build()
      );
    });

    it("should_returnVorfall_when_schliessungsuhrzeitIsNotSet", () => {
      const result = getEreignisArtForDateRelatedToSchliessungsuhrzeit(
        new Date(),
        undefined
      );

      expect(result).toStrictEqual(EreignisartEnum.Vorfall);
    });

    it("should_returnVorfall_when_schliessungsuhrzeitIsAfterEreignisDate", () => {
      const schliessungsuhrzeit = new Date();
      const result = getEreignisArtForDateRelatedToSchliessungsuhrzeit(
        new Date(schliessungsuhrzeit.getTime() - 1),
        schliessungsuhrzeit
      );

      expect(result).toStrictEqual(EreignisartEnum.Vorfall);
    });

    it("should_returnVorfall_when_schliessungsuhrzeitIsEqualEreignisDate", () => {
      const schliessungsuhrzeit = new Date();
      const result = getEreignisArtForDateRelatedToSchliessungsuhrzeit(
        schliessungsuhrzeit,
        schliessungsuhrzeit
      );

      expect(result).toStrictEqual(EreignisartEnum.Vorfall);
    });

    it("should_returnVorkommnis_when_schliesssungsuhrzeitIsBeforeEreignisDate", () => {
      const schliessungsuhrzeit = new Date();
      const result = getEreignisArtForDateRelatedToSchliessungsuhrzeit(
        new Date(schliessungsuhrzeit.getTime() + 1),
        schliessungsuhrzeit
      );

      expect(result).toStrictEqual(EreignisartEnum.Vorkommnis);
    });
  });

  describe("getEreignisArtForDateRelatedToSchliessungsuhrzeitForBWB", () => {
    beforeEach(() => {
      const user = prepareUser().wahlbezirksArt(WahlbezirksArtEnum.BWB).build();
      userStore.setUser(user);
    });

    it("should_returnVorkommnis_when_schliessungsuhrzeitIsNotSet", () => {
      const result = getEreignisArtForDateRelatedToSchliessungsuhrzeit(
        new Date(),
        undefined
      );

      expect(result).toStrictEqual(EreignisartEnum.Vorkommnis);
    });

    it("should_returnVorkommnis_when_schliessungsuhrzeitIsAfterEreignisDate", () => {
      const schliessungsuhrzeit = new Date();
      const result = getEreignisArtForDateRelatedToSchliessungsuhrzeit(
        new Date(schliessungsuhrzeit.getTime() - 1),
        schliessungsuhrzeit
      );

      expect(result).toStrictEqual(EreignisartEnum.Vorkommnis);
    });

    it("should_returnVorkommnis_when_schliessungsuhrzeitIsEqualEreignisDate", () => {
      const schliessungsuhrzeit = new Date();
      const result = getEreignisArtForDateRelatedToSchliessungsuhrzeit(
        schliessungsuhrzeit,
        schliessungsuhrzeit
      );

      expect(result).toStrictEqual(EreignisartEnum.Vorkommnis);
    });

    it("should_returnVorkommnis_when_schliesssungsuhrzeitIsBeforeEreignisDate", () => {
      const schliessungsuhrzeit = new Date();
      const result = getEreignisArtForDateRelatedToSchliessungsuhrzeit(
        new Date(schliessungsuhrzeit.getTime() + 1),
        schliessungsuhrzeit
      );

      expect(result).toStrictEqual(EreignisartEnum.Vorkommnis);
    });
  });
});
