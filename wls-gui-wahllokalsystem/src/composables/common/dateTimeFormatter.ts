import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";

export function useDateTimeFormatter() {
  const NO_VALUE_DEFAULT = "";
  const TIME_FIELD_SEPARATOR = ":";
  const INDEX_OF_TIME_SEPARATOR = 10;

  const { isValidDate } = useDateTimeUtils();

  const time = function (date?: Date | null): string {
    if (!date) {
      return NO_VALUE_DEFAULT;
    }

    const hour = leftPadTwoDigitsWithZero(date.getHours());
    const minute = leftPadTwoDigitsWithZero(date.getMinutes());
    const second = leftPadTwoDigitsWithZero(date.getSeconds());

    return `${hour}${TIME_FIELD_SEPARATOR}${minute}${TIME_FIELD_SEPARATOR}${second}`;
  };

  const toHhMm = function (date?: Date | null): string {
    if (!date) {
      return NO_VALUE_DEFAULT;
    }

    const hour = leftPadTwoDigitsWithZero(date.getHours());
    const minute = leftPadTwoDigitsWithZero(date.getMinutes());

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

  function toIsoDate(dateToFormat: Date) {
    if (!isValidDate(dateToFormat)) {
      return NO_VALUE_DEFAULT;
    }

    return dateToFormat.toISOString().substring(0, INDEX_OF_TIME_SEPARATOR);
  }

  return {
    time,
    toHhMm,
    toIsoDate,
    applyLocalTimezoneOffset,
    getDateFromTimeString,
    toGermanDateFormat,
    toGermanDateWithLongMonth,
  };
}

function leftPadTwoDigitsWithZero(number: number): string {
  return `${number}`.padStart(2, "0");
}
