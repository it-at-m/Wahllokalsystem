import { afterEach, describe, expect, it, vi } from "vitest";

import {
  BROADCAST_API_URL,
  broadcastMessageRead,
  getBroadcastMessage,
  postBroadcastMessage,
} from "@/api/wls-clients/broadcast-service/broadcast-client";

describe("Broadcast Service API", () => {
  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("GetBroadcastMessage", () => {
    it("should_returnMessageForWahlbezirk_when_messageFoundAndCalledWithCorrectParams", async () => {
      const wahlbezirkId = "id12345";
      const mockedBroadcastMessage = {
        oid: "123456789",
        wahlbezirkIDs: [wahlbezirkId],
        nachricht: "Neue Nachricht",
      };

      // gibt die gemockte response zurück, wenn die fetch funktion aufgerufen wird
      global.fetch = vi.fn(() =>
        Promise.resolve(
          new Response(JSON.stringify(mockedBroadcastMessage), { status: 200 })
        )
      );

      const response = await getBroadcastMessage(wahlbezirkId);
      const result = await response.json();

      expect(result).toEqual(mockedBroadcastMessage);
      expect(fetch).toHaveBeenCalledWith(
        `${BROADCAST_API_URL}getMessage/${wahlbezirkId}`,
        expect.anything() // ignoriert den zweiten parameter, der an fetch übergeben wird
      );
    });

    it("should_throwError_when_noMessageFound", async () => {
      global.fetch = vi.fn(() =>
        Promise.resolve(new Response(null, { status: 204 }))
      );

      await expect(getBroadcastMessage("123")).rejects.toThrow(
        "Es konnten keine Daten gefunden werden"
      );
    });

    it.each([
      [""], // empty id
      [" "], // blank id
    ])(
      'should_throwWlsError_when_calledWithWrongOrMissingWahlbezirkID: "%s"',
      async (wahlbezirkID: string) => {
        global.fetch = vi.fn(() =>
          Promise.resolve(new Response(null, { status: 400 }))
        );

        await expect(getBroadcastMessage(wahlbezirkID)).rejects.toThrow(
          "Ungültige Anfrage"
        );
      }
    );
  });

  describe("PostBroadcastMessage", () => {
    it("should_postMessageSuccessfully_when_calledWithCorrectParams", async () => {
      const mockedResponse = { error: null };
      global.fetch = vi.fn(() =>
        Promise.resolve(
          new Response(JSON.stringify(mockedResponse), { status: 200 })
        )
      );
      const response = await postBroadcastMessage(["id"], "message");
      const result = await response.json();

      expect(result).toEqual(mockedResponse);
      expect(fetch).toHaveBeenCalledWith(
        `${BROADCAST_API_URL}broadcast`,
        expect.anything()
      );
    });

    it.each([
      [[""], "message"], // empty id
      [[" "], "message"], // blank id
      [["123"], ""], // empty message
      [["123"], " "], // blank message
    ])(
      'should_throwWlsError_when_calledWithWrongOrMissingParams: "%s"',
      async (wahlbezirkIDs: string[], message: string) => {
        global.fetch = vi.fn(() =>
          Promise.resolve(new Response(null, { status: 400 }))
        );

        await expect(
          postBroadcastMessage(wahlbezirkIDs, message)
        ).rejects.toThrow("Ungültige Anfrage");
      }
    );
  });

  describe("BroadcastMessageRead", () => {
    it("should_postMessageReadStausSuccessfully_whenCalledWithCorrectParams", async () => {
      const nachrichtID = "messageId123";
      const mockedResponse = { error: null };
      global.fetch = vi.fn(() =>
        Promise.resolve(
          new Response(JSON.stringify(mockedResponse), { status: 200 })
        )
      );
      const response = await broadcastMessageRead(nachrichtID);
      const result = await response.json();

      expect(result).toEqual(mockedResponse);
      expect(fetch).toHaveBeenCalledWith(
        `${BROADCAST_API_URL}messageRead/${nachrichtID}`,
        expect.anything()
      );
    });

    it("should_throwWlsError_when_postingMessageReadStatusFailed", async () => {
      global.fetch = vi.fn(() =>
        Promise.resolve(new Response(null, { status: 400 }))
      );

      await expect(broadcastMessageRead("")).rejects.toThrow(
        "Ungültige Anfrage"
      );
    });
  });
});
