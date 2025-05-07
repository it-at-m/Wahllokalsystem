export function useCommonTestDataFactory() {
  function generateRandomNumber(length: number): number {
    return Math.floor(Math.random() * Math.pow(10, length));
  }

  function generateRandomDateTimeAsString() {
    const date = new Date();

    date.setDate(date.getDate() - Math.trunc(Math.random() * 100));

    return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, "0")}-${date.getDate().toString().padStart(2, "0")}T${date.getHours().toString().padStart(2, "0")}:${date.getMinutes().toString().padStart(2, "0")}:${date.getSeconds().toString().padStart(2, "0")}`;
  }

  function generateRandomDate(): Date {
    const date = new Date();

    date.setDate(date.getDate() - Math.trunc(Math.random() * 100));

    return date;
  }

  return {
    generateRandomNumber,
    generateRandomDate,
    generateRandomDateTimeAsString,
  };
}
