import { useLogging } from "@/composables/common/logging.ts";
import { useMonitoringStore } from "@/stores/monitoringStore.ts";
import { useSchedulerStore } from "@/stores/schedulerStore.ts";

const { logDebug } = useLogging("monitoringCronjobService");

export function useMonitoringCronjobService() {
  const { sendWaehler } = useMonitoringStore();
  const { registerInterval, stopInterval } = useSchedulerStore();

  const time30MinutesInMilliseconds = 1_800_000;
  const wahlbeteiligungUpdateInterval = time30MinutesInMilliseconds;

  let wahlbeteiligungActiveInterval: number | null = null;

  function startWahlbeteiligungInterval(): void {
    if (wahlbeteiligungActiveInterval !== null) {
      stopWahlbeteiligungInterval();
    }

    wahlbeteiligungActiveInterval = registerInterval({
      title: "Send Wahlbeteiligung Interval",
      action: _sendWaehlerForInterval,
      delay: wahlbeteiligungUpdateInterval,
      runActionAfterRegister: false,
    });
  }

  function stopWahlbeteiligungInterval(): void {
    if (wahlbeteiligungActiveInterval !== null) {
      stopInterval(wahlbeteiligungActiveInterval);
      wahlbeteiligungActiveInterval = null;
    }
  }

  async function _sendWaehlerForInterval() {
    try {
      await sendWaehler();
    } catch (error) {
      logDebug("Failed to send wahlbeteiligung:", error);
    }
  }

  return {
    startWahlbeteiligungInterval,
    stopWahlbeteiligungInterval,
  };
}
