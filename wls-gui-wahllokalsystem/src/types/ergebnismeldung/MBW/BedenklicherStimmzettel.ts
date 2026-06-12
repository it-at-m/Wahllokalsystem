import type { SupplementEnum } from "@/types/ergebnismeldung/MBW/SupplementEnum.ts";
import type { ValidityEnum } from "@/types/ergebnismeldung/MBW/ValidityEnum.ts";

export interface BedenklicherStimmzettel {
  orderIndex: number;
  supplements: SupplementEnum[];
  validity: ValidityEnum;
}
