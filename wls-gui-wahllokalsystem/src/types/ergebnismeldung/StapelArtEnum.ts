export const StapelArtEnum = {
  ObwA: "OBW_A",
  ObwBLeer: "OBW_B_LEER",
  ObwBUngekennzeichnet: "OBW_B_UNGEKENNZEICHNET",
  ObwCGueltig: "OBW_C_GUELTIG",
  ObwCUngueltig: "OBW_C_UNGUELTIG",
  SrwBawA: "SRW_BAW_A",
  SrwBawB: "SRW_BAW_B",
  SrwBawAB: "SRW_BAW_A_B",
  SrwBawDUngueltig: "SRW_BAW_D_UNGUELTIG",
  SrwBawBC: "SRW_BAW_B_C",
  StimmzettelUmschlaege: "STIMMZETTEL_UMSCHLAEGE",
} as const;

export type StapelArtEnum = (typeof StapelArtEnum)[keyof typeof StapelArtEnum];
