import { useLogging } from "@/composables/common/logging.ts";
import { useMonitoringStore } from "@/stores/monitoringStore.ts";

const { logDebug } = useLogging("monitoringCronjobService");

export function useMonitoringCronjobService() {
  const { sendWaehler } = useMonitoringStore();

  const time30MinutesInMilliseconds = 1_800_000;
  const wahlbeteiligungUpdateInterval = time30MinutesInMilliseconds;

  let wahlbeteiligungActiveInterval: number | null = null;

  function startWahlbeteiligungInterval(): void {
    if (wahlbeteiligungActiveInterval !== null) {
      stopWahlbeteiligungInterval();
    }

    wahlbeteiligungActiveInterval = window.setInterval(async () => {
      try {
        await sendWaehler();
      } catch (error) {
        logDebug("Failed to send wahlbeteiligung:", error);
      }
    }, wahlbeteiligungUpdateInterval);
  }

  function stopWahlbeteiligungInterval(): void {
    if (wahlbeteiligungActiveInterval !== null) {
      clearInterval(wahlbeteiligungActiveInterval);
      wahlbeteiligungActiveInterval = null;
    }
  }

  return {
    startWahlbeteiligungInterval,
    stopWahlbeteiligungInterval,
  };
}
