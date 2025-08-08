/* eslint-disable no-console */

export function useLogging(loggerName: string) {
  const doLogging = import.meta.env.DEV;
  function log(message: string, ...additionalData: unknown[]) {
    if (doLogging) {
      console.log(`${loggerName}: ${message}`, ...additionalData);
    }
  }

  function logDebug(message: string, ...additionalData: unknown[]) {
    if (doLogging) {
      console.debug(`${loggerName}: ${message}`, ...additionalData);
    }
  }

  function logWarn(message: string, ...additionalData: unknown[]) {
    if (doLogging) {
      console.warn(`${loggerName}: ${message}`, ...additionalData);
    }
  }

  function logError(message: string, ...additionalData: unknown[]) {
    if (doLogging) {
      console.error(`${loggerName}: ${message}`, ...additionalData);
    }
  }

  return {
    log,
    logDebug,
    logWarn,
    logError,
  };
}
