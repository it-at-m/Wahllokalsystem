export function useStringNumberMapTools(container: Map<string, number>) {
  function add(key: string, value: number) {
    const currentValue = container.get(key);
    if (currentValue === undefined) {
      container.set(key, value);
    } else {
      container.set(key, currentValue + value);
    }
  }

  function getOrDefault(key: string, defaultValue = 0) {
    return container.get(key) ?? defaultValue;
  }

  function sum() {
    let result = 0;
    container.forEach((value) => (result += value));
    return result;
  }

  return {
    add,
    getOrDefault,
    sum,
  };
}
