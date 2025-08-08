import { afterEach, describe, expect, it, vi } from "vitest";

import { useLogging } from "@/composables/common/logging.ts";

describe("logging.ts", () => {
  let unitUnderTest: ReturnType<typeof useLogging>;

  const loggerName = "loggerName";

  afterEach(() => {
    vi.resetAllMocks();
    vi.clearAllMocks();
  });

  describe("log", () => {
    it("should_log_when_loggingIsActive", () => {
      allowLogging(true);
      unitUnderTest = useLogging(loggerName);

      const consoleLogMock = vi
        .spyOn(console, "log")
        .mockImplementation(() => undefined);

      const logMessage = "logMessage";
      unitUnderTest.log(logMessage);

      expect(consoleLogMock).toHaveBeenCalledWith(
        `${loggerName}: ${logMessage}`,
        []
      );

      consoleLogMock.mockReset();
    });

    it.each([undefined, new Error("dummy error"), "a string"])(
      "should_log_when_loggingIsActiveWithAdditionalData'%s'",
      (additionalData) => {
        allowLogging(true);
        unitUnderTest = useLogging(loggerName);

        const consoleLogMock = vi
          .spyOn(console, "log")
          .mockImplementation(() => undefined);

        const logMessage = "logMessage";
        unitUnderTest.log(logMessage, additionalData);

        expect(consoleLogMock).toHaveBeenCalledWith(
          `${loggerName}: ${logMessage}`,
          [additionalData]
        );

        consoleLogMock.mockReset();
      }
    );

    it("should_notLog_when_loggingIsActive", () => {
      allowLogging(false);
      unitUnderTest = useLogging(loggerName);

      const consoleLogMock = vi
        .spyOn(console, "log")
        .mockImplementation(() => undefined);

      const logMessage = "logMessage";
      unitUnderTest.log(logMessage);

      expect(consoleLogMock).toHaveBeenCalledTimes(0);

      consoleLogMock.mockReset();
    });
  });

  describe("logDebug", () => {
    it("should_log_when_loggingIsActive", () => {
      allowLogging(true);
      unitUnderTest = useLogging(loggerName);

      const consoleLogMock = vi
        .spyOn(console, "debug")
        .mockImplementation(() => undefined);

      const logMessage = "logMessage";
      unitUnderTest.logDebug(logMessage);

      expect(consoleLogMock).toHaveBeenCalledWith(
        `${loggerName}: ${logMessage}`,
        []
      );

      consoleLogMock.mockReset();
    });

    it.each([undefined, new Error("dummy error"), "a string"])(
      "should_log_when_loggingIsActiveWithAdditionalData'%s'",
      (additionalData) => {
        allowLogging(true);
        unitUnderTest = useLogging(loggerName);

        const consoleLogMock = vi
          .spyOn(console, "debug")
          .mockImplementation(() => undefined);

        const logMessage = "logMessage";
        unitUnderTest.logDebug(logMessage, additionalData);

        expect(consoleLogMock).toHaveBeenCalledWith(
          `${loggerName}: ${logMessage}`,
          [additionalData]
        );

        consoleLogMock.mockReset();
      }
    );

    it("should_notLog_when_loggingIsActive", () => {
      allowLogging(false);
      unitUnderTest = useLogging(loggerName);

      const consoleLogMock = vi
        .spyOn(console, "debug")
        .mockImplementation(() => undefined);

      const logMessage = "logMessage";
      unitUnderTest.logDebug(logMessage);

      expect(consoleLogMock).toHaveBeenCalledTimes(0);

      consoleLogMock.mockReset();
    });
  });

  describe("logWarn", () => {
    it.each([undefined, new Error("dummy error"), "a string"])(
      "should_log_when_loggingIsActiveWithAdditionalData'%s'",
      (additionalData) => {
        allowLogging(true);
        unitUnderTest = useLogging(loggerName);

        const consoleLogMock = vi
          .spyOn(console, "warn")
          .mockImplementation(() => undefined);

        const logMessage = "logMessage";
        unitUnderTest.logWarn(logMessage, additionalData);

        expect(consoleLogMock).toHaveBeenCalledWith(
          `${loggerName}: ${logMessage}`,
          [additionalData]
        );

        consoleLogMock.mockReset();
      }
    );

    it("should_notLog_when_loggingIsActive", () => {
      allowLogging(false);
      unitUnderTest = useLogging(loggerName);

      const consoleLogMock = vi
        .spyOn(console, "warn")
        .mockImplementation(() => undefined);

      const logMessage = "logMessage";
      unitUnderTest.logWarn(logMessage);

      expect(consoleLogMock).toHaveBeenCalledTimes(0);

      consoleLogMock.mockReset();
    });
  });

  describe("logError", () => {
    it.each([undefined, new Error("dummy error"), "a string"])(
      "should_log_when_loggingIsActiveWithAdditionalData'%s'",
      (additionalData) => {
        allowLogging(true);
        unitUnderTest = useLogging(loggerName);

        const consoleLogMock = vi
          .spyOn(console, "error")
          .mockImplementation(() => undefined);

        const logMessage = "logMessage";
        unitUnderTest.logError(logMessage, additionalData);

        expect(consoleLogMock).toHaveBeenCalledWith(
          `${loggerName}: ${logMessage}`,
          [additionalData]
        );

        consoleLogMock.mockReset();
      }
    );

    it("should_notLog_when_loggingIsActive", () => {
      allowLogging(false);
      unitUnderTest = useLogging(loggerName);

      const consoleLogMock = vi
        .spyOn(console, "error")
        .mockImplementation(() => undefined);

      const logMessage = "logMessage";
      unitUnderTest.logError(logMessage);

      expect(consoleLogMock).toHaveBeenCalledTimes(0);

      consoleLogMock.mockReset();
    });
  });

  function allowLogging(allow: boolean) {
    import.meta.env.DEV = allow;
  }
});
