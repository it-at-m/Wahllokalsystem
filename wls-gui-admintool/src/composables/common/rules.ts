export function useRules() {
  const requiredText = (requiredText: string) => (value: unknown) =>
    value === requiredText;

  return {
    requiredText,
  };
}
