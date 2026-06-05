export function useStringUtils() {
  function toLowerCaseFirstLetter(text: string) {
    if (text.length === 0) {
      return text;
    }
    return text.charAt(0).toLowerCase() + text.slice(1);
  }

  return {
    toLowerCaseFirstLetter,
  };
}
