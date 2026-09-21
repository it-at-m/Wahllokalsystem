import { useDseStimmzettelTestDataFactory } from "@tests/utils/dse/DseStimmzettelTestDataFactory.ts";
import { describe, expect, it } from "vitest";
import { ref } from "vue";

import { useBearbeitenDialogStimmzettelWahlvorschlagUtils } from "@/composables/dse/stimmzettelerfassung/bearbeitenDialogStimmzettel/bearbeitenDialogStimmzettelWahlvorschlagUtils.ts";

describe("bearbeitenDialogStimmzettelWahlvorschlagUtils.ts", () => {
  const { prepareDseStimmzettel, prepareDseWahlvorschlag } =
    useDseStimmzettelTestDataFactory();

  describe("getWahlvorschlagByOrdnungszahl", () => {
    it("should_findWahlvorschlagByOrdnungszahl_when_called", () => {
      const wv1 = prepareDseWahlvorschlag().ordnungszahl(1).build();
      const wv2 = prepareDseWahlvorschlag().ordnungszahl(2).build();
      const stimmzettel = prepareDseStimmzettel()
        .wahlvorschlaege([wv1, wv2])
        .build();

      const { getWahlvorschlagByOrdnungszahl } =
        useBearbeitenDialogStimmzettelWahlvorschlagUtils(ref(stimmzettel));

      expect(getWahlvorschlagByOrdnungszahl(1)).toStrictEqual(wv1);
      expect(getWahlvorschlagByOrdnungszahl(2)).toStrictEqual(wv2);
      expect(getWahlvorschlagByOrdnungszahl(3)).toBeUndefined();
    });
  });
});
