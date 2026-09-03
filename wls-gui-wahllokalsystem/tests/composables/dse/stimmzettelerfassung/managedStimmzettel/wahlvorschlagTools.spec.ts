import { useManagedStimmzettelTestDataFactory } from "@tests/utils/dse/ManagedStimmzettelTestDataFactory.ts";
import { describe, expect, it } from "vitest";
import { ref } from "vue";

import { useWahlvorschlagTools } from "@/composables/dse/stimmzettelerfassung/managedStimmzettel/wahlvorschlagTools.ts";

describe("wahlvorschlagTools.ts", () => {
  const {
    prepareManagedStimmzettelStimmzettel,
    prepareManagedStimmzettelWahlvorschlag,
  } = useManagedStimmzettelTestDataFactory();

  it("should_findWahlvorschlagByOrdnungszahl_when_called", () => {
    const wv1 = prepareManagedStimmzettelWahlvorschlag()
      .ordnungszahl(1)
      .build();
    const wv2 = prepareManagedStimmzettelWahlvorschlag()
      .ordnungszahl(2)
      .build();
    const stimmzettel = prepareManagedStimmzettelStimmzettel()
      .wahlvorschlaege([wv1, wv2])
      .build();

    const { getWahlvorschlagByOrdnungszahl } = useWahlvorschlagTools(
      ref(stimmzettel)
    );

    expect(getWahlvorschlagByOrdnungszahl(1)).toStrictEqual(wv1);
    expect(getWahlvorschlagByOrdnungszahl(2)).toStrictEqual(wv2);
    expect(getWahlvorschlagByOrdnungszahl(3)).toBeUndefined();
  });
});
