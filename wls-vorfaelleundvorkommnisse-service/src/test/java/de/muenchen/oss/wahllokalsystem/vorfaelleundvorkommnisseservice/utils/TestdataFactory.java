package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignis;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis.EreignisartDTO;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis.EreignisseWriteDTO;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis.WahlbezirkEreignisseDTO;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis.EreignisDTO;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisartModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.WahlbezirkEreignisseModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisseWriteModel;
import java.time.LocalDateTime;
import java.util.List;

public class TestdataFactory {

    public static class CreateEreignisEntity {

        public static Ereignis withData(String wahlbezirkID) {
            return new Ereignis(wahlbezirkID, "beschreibung", LocalDateTime.now().withNano(0), Ereignisart.VORFALL);
        }

        public static Ereignis fromModel(EreignisModel ereignisModel, String wahlbezirkID) {
            return new Ereignis(wahlbezirkID, ereignisModel.beschreibung(), ereignisModel.uhrzeit(),
                    MapEreignisart.ereignisartModelToEreignisart(ereignisModel.ereignisart()));
        }

        public static List<Ereignis> listFromModel(EreignisseWriteModel ereignisWriteModel) {
            List<Ereignis> ereignisList = ereignisWriteModel.ereigniseintraege().stream().map(ereignis -> new Ereignis(ereignisWriteModel.wahlbezirkID(),
                    ereignis.beschreibung(), ereignis.uhrzeit(), MapEreignisart.ereignisartModelToEreignisart(ereignis.ereignisart()))).toList();
            return ereignisList;
        }
    }

    public static class CreateEreignisModel {

        public static EreignisModel withData() {
            return new EreignisModel("beschreibung", LocalDateTime.now().withNano(0), EreignisartModel.VORFALL);
        }

        public static EreignisModel withEreignisart(EreignisartModel ereignisart) {
            return new EreignisModel("beschreibung", LocalDateTime.now().withNano(0), ereignisart);
        }

        public static EreignisModel fromEntity(Ereignis ereignisEntity) {
            return new EreignisModel(ereignisEntity.getBeschreibung(), ereignisEntity.getUhrzeit(),
                    MapEreignisart.ereignisartToEreignisartModel(ereignisEntity.getEreignisart()));
        }
    }

    public static class CreateWahlbezirkEreignisseModel {

        public static WahlbezirkEreignisseModel withData(String wahlbezirkID, boolean keineVorfaelle, boolean keineVorkommnisse,
                List<EreignisModel> ereignisModelList) {
            return new WahlbezirkEreignisseModel(wahlbezirkID, keineVorfaelle, keineVorkommnisse, ereignisModelList);
        }
    }

    public static class CreateEreignisseWriteModel {

        public static EreignisseWriteModel withData(String wahlbezirkID, List<EreignisModel> ereignisModelList) {
            return new EreignisseWriteModel(wahlbezirkID, ereignisModelList);
        }

        public static EreignisseWriteModel fromDto(String wahlbezirkID, EreignisseWriteDTO ereignisseWriteDTO) {
            List<EreignisModel> ereignisModelList = ereignisseWriteDTO.ereigniseintraege().stream()
                    .map(ereignisDto -> new EreignisModel(ereignisDto.beschreibung(), ereignisDto.uhrzeit(),
                            MapEreignisart.ereignisartDtoToEreignisartModel(ereignisDto.ereignisart())))
                    .toList();
            return new EreignisseWriteModel(wahlbezirkID, ereignisModelList);
        }
    }

    public static class CreateEreignisDto {
        public static EreignisDTO withData() {
            return new EreignisDTO("beschreibung", LocalDateTime.now().withNano(0), EreignisartDTO.VORFALL);
        }
    }

    public static class CreateEreignisseWriteDto {
        public static EreignisseWriteDTO withData(List<EreignisDTO> ereignisDtoList) {
            return new EreignisseWriteDTO(ereignisDtoList);
        }
    }

    public static class CreateWahlbezirkEreignisseDto {
        public static WahlbezirkEreignisseDTO fromModel(WahlbezirkEreignisseModel ereignisseModel) {
            List<EreignisDTO> ereignisDtoList = ereignisseModel.ereigniseintraege().stream().map(ereignisModel -> new EreignisDTO(ereignisModel.beschreibung(),
                    ereignisModel.uhrzeit(), MapEreignisart.ereignisartModelToEreignisartDto(ereignisModel.ereignisart()))).toList();
            return new WahlbezirkEreignisseDTO(ereignisseModel.wahlbezirkID(), ereignisseModel.keineVorfaelle(), ereignisseModel.keineVorkommnisse(),
                    ereignisDtoList);
        }
    }

    public static class MapEreignisart {
        public static Ereignisart ereignisartModelToEreignisart(EreignisartModel ereignisartModel) {
            return switch (ereignisartModel) {
            case VORFALL -> Ereignisart.VORFALL;
            case VORKOMMNIS -> Ereignisart.VORKOMMNIS;
            };
        }

        public static Ereignisart ereignisartDtoToEreignisart(EreignisartDTO ereignisartDTO) {
            return switch (ereignisartDTO) {
            case VORFALL -> Ereignisart.VORFALL;
            case VORKOMMNIS -> Ereignisart.VORKOMMNIS;
            };
        }

        public static EreignisartModel ereignisartToEreignisartModel(Ereignisart ereignisart) {
            return switch (ereignisart) {
            case VORFALL -> EreignisartModel.VORFALL;
            case VORKOMMNIS -> EreignisartModel.VORKOMMNIS;
            };
        }

        public static EreignisartModel ereignisartDtoToEreignisartModel(EreignisartDTO ereignisartDTO) {
            return switch (ereignisartDTO) {
            case VORFALL -> EreignisartModel.VORFALL;
            case VORKOMMNIS -> EreignisartModel.VORKOMMNIS;
            };
        }

        public static EreignisartDTO ereignisartModelToEreignisartDto(EreignisartModel ereignisartModel) {
            return switch (ereignisartModel) {
            case VORFALL -> EreignisartDTO.VORFALL;
            case VORKOMMNIS -> EreignisartDTO.VORKOMMNIS;
            };
        }
    }
}
