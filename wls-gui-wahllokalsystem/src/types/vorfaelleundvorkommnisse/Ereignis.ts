import { EreignisartEnum } from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";

export interface Ereignis {
  beschreibung?: string;
  uhrzeit?: Date;
  ereignisart: EreignisartEnum;
}
