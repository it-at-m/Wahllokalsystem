package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltermindaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahltermindaten.AsyncProgress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface AsyncProgressDTOMapper {

  String NAMED_MAPPING_WAHLVORSCHLAEGE_LOADING = "setIsWahlvorschlaegeLoadingActive";
  String NAMED_MAPPING_REFERENDUMVORLAGEN_LOADING = "setIsReferendumLoadingActive";

  @Mapping(
      target = "wahlvorschlaegeLoadingActive",
      source = ".",
      qualifiedByName = NAMED_MAPPING_WAHLVORSCHLAEGE_LOADING)
  @Mapping(
      target = "referendumLoadingActive",
      source = ".",
      qualifiedByName = NAMED_MAPPING_REFERENDUMVORLAGEN_LOADING)
  AsyncProgressDTO toDto(AsyncProgress asyncProgress);

  @Named(NAMED_MAPPING_WAHLVORSCHLAEGE_LOADING)
  default boolean mapIsWahlvorschlaegeLoadingActive(final AsyncProgress asyncProgress) {
    return asyncProgress.getWahlvorschlaegeTotal() > asyncProgress.getWahlvorschlageFinished();
  }

  @Named(NAMED_MAPPING_REFERENDUMVORLAGEN_LOADING)
  default boolean mapIsReferendumLoadingActive(final AsyncProgress asyncProgress) {
    return asyncProgress.getReferendumVorlagenTotal()
        > asyncProgress.getReferendumVorlagenFinished();
  }
}
