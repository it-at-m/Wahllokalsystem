import type { VueWrapper } from "@vue/test-utils";

import { useProgressTestDataFactory } from "@tests/types/common/ProgressTestDataFactory.ts";
import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import {
  COMPONENT_RENDER_TESTS,
  getSnapshotFilename,
} from "@tests/utils/testutils.ts";
import { mount } from "@vue/test-utils";
import { beforeEach, describe, expect, it } from "vitest";

import BaseRowInitProgress from "@/components/wahltag/BaseRowInitProgress.vue";
import vuetify from "@/plugins/vuetify.ts";

const { createProgressComplete } = useProgressTestDataFactory();
const { generateRandomNumber } = useCommonTestDataFactory();

describe("BaseRowInitProgress.vue", () => {
  let wrapper: VueWrapper<InstanceType<typeof BaseRowInitProgress>>;

  beforeEach(() => {
    wrapper = mount(BaseRowInitProgress, {
      global: { plugins: [vuetify] },
      props: {
        progress: createProgressComplete(),
        title: `title ${generateRandomNumber(4)}`,
      },
    });
  });

  describe(COMPONENT_RENDER_TESTS, () => {
    it("should_renderTitleAndProgress_when_progressIsActive", async (context) => {
      await wrapper.setProps({
        progress: {
          active: true,
          total: 100,
          finished: 32,
        },
        title: "test progress",
      });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });

    it("should_renderTitleAndProgress_when_progressIsNotActive", async (context) => {
      await wrapper.setProps({
        progress: {
          active: false,
          total: 100,
          finished: 32,
        },
        title: "test progress",
      });

      await expect(wrapper.html()).toMatchFileSnapshot(
        getSnapshotFilename(context)
      );
    });
  });
});
