import { createPinia, setActivePinia } from "pinia";
import {
  afterAll,
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";

import { useBeschlussentscheidungenDruckenTools } from "@/composables/dse/beschlussfassung/beschlussentscheidungenDruckenTools.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  logError: vi.fn(),
  postAusdruck: vi.fn(),
}));

vi.mock(import("@/composables/common/logging.ts"), async (importOriginal) => {
  const mod = await importOriginal();
  return {
    useLogging: () => ({
      ...mod.useLogging("stimmzettelErfassungViewUtils"),
      logError: mockDefinitions.logError,
    }),
  };
});
vi.mock(
  import("@/composables/ergebnismeldung/common/ausdruckService.ts"),
  () => ({
    useAusdruckService: () => ({
      postAusdruck: mockDefinitions.postAusdruck,
    }),
  })
);

describe("beschlussentscheidungenDruckenTools", () => {
  const wahlID = "wahlID";
  const wahlbezirkID = "wahlbezirkID";

  let unitUnderTest: ReturnType<typeof useBeschlussentscheidungenDruckenTools>;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useBeschlussentscheidungenDruckenTools(
      wahlID,
      wahlbezirkID
    );
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe("sendAusdruckBeschlussentscheidungen", () => {
    it("should_callPostAusdruckWithGivenParameters_when_apiCallSucceeded", async () => {
      mockDefinitions.postAusdruck.mockResolvedValue(null);

      await unitUnderTest.sendAusdruckBeschlussentscheidungen(
        MeldungsArtEnum.Schnellmeldung,
        "<html>ausdruck</html>"
      );

      expect(mockDefinitions.postAusdruck).toHaveBeenCalledTimes(1);
      expect(mockDefinitions.postAusdruck).toHaveBeenCalledWith(
        wahlbezirkID,
        wahlID,
        MeldungsArtEnum.Schnellmeldung,
        "<html>ausdruck</html>"
      );
      expect(mockDefinitions.logError).not.toHaveBeenCalled();
    });

    it("should_logError_when_postAusdruckFailed", async () => {
      mockDefinitions.postAusdruck.mockRejectedValue(
        new Error("mocked failed api call")
      );

      await unitUnderTest.sendAusdruckBeschlussentscheidungen(
        MeldungsArtEnum.Schnellmeldung,
        "pdf-bytes"
      );

      expect(mockDefinitions.postAusdruck).toHaveBeenCalledTimes(1);
      expect(mockDefinitions.logError).toHaveBeenCalledWith(
        "Fehler beim Speichern des Ausdrucks"
      );
    });
  });
});
