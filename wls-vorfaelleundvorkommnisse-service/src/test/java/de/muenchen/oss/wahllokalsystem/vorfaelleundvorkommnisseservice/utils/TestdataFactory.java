package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignis;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.EreignisartEntity;
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
            return new Ereignis(wahlbezirkID, "beschreibung", LocalDateTime.now().withNano(0), EreignisartEntity.VORFALL);
        }

        public static Ereignis fromModel(EreignisModel ereignisModel, String wahlbezirkID) {
            return new Ereignis(wahlbezirkID, ereignisModel.beschreibung(), ereignisModel.uhrzeit(), EreignisartEntity.fromModel(ereignisModel.ereignisart()));
        }

        public static List<Ereignis> listFromModel(EreignisseWriteModel ereignisWriteModel) {
            List<Ereignis> ereignisList = ereignisWriteModel.ereigniseintraege().stream().map(ereignis -> new Ereignis(ereignisWriteModel.wahlbezirkID(),
                    ereignis.beschreibung(), ereignis.uhrzeit(), EreignisartEntity.fromModel(ereignis.ereignisart()))).toList();
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
                    EreignisartModel.fromEntity(ereignisEntity.getEreignisart()));
        }
    }

    public static class CreateEreignisseModel {

        public static WahlbezirkEreignisseModel withData(String wahlbezirkID, boolean keineVorfaelle, boolean keineVorkommnisse,
                List<EreignisModel> EreignisModelList) {
            return new WahlbezirkEreignisseModel(wahlbezirkID, keineVorfaelle, keineVorkommnisse, EreignisModelList);
        }
    }

    public static class CreateEreignisseWriteModel {

        public static EreignisseWriteModel withData(String wahlbezirkID, List<EreignisModel> EreignisModelList) {
            return new EreignisseWriteModel(wahlbezirkID, EreignisModelList);
        }

        public static EreignisseWriteModel fromDto(String wahlbezirkID, EreignisseWriteDTO ereignisseWriteDTO) {
            List<EreignisModel> ereignisModelList = ereignisseWriteDTO.ereigniseintraege().stream().map(
                    ereignisDto -> new EreignisModel(ereignisDto.beschreibung(), ereignisDto.uhrzeit(), EreignisartModel.fromDto(ereignisDto.ereignisart())))
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
                    ereignisModel.uhrzeit(), EreignisartDTO.fromModel(ereignisModel.ereignisart()))).toList();
            return new WahlbezirkEreignisseDTO(ereignisseModel.wahlbezirkID(), ereignisseModel.keineVorfaelle(), ereignisseModel.keineVorkommnisse(),
                    ereignisDtoList);
        }
    }
}
