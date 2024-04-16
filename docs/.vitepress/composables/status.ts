import {
  mdiAlertOutline,
  mdiProgressQuestion,
  mdiThumbDownOutline,
  mdiThumbUpOutline,
} from "@mdi/js";

export enum Status {
  ACCEPTED = "accepted",
  REJECTED = "rejected",
  PROPOSED = "proposed",
  DEPRECATED = "deprecated",
}

const empty = "";

const mapStatusToColorMap = new Map<Status, String>([
  [Status.ACCEPTED, "green"],
  [Status.REJECTED, "red"],
  [Status.PROPOSED, "gray"],
  [Status.DEPRECATED, "orange"],
]);

const mapStatusToI18NText = new Map<String, Map<Status, String>>([
  [
    "en-US",
    new Map<Status, String>([
      [Status.ACCEPTED, "accepted"],
      [Status.REJECTED, "rejected"],
      [Status.PROPOSED, "proposed"],
      [Status.DEPRECATED, "deprecated"],
    ]),
  ],
  [
    "de-DE",
    new Map<Status, String>([
      [Status.ACCEPTED, "angenommen"],
      [Status.REJECTED, "abgelehnt"],
      [Status.PROPOSED, "vorgeschlagen"],
      [Status.DEPRECATED, "veraltet"],
    ]),
  ],
]);

const mapStatusToIconPath = new Map<Status, String>([
  [Status.ACCEPTED, mdiThumbUpOutline],
  [Status.REJECTED, mdiThumbDownOutline],
  [Status.PROPOSED, mdiProgressQuestion],
  [Status.DEPRECATED, mdiAlertOutline],
]);

export const useStatus = function () {
  function statusToIconPathOrEmpty(status: Status): String {
    return mapStatusToIconPath.get(status) ?? empty;
  }

  function statusToColorOrEmpty(status: Status): String {
    return mapStatusToColorMap.get(status) ?? empty;
  }

  function statusToI18NTextOrEmpty(lang: String, status: Status): String {
    return mapStatusToI18NText.get(lang)?.get(status) ?? empty;
  }

  return {
    statusToIconPathOrEmpty,
    statusToColorOrEmpty,
    statusToI18NTextOrEmpty,
  };
};
