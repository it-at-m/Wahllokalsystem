export function useNumberFormatter() {
  function convertToSixDigitArray(value: number) {
    if (value < 0 || value > 999999) {
      throw new Error(`Value ${value} out of valid range (0-999999)`);
    }
    return value.toString().padStart(6, "0").split("");
  }

  return {
    convertToSixDigitArray,
  };
}
