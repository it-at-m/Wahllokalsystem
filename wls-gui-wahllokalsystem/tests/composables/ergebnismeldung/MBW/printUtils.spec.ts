import { afterEach, describe, expect, it, vi } from "vitest";

import { usePrintUtils } from "@/composables/ergebnismeldung/MBW/printUtils.ts";

const mockDefinitions = vi.hoisted(() => ({
  logError: vi.fn(),
}));

vi.mock("@/composables/common/logging.ts", () => ({
  useLogging: () => ({ logError: mockDefinitions.logError }),
}));

describe("printUtils", () => {
  afterEach(() => {
    vi.restoreAllMocks();
    vi.clearAllMocks();
  });

  it("should_returnFalse_when_printWindowCannotBeOpened", async () => {
    vi.spyOn(window, "open").mockReturnValue(null);

    const result = await usePrintUtils().printInWindow("print content");

    expect(result).toBe(false);
  });

  it("should_writePrintContentAndPrint_when_printWindowCanBeOpened", async () => {
    const printWindow = createPrintWindow();
    vi.spyOn(window, "open").mockReturnValue(printWindow as unknown as Window);

    const result = await usePrintUtils().printInWindow("print content");

    expect(window.open).toHaveBeenCalledWith(
      "",
      "",
      "left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0"
    );
    expect(printWindow.document.writeln).toHaveBeenCalledWith("print content");
    expect(printWindow.document.close).toHaveBeenCalledOnce();
    expect(printWindow.print).toHaveBeenCalledOnce();
    expect(printWindow.close).toHaveBeenCalledOnce();
    expect(result).toBe(true);
  });

  it("should_waitForImageDecodeBeforePrinting_when_imageIsNotDecoded", async () => {
    let resolveImageDecode!: () => void;
    const imageDecode = new Promise<void>((resolve) => {
      resolveImageDecode = resolve;
    });
    const printWindow = createPrintWindow([
      { decode: vi.fn().mockReturnValue(imageDecode), src: "image-url" },
    ]);
    vi.spyOn(window, "open").mockReturnValue(printWindow as unknown as Window);

    const printing = usePrintUtils().printInWindow("print content");

    expect(printWindow.print).not.toHaveBeenCalled();
    resolveImageDecode();
    await printing;

    expect(printWindow.print).toHaveBeenCalledOnce();
  });

  it("should_printAndLogError_when_imageDecodeFails", async () => {
    const image = {
      decode: vi.fn().mockRejectedValue(new Error("decode failed")),
      src: "image-url",
    };
    const printWindow = createPrintWindow([image]);
    vi.spyOn(window, "open").mockReturnValue(printWindow as unknown as Window);

    await usePrintUtils().printInWindow("print content");

    expect(mockDefinitions.logError).toHaveBeenCalledWith(
      "Image konnte nicht dekodiert werden: image-url"
    );
    expect(printWindow.print).toHaveBeenCalledOnce();
  });

  function createPrintWindow(images: unknown[] = []) {
    return {
      close: vi.fn(),
      document: {
        close: vi.fn(),
        querySelectorAll: vi.fn().mockReturnValue(images),
        writeln: vi.fn(),
      },
      print: vi.fn(),
    };
  }
});
