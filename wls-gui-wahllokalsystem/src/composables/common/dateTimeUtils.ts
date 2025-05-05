export function useDateTimeUtils() {
  function isValidDate(date: Date): boolean {
    return !isNaN(date.getTime());
  }

  return {
    isValidDate,
  };
}
