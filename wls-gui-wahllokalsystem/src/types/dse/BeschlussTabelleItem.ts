export class BeschlussTabelleItem {
  id: number;
  kennung;
  team;
  grund: string;
  beschluss;
  beschlussergebnis;

  constructor(
    kennung: number,
    team: string,
    grund: string,
    beschluss: boolean,
    beschlussergebnis: string
  ) {
    this.id = 0;
    this.kennung = kennung;
    this.team = team;
    this.grund = grund;
    this.beschluss = beschluss;
    this.beschlussergebnis = beschlussergebnis;
  }
}
