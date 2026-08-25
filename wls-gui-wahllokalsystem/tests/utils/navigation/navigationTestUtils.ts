import type { NavigationDefinition } from "@/types/navigation/NavigationDefinition.ts";
import type { ComputedRef } from "vue";

import { expect } from "vitest";

export function assertThatRequiredRoutesAreReturned(
  navigation: ComputedRef<NavigationDefinition[]>,
  wahlID: string,
  wahlbezirkID: string,
  stepsEnum: Record<string, string>
) {
  const expectedRouteNames = Object.values(stepsEnum);
  expect(navigation.value.length).toStrictEqual(expectedRouteNames.length);
  expectedRouteNames.forEach((expectedRouteName) => {
    expect(navigation.value).satisfy(
      (navigationItems: NavigationDefinition[]) => {
        return navigationItems.some(
          (navigationItem) =>
            navigationItem.targetRoute.name === expectedRouteName &&
            navigationItem.targetRoute.params?.wahlId === wahlID &&
            navigationItem.targetRoute.params?.wahlbezirkId === wahlbezirkID
        );
      },
      `route with name ${expectedRouteName} not found in navigation array`
    );
  });
}
