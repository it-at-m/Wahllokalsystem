export function useNumberTools() {
  function zeroAsNull(value: number): number | null {
    return value === 0 ? null : value;
  }

  return { zeroAsNull };
}
