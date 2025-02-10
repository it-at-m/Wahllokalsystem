/* eslint-disable */
export const REQUIRED = (value: any) => !!value || "Feld darf nicht leer sein.";
export const MAX_LENGTH = (length: number) => (value: any) => {
    return (value && value.length <= length) || `Maximum length is ${length} characters.`;
};
export const MIN_LENGTH = (length: number) => (value: any) => {
    return (value && value.length >= length) || `Minimum length is ${length} characters.`;
};
