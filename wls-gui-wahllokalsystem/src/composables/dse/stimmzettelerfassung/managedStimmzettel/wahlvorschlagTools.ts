import type { Stimmzettel } from "@/types/dse/stimmzettelerfassung/Stimmzettel.ts";
import type { Ref } from "vue";

export function useWahlvorschlagTools(stimmzettel: Ref<Stimmzettel>) {
  function getWahlvorschlagByOrdnungszahl(ordnungszahl: number) {
    return stimmzettel.value.wahlvorschlaege.find(
      (wahlvorschlag) => wahlvorschlag.ordnungszahl === ordnungszahl
    );
  }

  return {
    getWahlvorschlagByOrdnungszahl,
  };
}
