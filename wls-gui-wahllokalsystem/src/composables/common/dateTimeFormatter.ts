export function useDateTimeFormatter() {
  const NO_VALUE_DEFAULT = "";
  const TIME_FIELD_SEPARATOR = ":";

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

  const toCorrectTimezone = function (date: Date | string): Date {
    const mappedUhrzeit = new Date(date);
    mappedUhrzeit.setHours(
      mappedUhrzeit.getHours() -
        Math.trunc(mappedUhrzeit.getTimezoneOffset() / 60)
    );
    return mappedUhrzeit;
  };

  const getDateFromTimeString = function (timeString: string): Date {
    // Validate time string format (HH:MM)
    if (!timeString || !/^\d{1,2}:\d{1,2}$/.test(timeString)) {
      return new Date(NaN);
    }
    const [hours, minutes] = timeString.split(":").map(Number);
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
    const date = new Date();
    date.setHours(hours, minutes);
    return date;
  };

  return { time, toHhMm, toCorrectTimezone, getDateFromTimeString };
}

function leftPadTwoDigitsWithZero(number: number): string {
  return `${number}`.padStart(2, "0");
}
