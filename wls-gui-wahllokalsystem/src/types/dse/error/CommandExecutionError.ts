import { AbstractCommandError } from "@/types/dse/error/AbstractCommandError.ts";

export class CommandExecutionError extends AbstractCommandError {
  constructor(
    command: string,
    message = "Befehl war nicht ausführbar.",
    options?: ErrorOptions
  ) {
    super(command, message, options);
  }
}
