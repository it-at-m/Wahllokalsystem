import type {
  NavigationGuard,
  NavigationGuardNext,
  NavigationGuardNextCallback,
  RouteLocationNormalized,
  RouteLocationRaw,
} from "vue-router";

type NavigationGuardNextArgument =
  boolean | Error | NavigationGuardNextCallback | RouteLocationRaw;

async function isGuardValid(
  guard: NavigationGuard,
  to: RouteLocationNormalized,
  from: RouteLocationNormalized
): Promise<boolean> {
  let hasCalledNext = false;
  let isValid = false;

  const fakeNext: NavigationGuardNext = (arg?: NavigationGuardNextArgument) => {
    hasCalledNext = true;
    isValid = arg === undefined || arg === true;
  };

  const result = await guard(to, from, fakeNext);

  return hasCalledNext ? isValid : result === undefined || result === true;
}

/**
 * Evaluates navigation guards: At least one of the navigation guards must be true to receive "true"
 */
export function anyGuard(...guards: NavigationGuard[]): NavigationGuard {
  return async (to, from, next) => {
    for (const guard of guards) {
      const isValid = await isGuardValid(
        guard,
        to as RouteLocationNormalized,
        from
      );

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
      const isValid = await isGuardValid(
        guard,
        to as RouteLocationNormalized,
        from
      );

      if (!isValid) {
        return next(false);
      }
    }
    next();
  };
}
