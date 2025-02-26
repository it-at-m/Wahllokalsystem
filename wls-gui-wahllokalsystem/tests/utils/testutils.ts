import type { ExtendedContext, RunnerTestCase, RunnerTestSuite } from "vitest";

export function getSnapshotFilename(
  context: ExtendedContext<RunnerTestCase>
): string {
  const path = `./__snapshots__/${getSuitPath(context.task.suite)}/`;
  return `${path}/${context.task.name}.html`;
}

function getSuitPath(suite?: RunnerTestSuite): string {
  const pathElementsBottomUp: string[] = [];

  let suitForPath = suite;
  while (
    suitForPath !== undefined &&
    suitForPath.suite !== suitForPath &&
    !pathElementsBottomUp.includes(suitForPath.name)
  ) {
    pathElementsBottomUp.push(suitForPath.name);
    suitForPath = suitForPath.suite;
  }

  return pathElementsBottomUp.reverse().join("/");
}
