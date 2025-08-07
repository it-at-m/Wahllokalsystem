/* eslint-disable no-console */
const doLogging = import.meta.env.DEV;

export function useLogging(loggerName: string) {
  function log(message: string) {
    if (doLogging) {
      console.log(`${loggerName}: ${message}`);
    }
  }

  function logError(message: string, error?: unknown) {
    if (doLogging) {
      console.error(`${loggerName}: ${message}`, error);
    }
  }

  return {
    log,
    logError,
  };
}
