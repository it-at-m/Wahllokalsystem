import type { SupplementEnum } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/SupplementEnum.ts";
import type { ValidityEnum } from "@/types/ergebnismeldung/MBW/bedenklicheStimmzettel/ValidityEnum.ts";

export interface BedenklicherStimmzettel {
  orderIndex: number;
  supplements: SupplementEnum[];
  validity: ValidityEnum;
}
