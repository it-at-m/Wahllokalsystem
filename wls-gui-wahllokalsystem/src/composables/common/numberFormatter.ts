export function useNumberFormatter() {
  function convertToSixDigitArray(value: number) {
    return value.toString().padStart(6, "0").split("");
  }

  return {
    convertToSixDigitArray,
  };
}
