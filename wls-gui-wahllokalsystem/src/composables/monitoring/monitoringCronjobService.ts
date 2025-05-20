import { useMonitoringStore } from "@/stores/monitoringStore.ts";

export function useMonitoringCronjobService() {
  const { sendWaehler } = useMonitoringStore();

  const time1MinuteInMilliseconds = 60_000;
  const wahlbeteiligungUpdateInterval = time1MinuteInMilliseconds;

  let wahlbeteiligungActiveInterval: number | null = null;

  function startWahlbeteiligungInterval(): void {
    if (wahlbeteiligungActiveInterval !== null) {
      stopWahlbeteiligungInterval();
    }

    wahlbeteiligungActiveInterval = window.setInterval(async () => {
      try {
        await sendWaehler();
      } catch (error) {
        console.debug("Failed to send wahlbeteiligung:", error);
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
