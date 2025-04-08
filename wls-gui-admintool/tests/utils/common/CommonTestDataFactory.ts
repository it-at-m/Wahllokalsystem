export function useCommonTestDataFactory() {
  function generateRandomBoolean() {
    return Math.random() % 2 === 0;
  }

  function generateRandomNumber(length: number): number {
    return Math.floor(Math.random() * (length * 10));
  }

  function generateRandomDateAsString(): string {
    const date = new Date();

    date.setDate(date.getDate() - Math.trunc(Math.random() * 100));

    return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
  }

  function generateRandomDate(): Date {
    const date = new Date();

    date.setDate(date.getDate() - Math.trunc(Math.random() * 100));

    return date;
  }

  return {
    generateRandomBoolean,
    generateRandomNumber,
    generateRandomDate,
    generateRandomDateAsString,
  };
}
