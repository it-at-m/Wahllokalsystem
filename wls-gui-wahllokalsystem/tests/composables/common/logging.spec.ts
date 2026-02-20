import type { MockInstance } from "vitest";

import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useLogging } from "@/composables/common/logging.ts";

describe("logging.ts", () => {
  let unitUnderTest: ReturnType<typeof useLogging>;

  const loggerName = "loggerName";
  const logMessage = "logMessage";
  let consoleMock: MockInstance;

  afterEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("log", () => {
    beforeEach(() => {
      consoleMock = vi
        .spyOn(console, "log")
        .mockImplementation(() => undefined);
    });

    afterEach(() => {
      consoleMock.mockRestore();
    });

    it("should_log_when_loggingIsActive", () => {
      allowLogging(true);
      unitUnderTest = useLogging(loggerName);

      const logMessage = "logMessage";
      unitUnderTest.log(logMessage);

      expect(consoleMock).toHaveBeenCalledWith(`${loggerName}: ${logMessage}`);
    });

    it.each([undefined, new Error("dummy error"), "a string"])(
      "should_log_when_loggingIsActiveWithAdditionalData'%s'",
      (additionalData) => {
        allowLogging(true);
        unitUnderTest = useLogging(loggerName);

        unitUnderTest.log(logMessage, additionalData);

        expect(consoleMock).toHaveBeenCalledWith(
          `${loggerName}: ${logMessage}`,
          additionalData
        );
      }
    );

    it("should_notLog_when_loggingIsNotActive", () => {
      allowLogging(false);
      unitUnderTest = useLogging(loggerName);

      unitUnderTest.log(logMessage);

      expect(consoleMock).toHaveBeenCalledTimes(0);
    });
  });

  describe("logDebug", () => {
    beforeEach(() => {
      consoleMock = vi
        .spyOn(console, "debug")
        .mockImplementation(() => undefined);
    });

    afterEach(() => {
      consoleMock.mockRestore();
    });

    it("should_log_when_loggingIsActive", () => {
      allowLogging(true);
      unitUnderTest = useLogging(loggerName);

      unitUnderTest.logDebug(logMessage);

      expect(consoleMock).toHaveBeenCalledWith(`${loggerName}: ${logMessage}`);
    });

    it.each([undefined, new Error("dummy error"), "a string"])(
      "should_log_when_loggingIsActiveWithAdditionalData'%s'",
      (additionalData) => {
        allowLogging(true);
        unitUnderTest = useLogging(loggerName);

        unitUnderTest.logDebug(logMessage, additionalData);

        expect(consoleMock).toHaveBeenCalledWith(
          `${loggerName}: ${logMessage}`,
          additionalData
        );
      }
    );

    it("should_notLog_when_loggingIsNotActive", () => {
      allowLogging(false);
      unitUnderTest = useLogging(loggerName);

      unitUnderTest.logDebug(logMessage);

      expect(consoleMock).toHaveBeenCalledTimes(0);
    });
  });

  describe("logWarn", () => {
    beforeEach(() => {
      consoleMock = vi
        .spyOn(console, "warn")
        .mockImplementation(() => undefined);
    });

    afterEach(() => {
      consoleMock.mockRestore();
    });

    it("should_log_when_loggingIsActive", () => {
      allowLogging(true);
      unitUnderTest = useLogging(loggerName);

      unitUnderTest.logWarn(logMessage);

      expect(consoleMock).toHaveBeenCalledWith(`${loggerName}: ${logMessage}`);
    });

    it.each([undefined, new Error("dummy error"), "a string"])(
      "should_log_when_loggingIsActiveWithAdditionalData'%s'",
      (additionalData) => {
        allowLogging(true);
        unitUnderTest = useLogging(loggerName);

        unitUnderTest.logWarn(logMessage, additionalData);

        expect(consoleMock).toHaveBeenCalledWith(
          `${loggerName}: ${logMessage}`,
          additionalData
        );
      }
    );

    it("should_notLog_when_loggingIsNotActive", () => {
      allowLogging(false);
      unitUnderTest = useLogging(loggerName);

      unitUnderTest.logWarn(logMessage);

      expect(consoleMock).toHaveBeenCalledTimes(0);
    });
  });

  describe("logError", () => {
    beforeEach(() => {
      consoleMock = vi
        .spyOn(console, "error")
        .mockImplementation(() => undefined);
    });

    afterEach(() => {
      consoleMock.mockRestore();
    });

    it("should_log_when_loggingIsActive", () => {
      allowLogging(true);
      unitUnderTest = useLogging(loggerName);

      unitUnderTest.logError(logMessage);

      expect(consoleMock).toHaveBeenCalledWith(`${loggerName}: ${logMessage}`);
    });

    it.each([undefined, new Error("dummy error"), "a string"])(
      "should_log_when_loggingIsActiveWithAdditionalData'%s'",
      (additionalData) => {
        allowLogging(true);
        unitUnderTest = useLogging(loggerName);

        unitUnderTest.logError(logMessage, additionalData);

        expect(consoleMock).toHaveBeenCalledWith(
          `${loggerName}: ${logMessage}`,
          additionalData
        );
      }
    );

    it("should_notLog_when_loggingIsNotActive", () => {
      allowLogging(false);
      unitUnderTest = useLogging(loggerName);

      unitUnderTest.logError(logMessage);

      expect(consoleMock).toHaveBeenCalledTimes(0);
    });
  });

  function allowLogging(allow: boolean) {
    import.meta.env.DEV = allow;
  }
});
