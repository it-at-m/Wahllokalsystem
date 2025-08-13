import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";

export function useDateTimeFormatter() {
  const NO_VALUE_DEFAULT = "";
  const TIME_FIELD_SEPARATOR = ":";

  const { isValidDate } = useDateTimeUtils();

  const time = function (date?: Date | null): string {
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

  const applyLocalTimezoneOffset = function (date: Date | string): Date {
    const mappedUhrzeit = new Date(date);
    mappedUhrzeit.setHours(
      mappedUhrzeit.getHours() -
        Math.trunc(mappedUhrzeit.getTimezoneOffset() / 60)
    );
    return mappedUhrzeit;
  };

  const getDateFromTimeString = function (timeString: string): Date {
    // Validate time string format (HH:MM)
    if (!timeString || !/^\d{1,2}:\d{1,2}(?::\d{1,2})?$/.test(timeString)) {
      return new Date(NaN);
    }

    const timeParts = timeString.split(":").map(Number);

    const [hours, minutes] = timeParts;
    if (
      isNaN(hours) ||
      isNaN(minutes) ||
      hours < 0 ||
      hours > 23 ||
      minutes < 0 ||
      minutes > 59
    ) {
      return new Date(NaN); // Return invalid date for invalid time values
    }

    const seconds = timeParts.length === 3 ? timeParts[2] : 0;
    if (!isNaN(seconds) && (seconds < 0 || seconds > 59)) {
      return new Date(NaN);
    }

    const date = new Date();
    date.setHours(hours, minutes, seconds, 0);
    return date;
  };

  function toGermanDateFormat(dateString: string) {
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

  function toYyyyMmDdWithTimeWithoutTimezoneOffset(dateToFormat: Date) {
    if (!isValidDate(dateToFormat)) {
      return NO_VALUE_DEFAULT;
    }

    const fullYear = _leftPadFourDigitsWithZero(dateToFormat.getFullYear());
    const month = _leftPadTwoDigitsWithZero(dateToFormat.getMonth() + 1);
    const day = _leftPadTwoDigitsWithZero(dateToFormat.getDate());

    const hour = _leftPadTwoDigitsWithZero(dateToFormat.getHours());
    const minute = _leftPadTwoDigitsWithZero(dateToFormat.getMinutes());
    const second = _leftPadTwoDigitsWithZero(dateToFormat.getSeconds());
    const milliseconds = _leftPadWithZero(dateToFormat.getMilliseconds(), 3);

    return `${fullYear}-${month}-${day}T${hour}:${minute}:${second}.${milliseconds}`;
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
    time,
    toHhMm,
    toYyyyMmDd,
    toYyyyMmDdWithTimeWithoutTimezoneOffset,
    applyLocalTimezoneOffset,
    getDateFromTimeString,
    toGermanDateFormat,
    toGermanDateWithLongMonth,
  };
}
