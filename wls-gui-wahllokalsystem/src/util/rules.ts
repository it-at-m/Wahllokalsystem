/* eslint-disable */
export const REQUIRED = (value: any) => {
  if (typeof value === "string") {
    return value.trim().length > 0 || "Feld darf nicht leer sein.";
  } else if (typeof value === "number") {
    return value >= 0 || "Feld darf nicht leer sein.";
  }
  return "Feld darf nicht leer sein.";
};

export const MAX_LENGTH = (length: number) => (value: any) =>
  (value && value.length <= length) || `Maximale Länge ist ${length} Zeichen.`;

export const MIN_LENGTH = (length: number) => (value: any) =>
  (value && value.length >= length) || `Minimale Länge ist ${length} Zeichen.`;

export const MAX_NUMBER = (max: number) => (value: number) =>
  value <= max || `Eingabe darf nicht größer als ${max} sein.`;

export const MIN_NUMBER = (min: number) => (value: number) =>
  value >= min || `Eingabe darf nicht kleiner als ${min} sein.`;

export const NO_NEGATIVE_INPUT = MIN_NUMBER(0);
