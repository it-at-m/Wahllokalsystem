export function useFormatter() {
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

  function leftPadTwoDigitsWithZero(number: number): string {
    return `${number}`.padStart(2, "0");
  }

  return { time, toHhMm };
}
