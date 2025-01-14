import { afterEach, describe, expect, it, vi } from "vitest";

import { wlsCatchHandler, wlsResponseHandler } from "@/api/fetch-utils";

const { mockCreateDefaultWlsError } = vi.hoisted(() => ({
  mockCreateDefaultWlsError: vi.fn(),
}));

vi.mock("@/api/WLSError", () => ({
  createDefaultWlsError: mockCreateDefaultWlsError,
}));

/*const mockCreateDefaultWlsError = vi.fn();
// hier wird vi.doMock eingesetzt, um das Hoisting-Problem zu umgehen.
// nur mit vi.mock() würde es die Fehlermeldung "Error: [vitest] There was an error when mocking a module. If you are using "vi.mock" factory, make sure there are no top level variables inside, since this call is hoisted to top of the file." geben
// vi.doMock wird erst nach den Imports aufgerufen

vi.doMock("@/api/WLSError", () => ({
  createDefaultWlsError: mockCreateDefaultWlsError,
  rejectWithWlsError: vi.fn(),
}));*/

describe("WLS Fetch Utils", () => {
  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("WlsResponseHandler", () => {
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

      mockCreateDefaultWlsError.mockImplementation(() => {
        new Error(errorMessage);
      });

      expect(mockCreateDefaultWlsError).toHaveBeenCalledOnce;
      // expect(wlsCatchHandler()).toThrow ohne funktionsaufruf wirft den fehler: "Error: Es konnten keine Daten gefunden werden"
      expect(() => wlsCatchHandler(mockedResponse)).toThrow(errorMessage);
    });

    it("should_throwWlsErrorWithBadRequestMessage_when_responseCodeIs400", async () => {
      const mockedResponse = new Response(
        JSON.stringify({ error: "BadRequest" }),
        { status: 400 }
      );
      const errorMessage = "Ungültige Anfrage";

      mockCreateDefaultWlsError.mockImplementation(() => {
        new Error(errorMessage);
      });

      expect(mockCreateDefaultWlsError).toHaveBeenCalledOnce;
      await expect(wlsCatchHandler(mockedResponse)).rejects.toThrow(
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

        mockCreateDefaultWlsError.mockImplementation(() => {
          new Error(errorMessage);
        });

        expect(mockCreateDefaultWlsError).toHaveBeenCalledOnce;
        await expect(wlsCatchHandler(response)).rejects.toThrow(errorMessage);
      }
    );
  });
});
