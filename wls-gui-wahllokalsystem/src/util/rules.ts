import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";

const { getDateFromTimeString } = useDateTimeFormatter();

/* eslint-disable */
export const REQUIRED = (value: any) => {
  if (typeof value === "string") {
    return value.trim().length > 0 || "Feld darf nicht leer sein.";
  } else if (typeof value === "number") {
    return !isNaN(value) || "Feld darf nicht leer sein.";
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

export const TIME_NOT_IN_FUTURE = (value: string) =>
  getDateFromTimeString(value) <= new Date() ||
  `Eingabe darf nicht in der Zukunft liegen.`;

export const TIME_GREATER_THAN = (compareValue: string) => (value: string) =>
  getDateFromTimeString(value) >= getDateFromTimeString(compareValue) ||
  `Eingabe muss größer als ${compareValue} sein.`;

export const TIME_LESS_THAN = (compareValue: string) => (value: string) =>
  getDateFromTimeString(value) <= getDateFromTimeString(compareValue) ||
  `Eingabe muss kleiner als ${compareValue} sein.`;
