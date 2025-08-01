/* eslint-disable no-console */
const doLogging = true; //TODO Switch based on mode: dev or production

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
