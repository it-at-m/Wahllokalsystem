import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";

export function useZurueckweisungsgrundEnumUtils() {
  function isRejectingZurueckweisungsgrund(
    zurueckweisungsgruend: ZurueckweisungsgrundEnum | null
  ) {
    return (
      zurueckweisungsgruend !== null &&
      zurueckweisungsgruend !== ZurueckweisungsgrundEnum.Zugelassen
    );
  }

  return {
    isRejectingZurueckweisungsgrund,
  };
}
