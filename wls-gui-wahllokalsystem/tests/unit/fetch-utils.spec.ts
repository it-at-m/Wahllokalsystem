import { afterEach, describe, expect, it, vi } from "vitest";

import { wlsResponseHandler } from "@/api/fetch-utils";

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
});
