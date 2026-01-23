import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useCommonErgebnismeldungTestDataFactory } from "@tests/utils/ergebnismeldung/common/commonErgebnismeldungTestDataFactory.ts";
import { useStatusTestDataFactory } from "@tests/utils/ergebnismeldung/common/statusTestDataFactory.ts";
import { useUserTestDataFactory } from "@tests/utils/user/UserTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import {
  afterAll,
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";

import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import { ROUTE_WAHLVORSTAND, ROUTES_HOME } from "@/constants.ts";
import { useStatusStore } from "@/stores/statusStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const { generateRandomString } = useCommonTestDataFactory();
const { prepareStatus, prepareMeldung } = useStatusTestDataFactory();
const { prepareUser } = useUserTestDataFactory();
const { prepareBezirkUndWahlID } = useCommonErgebnismeldungTestDataFactory();
const { prepareWahl } = useWahlTestDataFactory();

const mockDefinitions = vi.hoisted(() => ({
  mbwGetNextRouteOrNull: vi.fn(),
}));

vi.mock("@/types/navigation/NextStepImplConstants.ts", () => ({
  MBWNestStepImpl: {
    getNextRouteOrNull: mockDefinitions.mbwGetNextRouteOrNull,
  },
  NullNextStepImpl: {
    getNextRouteOrNull: mockDefinitions.mbwGetNextRouteOrNull,
  },
}));

describe("navigationUtils.ts", () => {
  let unitUnderTest: ReturnType<typeof useNavigationUtils>;

  beforeEach(() => {
    createTestingPinia({
      createSpy: vi.fn,
      stubActions: false,
    });
    unitUnderTest = useNavigationUtils();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.restoreAllMocks();
  });

  describe("routeWithName", () => {
    it("should_setNameInObject_when_routeNameIsGiven", () => {
      const routeName = generateRandomString(20);

      const result = unitUnderTest.routeWithName(routeName);

      const expectedResult = {
        name: routeName,
      };
      expect(result).toStrictEqual(expectedResult);
    });
  });

  describe("routeWithNameAndParams", () => {
    it.each([
      {
        params: {},
        additionalTestCaseDescription: "AndParameterisEmptyObject",
      },
      {
        params: { [generateRandomString(4)]: generateRandomString(10) },
        additionalTestCaseDescription: "AndParameterisHasOneProperty",
      },
      {
        params: {
          [generateRandomString(4)]: generateRandomString(10),
          [generateRandomString(5)]: generateRandomString(10),
        },
        additionalTestCaseDescription: "AndParameterisHasTwoProperties",
      },
    ])(
      "should_setNameAndParamsInObject_when_routeNameAndParamsAreGiven$additionalTestCaseDescription",
      (args) => {
        const routeName = generateRandomString(20);

        const result = unitUnderTest.routeWithNameAndParams(
          routeName,
          args.params
        );

        const expectedResult = {
          name: routeName,
          params: args.params,
        };
        expect(result).toStrictEqual(expectedResult);
      }
    );
  });

  describe("getNextRoute", () => {
    it("should_returnRouteToWahlvorstand_when_wahlvorstandIsNotSet", () => {
      useStatusStore().isWahlvorstandErfasst = false;

      const result = unitUnderTest.getNextRoute();
      expect(result).toEqual(unitUnderTest.routeWithName(ROUTE_WAHLVORSTAND));
    });

    it("should_returnRouteToHome_when_noElectionAreGiven", () => {
      useUserStore().user = prepareUser().wahlMetaData([]).build();
      useStatusStore().isWahlvorstandErfasst = true;

      const result = unitUnderTest.getNextRoute();
      expect(result).toEqual(unitUnderTest.routeWithName(ROUTES_HOME));
    });

    it("should_returnRouteToHome_when_allElectionFinished", () => {
      const wahlID1 = "wahlID1";
      const wahlbezirkID1 = "wahlbezirkID1";

      const wahlID2 = "wahlID2";
      const wahlbezirkID2 = "wahlbezirkID2";

      useUserStore().user = prepareUser()
        .wahlMetaData([
          {
            wahlbezirkID: wahlbezirkID1,
            wahlID: wahlID1,
            wahlnummer: generateRandomString(2),
          },
          {
            wahlbezirkID: wahlbezirkID2,
            wahlID: wahlID2,
            wahlnummer: generateRandomString(2),
          },
        ])
        .build();
      useStatusStore().isWahlvorstandErfasst = true;

      useStatusStore().status = [
        createStatusWithNiederschriftGedruckt(wahlID1, wahlbezirkID1),
        createStatusWithNiederschriftGedruckt(wahlID2, wahlbezirkID2),
      ];

      const result = unitUnderTest.getNextRoute();
      expect(result).toEqual(unitUnderTest.routeWithName(ROUTES_HOME));
    });

    it("should_returnRouteOfMbwNextStepHandler_when_mbwIstNotDone", () => {
      const wahlID1 = "wahlID1";
      const wahlbezirkID1 = "wahlbezirkID1";

      const wahlID2 = "wahlID2";
      const wahlbezirkID2 = "wahlbezirkID2";

      const mbwWahlID = generateRandomString(10);
      const mbwWahlbezirkID = generateRandomString(10);

      useUserStore().user = prepareUser()
        .wahlMetaData([
          {
            wahlbezirkID: wahlbezirkID1,
            wahlID: wahlID1,
            wahlnummer: generateRandomString(2),
          },
          {
            wahlbezirkID: mbwWahlbezirkID,
            wahlID: mbwWahlID,
            wahlnummer: generateRandomString(2),
          },
          {
            wahlbezirkID: wahlbezirkID2,
            wahlID: wahlID2,
            wahlnummer: generateRandomString(2),
          },
        ])
        .build();
      useStatusStore().isWahlvorstandErfasst = true;

      const mbwStatus = createStatusWithNiederschriftGedruckt(
        mbwWahlID,
        mbwWahlbezirkID,
        false
      );
      useStatusStore().status = [
        createStatusWithNiederschriftGedruckt(wahlID1, wahlbezirkID1),
        mbwStatus,
        createStatusWithNiederschriftGedruckt(wahlID2, wahlbezirkID2),
      ];
      useStatusStore().isElectionFinished = vi
        .fn()
        .mockImplementation(
          (wahlID: string, wahlbezirkID: string) =>
            wahlID !== mbwWahlID || wahlbezirkID !== mbwWahlbezirkID
        );

      const mockedWahl = prepareWahl().wahlart(WahlWahlartEnum.Mbw).build();
      useWahlenStore().wahlenActions.getWahlOrUndefinedById = vi
        .fn()
        .mockReturnValueOnce(mockedWahl);

      const mockedNextMbwRoute = unitUnderTest.routeWithName(
        generateRandomString(10)
      );
      mockDefinitions.mbwGetNextRouteOrNull.mockReturnValueOnce(
        mockedNextMbwRoute
      );

      const result = unitUnderTest.getNextRoute();
      expect(result).toStrictEqual(mockedNextMbwRoute);
      expect(mockDefinitions.mbwGetNextRouteOrNull.mock.calls).toStrictEqual([
        [mbwStatus],
      ]);
    });
  });

  function createStatusWithNiederschriftGedruckt(
    wahlID: string,
    wahlbezirkID: string,
    gedruckt = true
  ) {
    return prepareStatus()
      .bezirkUndWahlID(
        prepareBezirkUndWahlID()
          .wahlID(wahlID)
          .wahlbezirkID(wahlbezirkID)
          .build()
      )
      .niederschrift(prepareMeldung().gedruckt(gedruckt).build())
      .build();
  }
});
