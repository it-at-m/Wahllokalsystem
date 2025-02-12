/* eslint-disable */
export const REQUIRED = (value: any) => !!value || "Feld darf nicht leer sein.";
export const MAX_LENGTH = (length: number) => (value: any) => {
  return (
    (value && value.length <= length) || `Maximale Länge ist ${length} Zeichen.`
  );
};
export const MIN_LENGTH = (length: number) => (value: any) => {
  return (
    (value && value.length >= length) || `Minimale Länge ist ${length} Zeichen.`
  );
};
