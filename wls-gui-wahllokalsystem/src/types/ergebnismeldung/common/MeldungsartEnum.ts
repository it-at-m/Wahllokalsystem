export const MeldungsArtEnum = {
  Schnellmeldung: "SCHNELLMELDUNG",
  Niederschrift: "NIEDERSCHRIFT",
} as const;

export type MeldungsartEnum =
  (typeof MeldungsArtEnum)[keyof typeof MeldungsArtEnum];
