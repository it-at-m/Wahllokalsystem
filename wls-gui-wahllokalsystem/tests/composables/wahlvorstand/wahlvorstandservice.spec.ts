import { useWahlvorstandTestDataFactory } from "@tests/utils/wahlvorstand/WahlvorstandTestDataFactory.ts";
import { afterEach, describe, expect, it, vi } from "vitest";

import { useWahlvorstandService } from "@/composables/wahlvorstand/wahlvorstandService.ts";

const mockDefinitions = vi.hoisted(() => ({
  getWahlvorstand: vi.fn(),
  mapDtoToModel: vi.fn(),
}));

vi.mock(
  import("@/api/wls-clients/generated-wahlvorstand-api"),
  async (importOriginal) => {
    const mod = await importOriginal();
    return {
      ...mod,
      WahlvorstandControllerApi: vi.fn().mockImplementation(() => ({
        getWahlvorstand: mockDefinitions.getWahlvorstand,
      })),
      Configuration: vi.fn(),
    };
  }
);

vi.mock("@/composables/wahlvorstand/wahlvorstandMapper", () => ({
  useWahlvorstandMapper: () => ({
    toModel: mockDefinitions.mapDtoToModel,
  }),
}));

const { createWahlvorstandDTO } = useWahlvorstandTestDataFactory();

describe("WahlvorstandService.ts", () => {
  const unitUnderTest = useWahlvorstandService();

  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
  });

  describe("getWahlvorstand", () => {
    it.each([
      { forceUpdate: true, expectedApiCallHeader: true },
      { forceUpdate: false, expectedApiCallHeader: false },
      { forceUpdate: undefined, expectedApiCallHeader: false },
    ])(
      "should_callApiWithForceUpdateEquals'$expectedApiCallHeader'_when_calledWithForceUpdateEquals'$forceUpdate'",
      async ({ forceUpdate, expectedApiCallHeader }) => {
        const wahlbezirkID = "wahlbezirkID";

        mockDefinitions.getWahlvorstand.mockReturnValue(
          Promise.resolve({ data: createWahlvorstandDTO() })
        );

        await unitUnderTest.getWahlvorstand(wahlbezirkID, forceUpdate);

        expect(mockDefinitions.getWahlvorstand.mock.calls).toStrictEqual([
          [wahlbezirkID, expectedApiCallHeader],
        ]);
      }
    );
  });
});
