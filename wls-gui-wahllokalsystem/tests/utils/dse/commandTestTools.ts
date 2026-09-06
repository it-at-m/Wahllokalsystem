export const validKandidatOrdnungszahlen: number[] = [
  101, 110, 199, 201, 210, 299, 999, 1001, 1010, 1099, 9999,
];

export const invalidKandidatOrdnungszahlenCommand: string[] = [
  "10",
  "abc",
  "0101",
  "100",
  "1000",
  "900",
  "9900",
  "101+0",
];

export const validRanges: [number, number][] = [
  [101, 103],
  [201, 299],
  [1099, 1010],
];

export const invalidCommandRanges: string[] = [
  "10-101",
  "abc-200",
  "100-1000",
  "101-100",
  "101-1000",
  "101-",
  "125-203",
];

export const validWahlvorschlagOrdnungszahlen = [
  1, 2, 9, 10, 11, 99, 100, 1000,
];

export const invalidWahlvorschlagOrdnungszahlen = [
  "0",
  "abc",
  "010",
  "101",
  "999",
  "1001",
  "9999",
];
