export function useMathUtils() {
  function maxOfOptionalNumbers(numbers: (number | null)[]): number | null {
    const nonNullNumbers = numbers.filter((n) => n !== null) as number[];
    if (nonNullNumbers.length === 0) {
      return null;
    }

    return Math.max(...nonNullNumbers);
  }

  return {
    maxOfOptionalNumbers,
  };
}
