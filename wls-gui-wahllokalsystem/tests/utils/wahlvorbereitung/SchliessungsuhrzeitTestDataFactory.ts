import type { UrnenwahlSchliessungsuhrzeit } from "@/types/wahlvorbereitung/UrnenwahlSchliessungsuhrzeit.ts";

export function useSchliessungsuhrzeitTestDataFactory() {
  function createSchliessungsuhrzeit(
    time: string
  ): UrnenwahlSchliessungsuhrzeit {
    return {
      schliessungsuhrzeit: time,
    };
  }

  return { createSchliessungsuhrzeit };
}
