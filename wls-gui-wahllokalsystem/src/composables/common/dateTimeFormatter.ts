import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";

export function useDateTimeFormatter() {
  const NO_VALUE_DEFAULT = "";
  const TIME_FIELD_SEPARATOR = ":";

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

  const updateTimeOfDateObject = function (
    timeString: string,
    dateToModify: Date
  ): Date | undefined {
    if (!timeString || timeString.trim().length === 0) {
      return undefined;
    }
    const [hours, minutes] = timeString.split(":").map(Number);
    if (isNaN(hours) || isNaN(minutes)) {
      return undefined;
    }
    const newDate = new Date(dateToModify);
    newDate.setHours(hours, minutes);
    return newDate;
  };

  const updateDateOfDateObject = function (
    dateString: string,
    dateToModify: Date
  ): Date | undefined {
    if (dateString.length === 0) {
      return dateToModify;
    }
    const [year, month, day] = dateString.split("-").map(Number);
    if (isNaN(year) || isNaN(month) || isNaN(day)) {
      return undefined;
    }
    const newTime = new Date(dateToModify);
    newTime.setFullYear(year, month - 1, day);
    return newTime;
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

  const parseDateString = (dateString: string) => {
    const date = new Date(dateString);
    return isNaN(date.getTime()) ? null : date;
  };

  return {
    time,
    toHhMm,
    applyLocalTimezoneOffset,
    updateDateOfDateObject,
    updateTimeOfDateObject,
    toGermanDateFormat,
    getDateFromTimeString,
    toGermanDateWithLongMonth,
    parseDateString,
  };
}

function leftPadTwoDigitsWithZero(number: number): string {
  return `${number}`.padStart(2, "0");
}
