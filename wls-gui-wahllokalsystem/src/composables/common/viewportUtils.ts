export function useViewportUtils() {
  function scrollIntoView(elementId: string) {
    const element = document.querySelector(elementId);
    if (element) {
      const headerOffset = 80;
      const positon =
        element.getBoundingClientRect().top + window.pageYOffset - headerOffset;
      window.scrollTo({ top: positon, behavior: "smooth" });
    }
  }

  function triggerRippleEffect(element: HTMLElement) {
    const rect = element.getBoundingClientRect();

    const eventOptions = {
      bubbles: false,
      cancelable: false,
      clientX: rect.left + rect.width / 2,
      clientY: rect.top + rect.height / 2,
    };

    element.dispatchEvent(new MouseEvent("mousedown", eventOptions));

    window.setTimeout(() => {
      element.dispatchEvent(new MouseEvent("mouseup", eventOptions));
    }, 350);
  }

  return {
    scrollIntoView,
    triggerRippleEffect,
  };
}
