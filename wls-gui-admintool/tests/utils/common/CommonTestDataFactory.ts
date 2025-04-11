export function useCommonTestDataFactory() {
  function generateRandomBoolean() {
    return Math.random() < 0.5;
  }

  function generateRandomNumber(length: number): number {
    return Math.floor(Math.random() * Math.pow(10, length));
  }

  function generateRandomDateAsString(): string {
    const date = new Date();

    date.setDate(date.getDate() - Math.trunc(Math.random() * 100));

    return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
  }

  function generateRandomDateTimeAsString() {
    const date = new Date();

    date.setDate(date.getDate() - Math.trunc(Math.random() * 100));

    return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}T${date.getHours()}:${date.getMinutes()}:${date.getSeconds()}`;
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
    generateRandomDateTimeAsString,
  };
}
