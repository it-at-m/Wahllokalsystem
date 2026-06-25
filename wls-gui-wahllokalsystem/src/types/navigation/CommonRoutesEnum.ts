export const CommonRoutesEnum = {
  LOGOUT: "logout",
} as const;
export type CommonRoutesEnum =
  (typeof CommonRoutesEnum)[keyof typeof CommonRoutesEnum];
