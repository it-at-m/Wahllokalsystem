import type { Task } from "@/types/tasks/Task.ts";
import type { WahlMetaData } from "@/types/wlsTypes/WahlMetaData.ts";

export interface TaskFactoryInterface {
  createTask(wahlMetaData: WahlMetaData, wahlName: string): Task;
}
