import type { VueWrapper } from "@vue/test-utils";

import { useAWerteTestDataFactory } from "@tests/types/aWerte/AWerteTestDataFactory.ts";
import { useBasisdatenTestDataFactory } from "@tests/types/basisdaten/BasisdatenTestDataFactory.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { flushPromises, mount } from "@vue/test-utils";
import { afterEach, describe, expect, it, vi } from "vitest";

import BaseInitProgress from "@/components/wahltag/BaseInitProgress.vue";
import TheWahltagInitProgressDiv from "@/components/wahltag/TheWahltagInitProgressDiv.vue";
import vuetify from "@/plugins/vuetify.ts";

const mockDefinitions = vi.hoisted(() => ({
  getAsyncProgress: vi.fn(),
  getAWerteProgress: vi.fn(),
}));

vi.mock(import("@/composables/basisdaten/basisdatenService.ts"), () => ({
  useBasisdatenService: () => ({
    getAsyncProgress: mockDefinitions.getAsyncProgress,
  }),
}));
vi.mock(import("@/composables/aWerte/aWerteService.ts"), () => ({
  useAWerteService: () => ({
    getAWerteProgress: mockDefinitions.getAWerteProgress,
  }),
}));

const { createAWerteInitProgressComplete, prepareAWerteInitProgress } =
  useAWerteTestDataFactory();
const { prepareBasisdatenInitProgressComplete } =
  useBasisdatenTestDataFactory();
const { generateRandomDateTimeAsString } = useCommonTestDataFactory();

describe("TheWahltagInitProgressDiv.vue", () => {
  let wrapper: VueWrapper<InstanceType<typeof TheWahltagInitProgressDiv>>;

  afterEach(() => {
    wrapper.unmount();
    vi.clearAllMocks();
    vi.resetAllMocks();
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_showLoadingProgress_when_loadingOfVorschlaegeIsNotFinished", async (context) => {
      const mockedVorschlageResponseData =
        prepareBasisdatenInitProgressComplete()
          .forWahltag("wahltag")
          .wahlNummer("wahlnummer")
          .lastFinishTime(undefined)
          .build();
      mockDefinitions.getAsyncProgress.mockReturnValue(
        Promise.resolve(mockedVorschlageResponseData)
      );

      const mockedAWerteProgressResponseData = prepareAWerteInitProgress()
        .lastFinishTime(generateRandomDateTimeAsString())
        .build();
      mockDefinitions.getAWerteProgress.mockReturnValue(
        Promise.resolve(mockedAWerteProgressResponseData)
      );

      wrapper = mountWrapper();

      await flushPromises();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showLoadingProgress_when_loadingOfAWerteIsNotFinished", async (context) => {
      const mockedVorschlageResponseData =
        prepareBasisdatenInitProgressComplete()
          .forWahltag("wahltag")
          .wahlNummer("wahlnummer")
          .lastFinishTime(generateRandomDateTimeAsString())
          .build();
      mockDefinitions.getAsyncProgress.mockReturnValue(
        Promise.resolve(mockedVorschlageResponseData)
      );

      const mockedAWerteProgressResponseData = prepareAWerteInitProgress()
        .lastFinishTime(undefined)
        .build();
      mockDefinitions.getAWerteProgress.mockReturnValue(
        Promise.resolve(mockedAWerteProgressResponseData)
      );

      wrapper = mountWrapper();

      await flushPromises();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_showDoneMessage_when_loadingIsFinished", async (context) => {
      const mockedVorschlageResponseData =
        prepareBasisdatenInitProgressComplete()
          .forWahltag("wahltag")
          .wahlNummer("wahlnummer")
          .lastFinishTime("last finish time")
          .build();
      mockDefinitions.getAsyncProgress.mockReturnValue(
        Promise.resolve(mockedVorschlageResponseData)
      );

      const mockedAWerteProgressResponseData = prepareAWerteInitProgress()
        .lastFinishTime(generateRandomDateTimeAsString())
        .build();
      mockDefinitions.getAWerteProgress.mockReturnValue(
        Promise.resolve(mockedAWerteProgressResponseData)
      );

      wrapper = mountWrapper();

      await flushPromises();

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_bindPropertiesCorrectly_when_isLoading", async () => {
      const mockedVorschlageResponseData =
        prepareBasisdatenInitProgressComplete()
          .lastFinishTime(undefined)
          .build();
      mockDefinitions.getAsyncProgress.mockReturnValue(
        Promise.resolve(mockedVorschlageResponseData)
      );
      const mockedAWerteProgressResponseData =
        createAWerteInitProgressComplete();
      mockDefinitions.getAWerteProgress.mockReturnValue(
        Promise.resolve(mockedAWerteProgressResponseData)
      );

      wrapper = mount(TheWahltagInitProgressDiv, {
        global: {
          plugins: [vuetify],
        },
        props: {},
      });

      await flushPromises();

      const baseInitProgressComponent = wrapper.findComponent(BaseInitProgress);

      expect(baseInitProgressComponent.props("awerte")).toStrictEqual(
        mockedAWerteProgressResponseData
      );
      expect(
        baseInitProgressComponent.props("referendumvorschlaege")
      ).toStrictEqual(mockedVorschlageResponseData.referendumvorlagen);
      expect(baseInitProgressComponent.props("wahlvorschlaege")).toStrictEqual(
        mockedVorschlageResponseData.wahlvorschlaege
      );
    });
  });
});

function mountWrapper() {
  return mount(TheWahltagInitProgressDiv, {
    global: {
      plugins: [vuetify],
      stubs: {
        BaseInitProgress: true,
      },
    },
    props: {},
  });
}
