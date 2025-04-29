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

  return { time, toHhMm, toCorrectTimezone };
}

function leftPadTwoDigitsWithZero(number: number): string {
  return `${number}`.padStart(2, "0");
}
