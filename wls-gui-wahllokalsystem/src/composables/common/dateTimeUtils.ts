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

  return {
    isValidDate,
    applyLocalTimezoneOffset,
  };
}
