import { createTestingPinia } from "@pinia/testing";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useCommonErgebnismeldungTestDataFactory } from "@tests/utils/ergebnismeldung/common/commonErgebnismeldungTestDataFactory.ts";
import { useWorkflowTestDataFactory } from "@tests/utils/navigation/NavigationTestDataFactory.ts";
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
import {
  ROUTE_BEGINN_STIMMABGABE,
  ROUTE_ERFASSUNG_WAHLBRIEFE,
  ROUTE_STIMMABGABE,
  ROUTE_STIMMABGABEVERMERKE,
  ROUTE_WAHLBRIEFE_ZULASSEN,
  ROUTE_WAHLSCHEINE,
  ROUTE_WAHLUMGEBUNG,
  ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS,
  ROUTE_WAHLVORSTAND,
  ROUTES_HOME,
} from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { generateRandomString } = useCommonTestDataFactory();
const { prepareUser } = useUserTestDataFactory();
const { prepareBezirkUndWahlID } = useCommonErgebnismeldungTestDataFactory();
const { prepareWahl } = useWahlTestDataFactory();
const { prepareElectionWorkflow } = useWorkflowTestDataFactory();

const mockDefinitions = vi.hoisted(() => ({
  mbwGetNextRouteOrNull: vi.fn(),
}));

