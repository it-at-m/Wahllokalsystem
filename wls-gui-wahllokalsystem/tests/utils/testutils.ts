import type { RunnerTestSuite, TestContext } from "vitest";
import type { App } from "vue";

import { vi } from "vitest";
import { createApp } from "vue";

export function getSnapshotFilename(context: TestContext): string {
  const path = `./__snapshots__/${getSuitePath(context.task.suite)}/`;
  return `${path}/${context.task.name}.html`;
}

function getSuitePath(suite?: RunnerTestSuite): string {
  const pathElementsBottomUp: string[] = [];

  let suiteForPath = suite;
  while (
    suiteForPath !== undefined &&
    suiteForPath.suite !== suiteForPath &&
    !pathElementsBottomUp.includes(suiteForPath.name)
  ) {
    pathElementsBottomUp.push(suiteForPath.name);
    suiteForPath = suiteForPath.suite;
  }

  return pathElementsBottomUp.reverse().join("/");
}

export function withSetup<T>(composable: () => T): [T, App] {
  let result: T;
  const app = createApp({
    setup() {
      result = composable();
      return vi.fn();
    },
  });
  app.mount(document.createElement("div"));
  // @ts-expect-error: error TS2454: Variable 'result' is used before being assigned.
  return [result, app];
}

export const COMPONENT_RENDER_TESTS = "visual logic";
export const COMPONENT_EVENT_TESTS = "behavioral logic";

/**
 * Mocks dynamic components
 */
export function mockAndStubResizeObserver() {
  const ResizeObserverMock = class MockedResizeObserverMock {
    observe = vi.fn();
    unobserve = vi.fn();
    disconnect = vi.fn();
  };
  vi.stubGlobal("ResizeObserver", ResizeObserverMock);
}
