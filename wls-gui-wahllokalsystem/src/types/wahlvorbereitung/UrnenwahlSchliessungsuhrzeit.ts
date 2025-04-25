export interface UrnenwahlSchliessungsuhrzeit {
  schliessungsuhrzeit: string;
}

export class UrnenwahlSchliessungsuhrzeitBuilder
  implements UrnenwahlSchliessungsuhrzeit
{
  constructor(public schliessungsuhrzeit: string) {}

  static createWithSchliessungsuhrzeit(
    time: string
  ): UrnenwahlSchliessungsuhrzeitBuilder {
    return new UrnenwahlSchliessungsuhrzeitBuilder(time);
  }
}
