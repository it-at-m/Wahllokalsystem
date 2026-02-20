export class Data {
  title: string;
  cake: string;
  cakeNumber: number;
  toppings: string[] | null;
  hungerIndex: number[];

  constructor(
    title: string,
    cake: string,
    cakeNumber: number,
    toppings: string[] | null,
    hungerIndex: number[]
  ) {
    this.title = title;
    this.cake = cake;
    this.cakeNumber = cakeNumber;
    this.toppings = toppings;
    this.hungerIndex = hungerIndex;
  }
}
