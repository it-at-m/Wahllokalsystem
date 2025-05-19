import { createTestingPinia } from "@pinia/testing";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useUserStore } from "@/stores/userStore.ts";
import { User } from "@/types/User.ts";
import {
  EreignisartEnum,
  getEreignisArtForDateRelatedToSchliessungsuhrzeit,
} from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

describe("Ereignisart.ts", () => {
  let userStore: ReturnType<typeof useUserStore>;

  beforeEach(() => {
    const testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
    userStore = useUserStore(testPinia);
  });

  describe("getEreignisArtForDateRelatedToSchliessungsuhrzeitForUWB", () => {
    beforeEach(() => {
      const user = new User();
      user.wahlbezirksArt = WahlbezirksArtEnum.UWB;
      userStore.setUser(user);
    });

    it("should_returnVorfall_when_schliessungsuhrzeitIsNotSetAndUwb", () => {
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
      const user = new User();
      user.wahlbezirksArt = WahlbezirksArtEnum.BWB;
      userStore.setUser(user);
    });

    it("should_returnVorkomniss_when_schliessungsuhrzeitIsNotSetAnd", () => {
      const result = getEreignisArtForDateRelatedToSchliessungsuhrzeit(
        new Date(),
        undefined
      );

      expect(result).toStrictEqual(EreignisartEnum.Vorkommnis);
    });

    it("should_returnVorkomniss_when_schliessungsuhrzeitIsAfterEreignisDate", () => {
      const schliessungsuhrzeit = new Date();
      const result = getEreignisArtForDateRelatedToSchliessungsuhrzeit(
        new Date(schliessungsuhrzeit.getTime() - 1),
        schliessungsuhrzeit
      );

      expect(result).toStrictEqual(EreignisartEnum.Vorkommnis);
    });

    it("should_returnVorkomniss_when_schliessungsuhrzeitIsEqualEreignisDate", () => {
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
