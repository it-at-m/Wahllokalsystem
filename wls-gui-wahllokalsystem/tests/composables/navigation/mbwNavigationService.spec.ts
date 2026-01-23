import type { NavigationDefinition } from "@/types/navigation/NavigationDefinition.ts";
import type { ComputedRef } from "vue";

import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useCommonErgebnismeldungTestDataFactory } from "@tests/utils/ergebnismeldung/common/commonErgebnismeldungTestDataFactory.ts";
import { useStatusTestDataFactory } from "@tests/utils/ergebnismeldung/common/statusTestDataFactory.ts";
import { flushPromises } from "@vue/test-utils";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useMbwNavigationService } from "@/composables/navigation/mbwNavigationService.ts";
import { useStatusStore } from "@/stores/statusStore.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";

const { generateRandomString } = useCommonTestDataFactory();
const { prepareStatus } = useStatusTestDataFactory();
const { prepareBezirkUndWahlID } = useCommonErgebnismeldungTestDataFactory();

describe("mbwNavigationService.ts", () => {
  beforeEach(() => {
    createTestingPinia({
      createSpy: vi.fn,
    });
  });

  describe("navigation", () => {
    it("should_returnEmptyArray_when_noStatusExists", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      const unitUnderTest = useMbwNavigationService(wahlID, wahlbezirkID);

      const navigation = unitUnderTest.navigation;
      expect(navigation.value).toStrictEqual([]);
    });

    it("should_returnNavigation_when_statusExists", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      useStatusStore().status = [
        prepareStatus()
          .bezirkUndWahlID(
            prepareBezirkUndWahlID()
              .wahlID(wahlID)
              .wahlbezirkID(wahlbezirkID)
              .build()
          )
          .build(),
      ];

      const unitUnderTest = useMbwNavigationService(wahlID, wahlbezirkID);

      const navigation = unitUnderTest.navigation;

      assertThatRequiredRoutesAreReturned(navigation, wahlID, wahlbezirkID);
    });

    it("should_returnNavigation_when_statusIsSetAfterInit", async () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);

      const unitUnderTest = useMbwNavigationService(wahlID, wahlbezirkID);
      const navigation = unitUnderTest.navigation;

      expect(navigation.value.length).toStrictEqual(0);

      useStatusStore().status = [
        prepareStatus()
          .bezirkUndWahlID(
            prepareBezirkUndWahlID()
              .wahlID(wahlID)
              .wahlbezirkID(wahlbezirkID)
              .build()
          )
          .build(),
      ];
      await flushPromises();

      assertThatRequiredRoutesAreReturned(navigation, wahlID, wahlbezirkID);
    });

    function assertThatRequiredRoutesAreReturned(
      navigation: ComputedRef<NavigationDefinition[]>,
      wahlID: string,
      wahlbezirkID: string
    ) {
      const expectedRouteNames = Object.values(MbwRoutesEnum);
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
  });
});
