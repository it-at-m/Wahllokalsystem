import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";

const { createTodayWithTime } = useDateTimeUtils();

export function useRules() {
  /* eslint-disable */
  const required = (value: any) => {
    if (typeof value === "string") {
      return value.trim().length > 0 || "Feld darf nicht leer sein.";
    } else if (typeof value === "number") {
      return !isNaN(value) || "Feld darf nicht leer sein.";
    }
    return "Feld darf nicht leer sein.";
  };

  const maxLength = (length: number) => (value: any) =>
    (value && value.length <= length) ||
    `Maximale Länge ist ${length} Zeichen.`;

  const minLength = (length: number) => (value: any) =>
    (value && value.length >= length) ||
    `Minimale Länge ist ${length} Zeichen.`;

  const maxNumber = (max: number) => (value: number) =>
    value <= max || `Eingabe darf nicht größer als ${max} sein.`;

  const minNumber = (min: number) => (value: number) =>
    value >= min || `Eingabe darf nicht kleiner als ${min} sein.`;

  const timeNotInFuture = (value: string) =>
    createTodayWithTime(value) <= new Date() ||
    `Eingabe darf nicht in der Zukunft liegen.`;

  const timeGreaterOrEqual = (compareValue: string) => (value: string) =>
    createTodayWithTime(value) >= createTodayWithTime(compareValue) ||
    `Eingabe muss größer oder gleich ${compareValue} sein.`;

  const timeLessOrEqual = (compareValue: string) => (value: string) =>
    createTodayWithTime(value) <= createTodayWithTime(compareValue) ||
    `Eingabe muss kleiner oder gleich ${compareValue} sein.`;

  return {
    required,
    maxLength,
    minLength,
    maxNumber,
    minNumber,
    timeNotInFuture,
    timeGreaterOrEqual,
    timeLessOrEqual,
  };
}
