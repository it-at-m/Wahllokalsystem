export interface Task {
  name: string;
  callback: () => Promise<unknown>;
}
