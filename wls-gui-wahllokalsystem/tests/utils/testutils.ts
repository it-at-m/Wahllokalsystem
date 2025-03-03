import type { ExtendedContext, RunnerTestCase, RunnerTestSuite } from "vitest";

export function getSnapshotFilename(
  context: ExtendedContext<RunnerTestCase>
): string {
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
