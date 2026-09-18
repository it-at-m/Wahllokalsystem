import type {
  NavigationGuard,
  NavigationGuardNext,
  NavigationGuardNextCallback,
  RouteLocationNormalized,
  RouteLocationRaw,
} from "vue-router";

type NavigationGuardNextArgument =
  boolean | Error | NavigationGuardNextCallback | RouteLocationRaw;

/**
 * Evaluates navigation guards: At least one of the navigation guards must be true to receive "true"
 */
export function anyGuard(...guards: NavigationGuard[]): NavigationGuard {
  return async (to, from, next) => {
    for (const guard of guards) {
      let isValid = false;

      // dummy method for necessary inner guard
      const fakeNext: NavigationGuardNext = (
        arg?: NavigationGuardNextArgument
      ) => {
        if (arg === undefined || arg === true) {
          isValid = true;
        }
      };

      await guard(to as RouteLocationNormalized, from, fakeNext);

      if (isValid) {
        return next();
      }
    }

    next(false);
  };
}

/**
 * Evaluates navigation guards: All navigation guards must be true to receive true
 */
export function allGuards(...guards: NavigationGuard[]): NavigationGuard {
  return async (to, from, next) => {
    for (const guard of guards) {
      let isValid = false;

      // dummy method for necessary inner guard
      const fakeNext: NavigationGuardNext = (
        arg?: NavigationGuardNextArgument
      ) => {
        if (arg === undefined || arg === true) {
          isValid = true;
        }
      };

      await guard(to as RouteLocationNormalized, from, fakeNext);

      if (!isValid) {
        return next(false);
      }
    }
    next();
  };
}
