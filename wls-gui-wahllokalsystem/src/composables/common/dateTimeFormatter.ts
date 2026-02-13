import { useDateTimeUtils } from "@/composables/common/dateTimeUtils.ts";

export function useDateTimeFormatter() {
  const NO_VALUE_DEFAULT = "";
  const TIME_FIELD_SEPARATOR = ":";
  const DATE_SEPARATOR = "-";

  const { isValidDate } = useDateTimeUtils();

  const toHhMmSs = function (date: Date | string | null | undefined): string {
    const parsedDate = _returnParsedDateOrInvalid(date);

    if (isValidDate(parsedDate)) {
      return [
        _leftPadTwoDigitsWithZero(parsedDate.getHours()),
        _leftPadTwoDigitsWithZero(parsedDate.getMinutes()),
        _leftPadTwoDigitsWithZero(parsedDate.getSeconds()),
      ].join(TIME_FIELD_SEPARATOR);
    } else {
      return NO_VALUE_DEFAULT;
    }
  };

  const toHhMm = function (date: Date | string | null | undefined): string {
    const parsedDate = _returnParsedDateOrInvalid(date);
    if (isValidDate(parsedDate)) {
      return [
        _leftPadTwoDigitsWithZero(parsedDate.getHours()),
        _leftPadTwoDigitsWithZero(parsedDate.getMinutes()),
      ].join(TIME_FIELD_SEPARATOR);
    } else {
      return NO_VALUE_DEFAULT;
    }
  };

  function toGermanDate(
    date: Date | string | null | undefined
  ): string | undefined {
    const parsedDate = _returnParsedDateOrInvalid(date);

    if (isValidDate(parsedDate)) {
      return parsedDate.toLocaleDateString("de-DE", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
      });
    } else {
      return undefined;
    }
  }

  function toGermanDateWithLongMonth(
    date: Date | string | null | undefined
  ): string | undefined {
    const parsedDate = _returnParsedDateOrInvalid(date);

    if (isValidDate(parsedDate)) {
      return parsedDate.toLocaleDateString("de-DE", {
        year: "numeric",
        month: "long",
        day: "numeric",
        hour12: false,
      });
    } else {
      return undefined;
    }
  }

  function toYyyyMmDd(date: Date | string | null | undefined): string {
    const parsedDate = _returnParsedDateOrInvalid(date);

    if (isValidDate(parsedDate)) {
      return [
        _leftPadFourDigitsWithZero(parsedDate.getFullYear()),
        _leftPadTwoDigitsWithZero(parsedDate.getMonth() + 1),
        _leftPadTwoDigitsWithZero(parsedDate.getDate()),
      ].join(DATE_SEPARATOR);
    } else {
      return NO_VALUE_DEFAULT;
    }
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

  function toTimeWithHoursAndOptionalMinutes(
    date: Date | string | null | undefined
  ): string {
    const time = toHhMm(date);
    const [hours, minutes] = time.split(TIME_FIELD_SEPARATOR);
    return minutes === "00" ? hours : time;
  }

  function _returnParsedDateOrInvalid(
    date: Date | string | null | undefined
  ): Date {
    if (!date) {
      return new Date(NaN);
    }

    return typeof date === "string" ? new Date(date) : date;
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
    toYyyyMmDdWithTimeWithoutTimezoneOffset,
    toGermanDate,
    toGermanDateWithLongMonth,
    toTimeWithHoursAndOptionalMinutes,
  };
}
