export function useUrlTools() {
  function getPathSegments(url: URL): string[] {
    return url.pathname.split("/").filter(Boolean);
  }

  return { getPathSegments };
}
