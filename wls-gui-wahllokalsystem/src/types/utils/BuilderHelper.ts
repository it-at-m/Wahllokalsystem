type SplitAndCapitalize<S extends string> =
  S extends `${infer Head}_${infer Tail}`
    ? `${Capitalize<Lowercase<Head>>}${SplitAndCapitalize<Tail>}`
    : Capitalize<Lowercase<S>>;

type RemoveFirstWord<S extends string> = S extends `${string}_${infer Tail}`
  ? Tail
  : S;

export type GenericFluentFunctions<
  TFluentAPI,
  TEnum extends Record<string, unknown>,
  TPrefix extends string = "with",
> = {
  [K in keyof TEnum as `${TPrefix}${SplitAndCapitalize<RemoveFirstWord<K & string>>}`]: () => TFluentAPI;
};
