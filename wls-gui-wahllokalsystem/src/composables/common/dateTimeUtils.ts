export function useDateTimeUtils() {
  function isValidDate(date: Date): boolean {
    return !isNaN(date.getTime());
  }

  const applyLocalTimezoneOffset = function (date: Date | string): Date {
    const mappedUhrzeit = new Date(date);
    mappedUhrzeit.setHours(
      mappedUhrzeit.getHours() -
        Math.trunc(mappedUhrzeit.getTimezoneOffset() / 60)
    );
    return mappedUhrzeit;
  };

  const createTodayWithTime = function (timeString: string): Date {
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

  return {
    isValidDate,
    applyLocalTimezoneOffset,
    createTodayWithTime,
  };
}
