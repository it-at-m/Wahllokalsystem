export function useNumberTools() {
  function zeroAsNull(value: number): number | null {
    return value === 0 ? null : value;
  }

  function sortAndGroupConsecutiveNumbers(numbers: number[]) {
    if (numbers.length === 0) {
      return [];
    }

    const sortedNumbers = numbers.sort((a, b) => a - b);
    const ranges = [];
    let start = sortedNumbers[0];
    let end = sortedNumbers[0];

    for (let i = 1; i < sortedNumbers.length; i++) {
      const nummer = sortedNumbers[i];

      if (nummer === end + 1) {
        end = nummer;
      } else {
        ranges.push(start === end ? `${start}` : `${start}-${end}`);

        start = nummer;
        end = nummer;
      }
    }

    ranges.push(start === end ? `${start}` : `${start}-${end}`);

    return ranges;
  }

  return { zeroAsNull, sortAndGroupConsecutiveNumbers };
}
