import { beforeEach, describe, expect, it, vi } from "vitest";

import { useViewportUtils } from "@/composables/common/viewportUtils.ts";

describe("viewportUtils.ts", () => {
  const { scrollIntoView } = useViewportUtils();

  beforeEach(() => {
    window.scrollTo = vi.fn();
  });

  describe("scrollIntoView", () => {
    it("should_scrollToElement_when_elementExists", () => {
      const mockElement = document.createElement("div");
      mockElement.getBoundingClientRect = () =>
        ({
          top: 200,
          left: 0,
          right: 0,
          bottom: 0,
          height: 0,
          width: 0,
          x: 0,
          y: 0,
        }) as DOMRect;

      document.body.appendChild(mockElement);
      mockElement.id = "elementId";

      scrollIntoView("#elementId");

      expect(window.scrollTo).toHaveBeenCalledWith({
        top: 120,
        behavior: "smooth",
      });
      document.body.removeChild(mockElement);
    });

    it("should_notScroll_when_elementNotExists", () => {
      scrollIntoView("#wrongElementId");
      expect(window.scrollTo).not.toHaveBeenCalled();
    });
  });
});
