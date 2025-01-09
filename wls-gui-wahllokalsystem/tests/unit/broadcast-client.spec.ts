import { afterEach, describe, expect, it, vi } from "vitest";

import {
  BROADCAST_API_URL,
  getBroadcastMessage,
} from "@/api/wls-clients/broadcast-service/broadcast-client";

describe("GetBroadcastMessage", () => {
  afterEach(() => {
    vi.clearAllMocks();
  });

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
