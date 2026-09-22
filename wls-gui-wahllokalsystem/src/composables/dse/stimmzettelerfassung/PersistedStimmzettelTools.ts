import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";

export function usePersistedStimmzettelTools() {
  function matchesMBWStapelA(stimmzettel: PersistedStimmzettel) {
    return false;
  }

  function matchesMBWStapelB(stimmzettel: PersistedStimmzettel) {
    return false;
  }

  function matchesMBWStapelBC(stimmzettel: PersistedStimmzettel) {
    return false;
  }

  function matchesMBWStapelDUngueltig(stimmzettel: PersistedStimmzettel) {
    return false;
  }

  function matchesMBWStapelEUngueltig(stimmzettel: PersistedStimmzettel) {
    return false;
  }

  return {
    matchesMBWStapelA,
    matchesMBWStapelB,
    matchesMBWStapelBC,
    matchesMBWStapelDUngueltig,
    matchesMBWStapelEUngueltig,
  };
}
