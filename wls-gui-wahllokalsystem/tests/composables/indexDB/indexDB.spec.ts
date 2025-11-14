import type { MockInstance } from "vitest";

import { useIndexDBValueTestDataFactory } from "@tests/utils/indexDB/IndexDBValueTestDataFactory.ts";
import localforage from "localforage";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useIndexDB } from "@/composables/indexDB/indexDB.ts";

const { prepareIndexDBValue } = useIndexDBValueTestDataFactory();

vi.mock("localforage");

describe("indexDB.ts", () => {
  let unitUnderTest: ReturnType<typeof useIndexDB>;
  let consoleMock: MockInstance;

  beforeEach(() => {
    unitUnderTest = useIndexDB();
    consoleMock = vi
      .spyOn(console, "error")
      .mockImplementation(() => undefined);
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.restoreAllMocks();
  });

  describe("getDirtyItems", () => {
    const indexDBValueDirty1 = prepareIndexDBValue().dirty(true).build();
    const indexDBValueDirty2 = prepareIndexDBValue().dirty(true).build();
    const indexDBValueNotDirty1 = prepareIndexDBValue().dirty(false).build();
    const indexDBValueNotDirty2 = prepareIndexDBValue().dirty(false).build();

    it("should_getItems_when_markedAsDirty", async () => {
      const mockItems = [
        indexDBValueNotDirty1,
        indexDBValueDirty1,
        indexDBValueDirty2,
        indexDBValueNotDirty2,
      ];

      vi.spyOn(localforage, "iterate").mockImplementation((callback) => {
        return new Promise<void>((resolve) => {
          mockItems.forEach((item, index) => {
            callback(item, `key${index}`, index);
          });
          resolve();
        });
      });

      const result = await unitUnderTest.getDirtyItems();

      expect(result).toEqual([
        { key: "key1", item: indexDBValueDirty1 },
        { key: "key2", item: indexDBValueDirty2 },
      ]);
    });

    it("should_getNoItems_when_notMarkedAsDirty", async () => {
      const mockItems = [indexDBValueNotDirty1, indexDBValueNotDirty2];

      vi.spyOn(localforage, "iterate").mockImplementation((callback) => {
        return new Promise<void>((resolve) => {
          mockItems.forEach((item, index) => {
            callback(item, `key${index}`, index);
          });
          resolve();
        });
      });

      const result = await unitUnderTest.getDirtyItems();

      expect(result).toEqual([]);
    });
  });

  describe("getItemFromIDB", () => {
    it("should_returnAnItem_when_ItemWithKeyExists", async () => {
      const key = "test";
      const mockItem = prepareIndexDBValue().build();
      vi.spyOn(localforage, "getItem").mockReturnValueOnce(
        Promise.resolve(mockItem)
      );

      const result = await unitUnderTest.getItemFromIDB(key);

      expect(result).toEqual(mockItem);
      expect(localforage.getItem).toHaveBeenCalledWith(key);
    });

    it("should_returnNull_when_ItemWithKeyNotExists", async () => {
      const key = "test";
      const error = "Error";
      vi.spyOn(localforage, "getItem").mockRejectedValue(error);

      const result = await unitUnderTest.getItemFromIDB(key);

      expect(localforage.getItem).toHaveBeenCalledWith(key);
      expect(consoleMock).toHaveBeenCalledWith(
        expect.stringContaining("useIndexDB: Fehler beim Laden aus IDB:"),
        error
      );
      expect(result).toEqual(null);
    });
  });

  describe("setupIndexDB", () => {
    it("should_setupIndexDB_when_called", () => {
      unitUnderTest.setupIndexDB();

      expect(localforage.config).toHaveBeenCalledWith({
        driver: localforage.INDEXEDDB,
        name: "wahldb",
        version: 1.0,
        storeName: "wahlstore",
        description: "store for data of electoral district",
      });
    });
  });

  describe("storeItem", () => {
    it("should_setItemInIndexDB_when_callStoreItem", async () => {
      const key = "test";
      const data = prepareIndexDBValue().build();

      await unitUnderTest.storeItem(key, data);

      expect(localforage.setItem).toHaveBeenCalledWith(key, data);
    });
  });
});
