export const EreignisartEnum = {
  Vorfall: "VORFALL",
  Vorkommnis: "VORKOMMNIS",
} as const;

export type EreignisartEnum =
  (typeof EreignisartEnum)[keyof typeof EreignisartEnum];

export function getEreignisArtForDateRelatedToSchliessungsuhrzeit(
  ereignisDate: Date,
  schliessungsuhrzeit: Date | undefined
): EreignisartEnum {
  if (!schliessungsuhrzeit) {
    return EreignisartEnum.Vorfall;
  } else {
    return ereignisDate.getTime() > schliessungsuhrzeit.getTime()
      ? EreignisartEnum.Vorkommnis
      : EreignisartEnum.Vorfall;
  }
}
