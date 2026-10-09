import { useLogging } from "@/composables/common/logging.ts";

export function usePrintUtils() {
  const { logError } = useLogging("printUtils");

  async function printInWindow(printContent: string) {
    const printWindow = window.open(
      "",
      "",
      "left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0"
    );

    if (printWindow) {
      printWindow.document.writeln(printContent);
      printWindow.document.close();

      await _fetchImages(printWindow);
      printWindow.print();
      printWindow.close();
      return true;
    }

    return false;
  }

  async function _fetchImages(printWindow: Window) {
    const images = printWindow.document.querySelectorAll("img");

    // Chrome/Chromium requires images to be fully loaded and ready in the DOM before printing.
    // Otherwise, they might be missing or blank in the generated PDF layout.
    const imagePromises = Array.from(images).map((img) =>
      img.decode
        ? img.decode().catch(() => {
            logError("Image konnte nicht dekodiert werden: " + img.src);
          })
        : Promise.resolve()
    );

    await Promise.all(imagePromises);
  }

  return { printInWindow };
}
