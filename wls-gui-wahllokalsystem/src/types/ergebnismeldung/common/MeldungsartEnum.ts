export const MeldungsArtEnum = {
  Schnellmeldung: "SCHNELLMELDUNG",
  Niederschrift: "NIEDERSCHRIFT",
  Beschlussentscheidungen: "BESCHLUSSENTSCHEIDUNGEN",
} as const;

export type MeldungsartEnum =
  (typeof MeldungsArtEnum)[keyof typeof MeldungsArtEnum];
