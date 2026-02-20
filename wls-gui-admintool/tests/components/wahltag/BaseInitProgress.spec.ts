import { useProgressTestDataFactory } from "@tests/types/common/ProgressTestDataFactory.ts";
import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount, VueWrapper } from "@vue/test-utils";
import { beforeEach, describe, expect, it } from "vitest";

import BaseInitProgress from "@/components/wahltag/BaseInitProgress.vue";
import BaseRowInitProgress from "@/components/wahltag/BaseRowInitProgress.vue";
import vuetify from "@/plugins/vuetify.ts";

const { createProgressComplete } = useProgressTestDataFactory();

describe("BaseInitProgress.vue", () => {
  let wrapper: VueWrapper<InstanceType<typeof BaseInitProgress>>;

  beforeEach(() => {
    wrapper = mount(BaseInitProgress, {
      shallow: true,
      global: { plugins: [vuetify] },
    });
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_useTheCorrectProps_when_bindToEachRow", async () => {
      const props = {
        awerte: createProgressComplete(),
        referendumvorschlaege: createProgressComplete(),
        wahlvorschlaege: createProgressComplete(),
      };
      await wrapper.setProps(props);

      const rowsInitProgress = wrapper.findAllComponents(BaseRowInitProgress);
      expect(rowsInitProgress[0]?.props()["progress"]).toStrictEqual(
        props.wahlvorschlaege
      );
      expect(rowsInitProgress[1]?.props()["progress"]).toStrictEqual(
        props.referendumvorschlaege
      );
      expect(rowsInitProgress[2]?.props()["progress"]).toStrictEqual(
        props.awerte
      );
    });

    it("should_renderProgressForWahlvorschlaegeReferendumvorlagenAndAWerte_when_allProgressPropertiesAreSet", async (context) => {
      const props = {
        awerte: createProgressComplete(),
        referendumvorschlaege: createProgressComplete(),
        wahlvorschlaege: createProgressComplete(),
      };
      await wrapper.setProps(props);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderOnlyWahlvorschlaegeProgressRow_when_onlyWahlvorschlaegeAreGiven", async (context) => {
      const props = {
        wahlvorschlaege: createProgressComplete(),
      };
      await wrapper.setProps(props);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderOnlyReferendumvorlagenProgressRow_when_onlyReferendumvorschlaegeAreGiven", async (context) => {
      const props = {
        referendumvorschlaege: createProgressComplete(),
      };
      await wrapper.setProps(props);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderOnlyAWerteProgressRow_when_onlyAWerteAreGiven", async (context) => {
      const props = {
        awerte: createProgressComplete(),
      };
      await wrapper.setProps(props);

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
