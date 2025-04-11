export function useCommonTestDataFactory() {
  function generateRandomNumber(length: number): number {
    return Math.floor(Math.random() * Math.pow(10, length));
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
    generateRandomNumber,
    generateRandomDate,
    generateRandomDateAsString,
  };
}
