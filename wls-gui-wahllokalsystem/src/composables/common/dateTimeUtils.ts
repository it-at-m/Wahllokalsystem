export function useDateTimeUtils() {
  function isValidDate(date: Date): boolean {
    return !isNaN(date.getTime());
  }

  const createTodayWithTime = function (timeString: string): Date {
    // Validate time string format (HH:MM:ss)
    if (!timeString || !/^\d{1,2}:\d{1,2}(?::\d{1,2})?$/.test(timeString)) {
      return new Date(NaN);
    }

    const timeParts = timeString.split(":").map(Number);

    const hours: number = timeParts[0] ?? 0;
    const minutes: number = timeParts[1] ?? 0;
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

    const seconds: number = timeParts[2] ?? 0;
    if (!isNaN(seconds) && (seconds < 0 || seconds > 59)) {
      return new Date(NaN);
    }

    const date = new Date();
    date.setHours(hours, minutes, seconds, 0);
    return date;
  };

  return {
    isValidDate,
    createTodayWithTime,
  };
}
