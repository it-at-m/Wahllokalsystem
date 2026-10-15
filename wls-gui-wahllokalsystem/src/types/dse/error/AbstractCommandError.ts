export abstract class AbstractCommandError extends Error {
  public readonly command: string;

  constructor(command: string, message?: string, options?: ErrorOptions) {
    super(message, options);
    this.command = command;
  }
}
