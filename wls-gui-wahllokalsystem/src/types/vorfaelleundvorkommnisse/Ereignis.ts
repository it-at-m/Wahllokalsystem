import { EreignisartEnum } from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";

export interface Ereignis {
  beschreibung?: string;
  uhrzeit?: Date;
  ereignisart: EreignisartEnum;
}

// export class EreignisBuilder implements Ereignis {
//   constructor(
//     public ereignisart: EreignisartEnum,
//     public beschreibung?: string,
//     public uhrzeit?: Date
//   ) {}
//
//   static createMinimal(): EreignisBuilder {
//     return new EreignisBuilder(EreignisartEnum.Vorfall);
//   }
//
//   static createComplete(): EreignisBuilder {
//     return new EreignisBuilder(
//       EreignisartEnum.Vorfall,
//       "Ein Stift ist runter gefallen",
//       new Date("2025-03-31T15:15:00.000")
//     );
//   }
//
//   withBeschreibung(beschreibung: string) {
//     this.beschreibung = beschreibung;
//     return this;
//   }
//
//   withUhrzeit(uhrzeit: Date) {
//     this.uhrzeit = uhrzeit;
//     return this;
//   }
//
//   withEreignisart(ereignisart: EreignisartEnum) {
//     this.ereignisart = ereignisart;
//     return this;
//   }
// }
