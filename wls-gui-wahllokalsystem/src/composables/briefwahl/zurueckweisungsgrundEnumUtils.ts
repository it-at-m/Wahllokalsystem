import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";

export function useZurueckweisungsgrundEnumUtils() {
  function isRejectingZurueckweisungsgrund(
    zurueckweisungsgrund: ZurueckweisungsgrundEnum | null
  ) {
    return (
      zurueckweisungsgrund !== null &&
      zurueckweisungsgrund !== ZurueckweisungsgrundEnum.Zugelassen
    );
  }

  return {
    isRejectingZurueckweisungsgrund,
  };
}
