import { useWahlbriefdatenTestDataFactory } from "@tests/utils/briefwahl/WahlbriefdatenTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useBriefwahlMapper } from "@/composables/briefwahl/briefwahlMapper.ts";

describe("briefwahlMapper.ts", () => {
  const { prepareWahlbriefdatenDTO, prepareWahlbriefdaten } =
    useWahlbriefdatenTestDataFactory();

  const { toWahlbriefdatenModel } = useBriefwahlMapper();

  describe("toWahlbriefdatenModel", () => {
    it("should_returnWahlbriefdatenModel_when_dtoIsGiven", () => {
      const zeitNachtraeglicheUeberbracht = "2025-04-28T13:20:11";
      const dto = prepareWahlbriefdatenDTO()
        .zeitNachtraeglichUeberbrachte(zeitNachtraeglicheUeberbracht)
        .build();

      const result = toWahlbriefdatenModel(dto);

      const expecetedResult = prepareWahlbriefdaten()
        .wahlbriefe(dto.wahlbriefe)
        .verzeichnisseUngueltige(dto.verzeichnisseUngueltige)
        .nachtraege(dto.nachtraege)
        .nachtraeglichUeberbrachte(dto.nachtraeglichUeberbrachte)
        .zeitNachtraeglichUeberbrachte(new Date(zeitNachtraeglicheUeberbracht))
        .build();

      expect(result).toStrictEqual(expecetedResult);
    });

    it("should_returnWahlbriefdatenModelWithUndefinedZeit_when_dtoIsGivenWithUndefinedZeit", () => {
      const zeitNachtraeglicheUeberbracht = undefined;
      const dto = prepareWahlbriefdatenDTO()
        .zeitNachtraeglichUeberbrachte(zeitNachtraeglicheUeberbracht)
        .build();

      const result = toWahlbriefdatenModel(dto);

      const expecetedResult = prepareWahlbriefdaten()
        .wahlbriefe(dto.wahlbriefe)
        .verzeichnisseUngueltige(dto.verzeichnisseUngueltige)
        .nachtraege(dto.nachtraege)
        .nachtraeglichUeberbrachte(dto.nachtraeglichUeberbrachte)
        .zeitNachtraeglichUeberbrachte(zeitNachtraeglicheUeberbracht)
        .build();

      expect(result).toStrictEqual(expecetedResult);
    });
  });
});
