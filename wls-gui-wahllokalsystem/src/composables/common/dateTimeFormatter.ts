import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";

export function useDateTimeFormatter() {
  const NO_VALUE_DEFAULT = "";
  const TIME_FIELD_SEPARATOR = ":";

  const { isValidDate } = useDateTimeUtils();

  const toHhMmSs = function (date?: Date | null): string {
    if (!date) {
      return NO_VALUE_DEFAULT;
    }

    const hour = _leftPadTwoDigitsWithZero(date.getHours());
    const minute = _leftPadTwoDigitsWithZero(date.getMinutes());
    const second = _leftPadTwoDigitsWithZero(date.getSeconds());

    return `${hour}${TIME_FIELD_SEPARATOR}${minute}${TIME_FIELD_SEPARATOR}${second}`;
  };

  const toHhMm = function (date?: Date | null): string {
    if (!date) {
      return NO_VALUE_DEFAULT;
    }

    const hour = _leftPadTwoDigitsWithZero(date.getHours());
    const minute = _leftPadTwoDigitsWithZero(date.getMinutes());

    return `${hour}${TIME_FIELD_SEPARATOR}${minute}`;
  };

  function toGermanDate(dateString: string) {
    const date = new Date(dateString);
    if (isValidDate(date)) {
      return date.toLocaleDateString("de-DE", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
      });
    }
  }

  function toGermanDateWithLongMonth(dateString: string) {
    const date = new Date(dateString);
    if (isValidDate(date)) {
      return date.toLocaleDateString("de-DE", {
        year: "numeric",
        month: "long",
        day: "numeric",
        hour12: false,
      });
    }
  }

  function toYyyyMmDd(dateToFormat: Date) {
    if (!isValidDate(dateToFormat)) {
      return NO_VALUE_DEFAULT;
    }

    return `${_leftPadFourDigitsWithZero(dateToFormat.getFullYear())}-${_leftPadTwoDigitsWithZero(dateToFormat.getMonth() + 1)}-${_leftPadTwoDigitsWithZero(dateToFormat.getDate())}`;
  }

  function _leftPadTwoDigitsWithZero(number: number): string {
    return _leftPadWithZero(number, 2);
  }

  function _leftPadFourDigitsWithZero(number: number): string {
    return _leftPadWithZero(number, 4);
  }

  function _leftPadWithZero(number: number, padLength: number): string {
    return `${number}`.padStart(padLength, "0");
  }

  return {
    toHhMmSs,
    toHhMm,
    toYyyyMmDd,
    toGermanDate,
    toGermanDateWithLongMonth,
  };
}
