package de.muenchen.oss.wahllokalsystem.authservice.service;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public enum WahlbezirksartModel {
  UWB,
  BWB
}
