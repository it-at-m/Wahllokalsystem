import type { ManagedStimmzettel } from "@/composables/dse/ManagedStimmzettel.ts";

export interface CommandHandler {
  canHandle: (command: string) => boolean;
  /**
   * throws CommandExecutionError when an error occurred during processing of command
   *
   * @param command
   * @param stimmzettel
   * @throws CommandExecutionError when an error occurred during processing of command
   */
  handleOrThrow: (command: string, stimmzettel: ManagedStimmzettel) => void;
}
