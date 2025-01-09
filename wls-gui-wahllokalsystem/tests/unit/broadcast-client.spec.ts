import { afterEach, describe, expect, it, vi } from "vitest";

import * as api from "@/api/wls-clients/broadcast-service/broadcast-client";

global.fetch = vi.fn();

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
    // Erstellen einer gemockten Response
    const mockedResponse = new Response(
      JSON.stringify(mockedBroadcastMessage),
      { status: 200 }
    );
    // gibt die gemockte response zurück, wenn die fetch funktion aufgerufen wird
    fetch.mockResolvedValue(mockedResponse);

    const response = await api.getBroadcastMessage(wahlbezirkId);
    const result = await response.json();

    expect(result).toEqual(mockedBroadcastMessage);
    expect(fetch).toHaveBeenCalledWith(
      `${api.BROADCAST_API_URL}getMessage/${wahlbezirkId}`,
      expect.anything() // ignoriert den zweiten parameter, der an fetch übergeben wird
    );
  });

  it("should_throwError_when_noMessageFound", async () => {
    const mockedResponse = new Response(null, { status: 204 });
    fetch.mockResolvedValue(mockedResponse);

    await expect(api.getBroadcastMessage("123")).rejects.toThrow(
      "Es konnten keine Daten gefunden werden"
    );
  });

  it.each([
    [""], // empty id
    [" "], // blank id
  ])(
    'should_throwWlsError_when_calledWithWrongOrMissingWahlbezirkID: "%s"',
    async (wahlbezirkID: string) => {
      // todo: damit läuft der test durch, aber ist das richtig? müsste es nicht auch ohne mocken einen fehler werfen?
      fetch.mockResolvedValue(new Response(null, { status: 400 }));
      await expect(api.getBroadcastMessage(wahlbezirkID)).rejects.toThrow(
        "Ungültige Anfrage"
      );
    }
  );
});
