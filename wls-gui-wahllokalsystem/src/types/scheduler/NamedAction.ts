export class NamedAction {
  title: string;
  action: () => void;

  constructor(title: string, action: () => void) {
    this.title = title;
    this.action = action;
  }
}
