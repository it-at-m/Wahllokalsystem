export function useCommonTestDataFactory() {
  function generateNumberRandom(length: number): number {
    return Math.floor(Math.random() * (length * 10));
  }

  function generateDateRandomAsString(): string {
    const date = new Date();

    date.setDate(date.getDate() - Math.trunc(Math.random() * 100));

    return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
  }

  return {
    generateNumberRandom,
    generateDateRandomAsString,
  };
}
