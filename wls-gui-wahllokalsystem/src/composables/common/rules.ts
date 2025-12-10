import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";

const { isValidDate, createTodayWithTime } = useDateTimeUtils();
const { toGermanDate } = useDateTimeFormatter();

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

  const timeGreaterOrEqual = (compareValue: string) => (value: string) => {
    let formattedErrorValue = _formatToHhMm(compareValue);
    return (
      createTodayWithTime(value) >= createTodayWithTime(compareValue) ||
      `Eingabe muss größer oder gleich ${formattedErrorValue} sein.`
    );
  };

  const timeLessOrEqual = (compareValue: string) => (value: string) => {
    let formattedErrorValue = _formatToHhMm(compareValue);
    return (
      createTodayWithTime(value) <= createTodayWithTime(compareValue) ||
      `Eingabe muss kleiner oder gleich ${formattedErrorValue} sein.`
    );
  };

  const dateNotInFuture = (value: string) => {
    const date = new Date(value);
    if (isValidDate(date)) {
      return date <= new Date() || `Datum darf nicht in der Zukunft liegen.`;
    } else {
      return `Ungültiges Datum`;
    }
  };

  const dateGreaterOrEqual = (compareValue: string) => (value: string) => {
    const date = new Date(value);
    const compareDate = new Date(compareValue);
    if (isValidDate(date) && isValidDate(compareDate)) {
      return (
        date >= compareDate ||
        `Datum darf nicht vor dem ${toGermanDate(compareValue)} liegen.`
      );
    } else {
      return `Ungültiges Datum`;
    }
  };

  function _formatToHhMm(compareValue: string) {
    let formattedErrorValue;
    if (/^\d{1,2}:\d{2}(:\d{2})?$/.test(compareValue)) {
      const parts = compareValue.split(":");
      const hh = parts[0];
      const mm = parts[1];
      formattedErrorValue = hh + ":" + mm;
    }
    return formattedErrorValue;
  }

  return {
    required,
    maxLength,
    minLength,
    maxNumber,
    minNumber,
    timeNotInFuture,
    timeGreaterOrEqual,
    timeLessOrEqual,
    dateNotInFuture,
    dateGreaterOrEqual,
  };
}