vi.mock("@/types/navigation/NextStepImplConstants.ts", () => ({
  MBWNextStepImpl: {
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
      useWorkflowStore().isWahlvorstandErfasst = false;

      const result = unitUnderTest.getNextRoute();
      expect(result).toEqual(unitUnderTest.routeWithName(ROUTE_WAHLVORSTAND));
    });

    it("should_returnRouteToHome_when_noElectionsAreGivenAndUsersWahlbezirkartIsUWB", () => {
      useUserStore().user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.UWB)
        .wahlMetaData([])
        .build();
      useWorkflowStore().isWahlvorstandErfasst = true;
      useWorkflowStore().isWaehlerverzeichnisErfasst = true;
      useWorkflowStore().isWahleroeffnungErfasst = true;
      useWorkflowStore().isStimmabgabeErfasst = true;
      useWorkflowStore().isStimmabgabevermerkeErfasst = true;

      const result = unitUnderTest.getNextRoute();
      expect(result).toEqual(unitUnderTest.routeWithName(ROUTES_HOME));
    });

    it("should_returnRouteToHome_when_noElectionsAreGivenAndUsersWahlbezirkartIsBWB", () => {
      useUserStore().user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.BWB)
        .wahlMetaData([])
        .build();
      useWorkflowStore().isWahlvorstandErfasst = true;
      useWorkflowStore().isWahlumgebungErfasst = true;
      useWorkflowStore().isWahlbriefeErfassenErfasst = true;
      useWorkflowStore().isWahlbriefeZulassenErfasst = true;
      useWorkflowStore().isAnzahlWahlscheineErfasst = true;

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
      useWorkflowStore().isWahlvorstandErfasst = true;
      useWorkflowStore().isWaehlerverzeichnisErfasst = true;
      useWorkflowStore().isWahleroeffnungErfasst = true;
      useWorkflowStore().isStimmabgabeErfasst = true;
      useWorkflowStore().isStimmabgabevermerkeErfasst = true;

      useWorkflowStore().electionWorkflowsStates = [
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
      useWorkflowStore().isWahlvorstandErfasst = true;
      useWorkflowStore().isWaehlerverzeichnisErfasst = true;
      useWorkflowStore().isWahleroeffnungErfasst = true;
      useWorkflowStore().isStimmabgabeErfasst = true;
      useWorkflowStore().isStimmabgabevermerkeErfasst = true;

      const mbwStatus = createStatusWithNiederschriftGedruckt(
        mbwWahlID,
        mbwWahlbezirkID,
        false
      );
      useWorkflowStore().electionWorkflowsStates = [
        createStatusWithNiederschriftGedruckt(wahlID1, wahlbezirkID1),
        mbwStatus,
        createStatusWithNiederschriftGedruckt(wahlID2, wahlbezirkID2),
      ];
      useWorkflowStore().isElectionFinished = vi
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

    it("should_returnRouteToWahlumgebung_when_allPreviousStepsAreDoneAndWahlumgebungIsNotSetAndUserHasWahlbezirksArtBWB", () => {
      useUserStore().user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.BWB)
        .build();

      useWorkflowStore().isWahlvorstandErfasst = true;
      useWorkflowStore().isWahlumgebungErfasst = false;

      const result = unitUnderTest.getNextRoute();
      expect(result).toEqual(unitUnderTest.routeWithName(ROUTE_WAHLUMGEBUNG));
    });

    it("should_returnRouteToErfassungWahlbriefe_when_allPreviousStepsAreDoneAndWahlbrieferfassungIsNotSetAndUserHasWahlbezirksArtBWB", () => {
      useUserStore().user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.BWB)
        .build();

      useWorkflowStore().isWahlvorstandErfasst = true;
      useWorkflowStore().isWahlumgebungErfasst = true;
      useWorkflowStore().isWahlbriefeErfassenErfasst = false;

      const result = unitUnderTest.getNextRoute();
      expect(result).toEqual(
        unitUnderTest.routeWithName(ROUTE_ERFASSUNG_WAHLBRIEFE)
      );
    });

    it("should_returnRouteToWahlbriefeZulassen_when_allPreviousStepsAreDoneAndWahlbriefzulassungIsNotSetAndUserHasWahlbezirksArtBWB", () => {
      useUserStore().user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.BWB)
        .build();

      useWorkflowStore().isWahlvorstandErfasst = true;
      useWorkflowStore().isWahlumgebungErfasst = true;
      useWorkflowStore().isWahlbriefeErfassenErfasst = true;
      useWorkflowStore().isWahlbriefeZulassenErfasst = false;

      const result = unitUnderTest.getNextRoute();
      expect(result).toEqual(
        unitUnderTest.routeWithName(ROUTE_WAHLBRIEFE_ZULASSEN)
      );
    });

    it("should_returnRouteToWahlscheineErfassen_when_allPreviousStepsAreDoneAndWahlscheinerfassungIsNotSetAndUserHasWahlbezirksArtBWB", () => {
      useUserStore().user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.BWB)
        .build();

      useWorkflowStore().isWahlvorstandErfasst = true;
      useWorkflowStore().isWahlumgebungErfasst = true;
      useWorkflowStore().isWahlbriefeErfassenErfasst = true;
      useWorkflowStore().isWahlbriefeZulassenErfasst = true;
      useWorkflowStore().isAnzahlWahlscheineErfasst = false;

      const result = unitUnderTest.getNextRoute();
      expect(result).toEqual(unitUnderTest.routeWithName(ROUTE_WAHLSCHEINE));
    });

    it("should_returnRouteToWaehlerverzeichnis_when_allPreviousStepsAreDoneAndWaehlerverzeichnisIsNotSetAndUserHasWahlbezirksArtUWB", () => {
      useUserStore().user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.UWB)
        .build();

      useWorkflowStore().isWahlvorstandErfasst = true;
      useWorkflowStore().isWahlumgebungErfasst = true;
      useWorkflowStore().isWaehlerverzeichnisErfasst = false;

      const result = unitUnderTest.getNextRoute();
      expect(result).toEqual(
        unitUnderTest.routeWithName(ROUTE_WAHLVORBEREITUNG_WAEHLERVERZEICHNIS)
      );
    });

    it("should_returnRouteToBeginnStimmabgabe_when_allPreviousStepsAreDoneAndBeginnStimmabgabeIsNotSetAndUserHasWahlbezirksArtUWB", () => {
      useUserStore().user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.UWB)
        .build();

      useWorkflowStore().isWahlvorstandErfasst = true;
      useWorkflowStore().isWahlumgebungErfasst = true;
      useWorkflowStore().isWaehlerverzeichnisErfasst = true;
      useWorkflowStore().isWahleroeffnungErfasst = false;

      const result = unitUnderTest.getNextRoute();
      expect(result).toEqual(
        unitUnderTest.routeWithName(ROUTE_BEGINN_STIMMABGABE)
      );
    });

    it("should_returnRouteToStimmabgabe_when_allPreviousStepsAreDoneAndStimmabgabeIsNotSetAndUserHasWahlbezirksArtUWB", () => {
      useUserStore().user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.UWB)
        .build();

      useWorkflowStore().isWahlvorstandErfasst = true;
      useWorkflowStore().isWahlumgebungErfasst = true;
      useWorkflowStore().isWaehlerverzeichnisErfasst = true;
      useWorkflowStore().isWahleroeffnungErfasst = true;
      useWorkflowStore().isStimmabgabeErfasst = false;

      const result = unitUnderTest.getNextRoute();
      expect(result).toEqual(unitUnderTest.routeWithName(ROUTE_STIMMABGABE));
    });

    it("should_returnRouteToStimmabgabevermerke_when_allPreviousStepsAreDoneAndStimmabgabevermerkeIsNotSetAndUserHasWahlbezirksArtUWB", () => {
      useUserStore().user = prepareUser()
        .wahlbezirksArt(WahlbezirksArtEnum.UWB)
        .build();

      useWorkflowStore().isWahlvorstandErfasst = true;
      useWorkflowStore().isWahlumgebungErfasst = true;
      useWorkflowStore().isWaehlerverzeichnisErfasst = true;
      useWorkflowStore().isWahleroeffnungErfasst = true;
      useWorkflowStore().isStimmabgabeErfasst = true;
      useWorkflowStore().isStimmabgabevermerkeErfasst = false;

      const result = unitUnderTest.getNextRoute();
      expect(result).toEqual(
        unitUnderTest.routeWithName(ROUTE_STIMMABGABEVERMERKE)
      );
    });
  });

  function createStatusWithNiederschriftGedruckt(
    wahlID: string,
    wahlbezirkID: string,
    finished = true
  ) {
    return prepareElectionWorkflow()
      .bezirkUndWahlID(
        prepareBezirkUndWahlID()
          .wahlID(wahlID)
          .wahlbezirkID(wahlbezirkID)
          .build()
      )
      .isNiederschriftDone(finished)
      .build();
  }
});
