export function useCommonTestDataFactory() {
  function generateRandomNumber(length: number): number {
    return Math.floor(Math.random() * Math.pow(10, length));
  }

  function generateRandomNumberInRange(min: number, max: number): number {
    return Math.floor(Math.random() * (max - min + 1)) + min;
  }

  function generateRandomBoolean() {
    return Math.random() < 0.5;
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

  function generateRandomString(length: number): string {
    const characters =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    let result = "";
    for (let i = 0; i < length; i++) {
      const randomIndex = Math.floor(Math.random() * characters.length);
      result += characters.charAt(randomIndex);
    }
    return result;
  }

  function getRandomItem<T>(items: T[]): T {
    const item = items[generateRandomNumberInRange(0, items.length - 1)];
    if (item === undefined) throw new Error("Fehler beim Generieren eines Zufälligen Items");
    return item;
  }

  return {
    generateRandomNumber,
    generateRandomDate,
    generateRandomDateTimeAsString,
    generateRandomString,
    generateRandomNumberInRange,
    generateRandomBoolean,
    getRandomItem,
  };
}
