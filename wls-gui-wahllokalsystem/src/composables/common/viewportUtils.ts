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

  return {
    scrollIntoView,
  };
}
