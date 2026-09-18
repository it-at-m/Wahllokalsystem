import type { NavigationGuard, RouteLocationNormalized } from "vue-router";

import { describe, expect, it, vi } from "vitest";

import { allGuards, anyGuard } from "@/composables/navigation/routerUtils.ts";

describe("routerUtils.ts", () => {
  const to = {} as RouteLocationNormalized;
  const from = {} as RouteLocationNormalized;

  function createGuard(isValid: boolean): NavigationGuard {
    return (_to, _from, next) => next(isValid);
  }

  describe("anyGuard", () => {
    it("should_permitNavigation_when_oneGuardIsValid", async () => {
      const invalidGuard = vi.fn(createGuard(false));
      const validGuard = vi.fn(createGuard(true));
      const next = vi.fn();

      await anyGuard(invalidGuard, validGuard)(to, from, next);

      expect(invalidGuard).toHaveBeenCalledExactlyOnceWith(
        to,
        from,
        expect.any(Function)
      );
      expect(validGuard).toHaveBeenCalledExactlyOnceWith(
        to,
        from,
        expect.any(Function)
      );
      expect(next).toHaveBeenCalledExactlyOnceWith();
    });

    it("should_stopEvaluatingGuards_when_oneGuardIsValid", async () => {
      const validGuard = vi.fn(createGuard(true));
      const subsequentGuard = vi.fn(createGuard(true));
      const next = vi.fn();

      await anyGuard(validGuard, subsequentGuard)(to, from, next);

      expect(subsequentGuard).not.toHaveBeenCalled();
      expect(next).toHaveBeenCalledExactlyOnceWith();
    });

    it("should_preventNavigation_when_noGuardIsValid", async () => {
      const next = vi.fn();

      await anyGuard(createGuard(false), createGuard(false))(to, from, next);

      expect(next).toHaveBeenCalledExactlyOnceWith(false);
    });
  });

  describe("allGuards", () => {
    it("should_permitNavigation_when_allGuardsAreValid", async () => {
      const firstGuard = vi.fn(createGuard(true));
      const secondGuard = vi.fn(createGuard(true));
      const next = vi.fn();

      await allGuards(firstGuard, secondGuard)(to, from, next);

      expect(firstGuard).toHaveBeenCalledExactlyOnceWith(
        to,
        from,
        expect.any(Function)
      );
      expect(secondGuard).toHaveBeenCalledExactlyOnceWith(
        to,
        from,
        expect.any(Function)
      );
      expect(next).toHaveBeenCalledExactlyOnceWith();
    });

    it("should_stopEvaluatingGuardsAndPreventNavigation_when_oneGuardIsInvalid", async () => {
      const invalidGuard = vi.fn(createGuard(false));
      const subsequentGuard = vi.fn(createGuard(true));
      const next = vi.fn();

      await allGuards(invalidGuard, subsequentGuard)(to, from, next);

      expect(subsequentGuard).not.toHaveBeenCalled();
      expect(next).toHaveBeenCalledExactlyOnceWith(false);
    });
  });
});
