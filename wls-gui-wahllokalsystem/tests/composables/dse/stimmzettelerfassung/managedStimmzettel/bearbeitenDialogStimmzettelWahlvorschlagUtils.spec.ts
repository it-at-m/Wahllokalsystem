import { useStimmzettelTestDataFactory } from "@tests/utils/dse/StimmzettelTestDataFactory.ts";
import { describe, expect, it } from "vitest";
import { ref } from "vue";

import { useBearbeitenDialogStimmzettelWahlvorschlagUtils } from "@/composables/dse/stimmzettelerfassung/bearbeitenDialogStimmzettel/bearbeitenDialogStimmzettelWahlvorschlagUtils.ts";

describe("bearbeitenDialogStimmzettelWahlvorschlagUtils.ts", () => {
  const { prepareStimmzettel, prepareStimmzettelWahlvorschlag } =
    useStimmzettelTestDataFactory();

  describe("getWahlvorschlagByOrdnungszahl", () => {
    it("should_findWahlvorschlagByOrdnungszahl_when_called", () => {
      const wv1 = prepareStimmzettelWahlvorschlag().ordnungszahl(1).build();
      const wv2 = prepareStimmzettelWahlvorschlag().ordnungszahl(2).build();
      const stimmzettel = prepareStimmzettel()
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
