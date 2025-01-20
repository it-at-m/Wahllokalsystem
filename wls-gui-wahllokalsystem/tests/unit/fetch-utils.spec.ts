import { afterEach, describe, expect, it, vi } from "vitest";

import { wlsCatchHandler, wlsResponseHandler } from "@/api/fetch-utils";

const mockCreateDefaultWlsError = vi.fn();
// hier wird vi.doMock eingesetzt, um das Hoisting-Problem zu umgehen, dass vi.mock vor allen imports ausgeführt wird.
// nur mit vi.mock() würde es die Fehlermeldung "Error: [vitest] There was an error when mocking a module. If you are using "vi.mock" factory, make sure there are no top level variables inside, since this call is hoisted to top of the file." geben
// vi.doMock wird erst nach den Imports aufgerufen, bzw. genau dort ausgeführt, wo es im Code steht.
vi.doMock("@/api/WLSError", () => ({
  createDefaultWlsError: mockCreateDefaultWlsError,
}));

describe("WLS Fetch Utils", () => {
  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("WlsResponseHandler", () => {
    // Parametrized Test, um verschiedene Szenarien (response codes) zu testen
    // mit `$variablenname` kann der entsprechende Parameter (response code) mit im Testnamen aufgenommen werden.
    it.each([
      { response: new Response(null, { status: 204 }) },
      { response: new Response('{"error": "unauthorized"}', { status: 401 }) },
      { response: new Response('{"error": "not found"}', { status: 404 }) },
      { response: new Response('{"error": "bad request"}', { status: 400 }) },
      {
        response: new Response('{"error": "internal server error"}', {
          status: 500,
        }),
      },
    ])(
      "should_returnRejectedResponse_when_responseCodeIs$response.status",
      async ({ response }) => {
        await expect(wlsResponseHandler(response)).rejects.toEqual(response);
      }
    );

    it("should_returnResolvedResponse_when_responseCodeIs200", async () => {
      const response = new Response('{"data": "test"}', { status: 200 });
      await expect(wlsResponseHandler(response)).resolves.toEqual(response);
    });
  });

  describe("WlsCatchHandler", () => {
    it("should_throwWlsErrorWithNoContentMessage_when_responseCodeIs204", async () => {
      const mockedResponse = new Response(null, { status: 204 });
      const errorMessage = "Es konnten keine Daten gefunden werden";

      // definiert das Verhalten der Methode
      mockCreateDefaultWlsError.mockResolvedValueOnce(new Error(errorMessage));

      // expect(wlsCatchHandler()).toThrow ohne Arrow-Funktion wirft den fehler: "Error: Es konnten keine Daten gefunden werden",
      // weil die Funktion tatsächlich ausgeführt wird. Mit der Kapselung in die Arrow-Funktion kann der Test vorher erkennen, dass
      // ein Fehler geworfen wird. Das expect-Statement führt dann die Funktion aus und überprüft, ob die erwartete Exception auftritt.
      expect(() => wlsCatchHandler(mockedResponse)).toThrow(errorMessage);
    });

    it("should_throwWlsErrorWithBadRequestMessage_when_responseCodeIs400", async () => {
      const mockedResponse = new Response('{"error": "bad request"}', {
        status: 400,
      });
      const errorMessage = "Ungültige Anfrage";

      mockCreateDefaultWlsError.mockResolvedValueOnce(new Error(errorMessage));

      await expect(() => wlsCatchHandler(mockedResponse)).rejects.toThrow(
        errorMessage
      );
    });

    it.each([
      { response: new Response('{"error": "unauthorized"}', { status: 401 }) },
      { response: new Response('{"error": "not found"}', { status: 404 }) },
      {
        response: new Response('{"error": "internal server error"}', {
          status: 500,
        }),
      },
    ])(
      "should_throwWlsErrorWithDefaultErrorMessage_when_responseCodeIs$response.status",
      async ({ response }) => {
        const errorMessage = "Ein unbekannter Fehler ist aufgetreten";

        mockCreateDefaultWlsError.mockResolvedValueOnce(
          new Error(errorMessage)
        );

        await expect(() => wlsCatchHandler(response)).rejects.toThrow(
          errorMessage
        );
      }
    );
  });
});
