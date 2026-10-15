export interface SyncronizeDataResult {
  numberOfTasksRan: number;
  numberOfTasksFailed: number;
  numberOfTasksSucceeded: number;
  numberOfDirtyTasksRemaining: number;
}
