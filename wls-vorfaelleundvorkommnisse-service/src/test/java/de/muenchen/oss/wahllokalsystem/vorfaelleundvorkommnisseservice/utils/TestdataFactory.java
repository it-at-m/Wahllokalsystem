package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignis;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis.EreignisseWriteDTO;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis.WahlbezirkEreignisseDTO;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis.EreignisDTO;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisseModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisseWriteModel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestdataFactory {

    // Entities
    public static Ereignis createEreignisEntityWithNoData() {
        return new Ereignis(null, null, null, null);
    }

    public static Ereignis createEreignisEntityWithData(String wahlbezirkID, String beschreibung, LocalDateTime uhrzeit, Ereignisart ereignisart) {
        return new Ereignis(wahlbezirkID, beschreibung, uhrzeit, ereignisart);
    }

    public static Ereignis createEreignisEntityFromModel(EreignisModel ereignisModel, String wahlbezirkID) {
        return new Ereignis(wahlbezirkID, ereignisModel.beschreibung(), ereignisModel.uhrzeit(), ereignisModel.ereignisart());
    }

    public static List<Ereignis> createEreignisEntityListFromModel(EreignisseWriteModel ereignisWriteModel) {
        List<Ereignis> ereignisList = new ArrayList<>();
        for (EreignisModel ereignisModel : ereignisWriteModel.ereigniseintraege()) {
            Ereignis ereignis = new Ereignis(ereignisWriteModel.wahlbezirkID(), ereignisModel.beschreibung(), ereignisModel.uhrzeit(),
                    ereignisModel.ereignisart());
            ereignisList.add(ereignis);
        }
        return ereignisList;
    }

    // Models
    public static EreignisModel createEreignisModelWithNoData() {
        return new EreignisModel(null, null, null);
    }

    public static EreignisModel createEreignisModelWithData(String beschreibung, LocalDateTime uhrzeit, Ereignisart ereignisart) {
        return new EreignisModel(beschreibung, uhrzeit, ereignisart);
    }

    public static EreignisseModel createEreignisseModelWithNoData() {
        return new EreignisseModel(null, true, true, null);
    }

    public static EreignisseModel createEreignisseModelWithData(String wahlbezirkID, boolean keineVorfaelle, boolean keineVorkommnisse,
            List<EreignisModel> EreignisModelList) {
        return new EreignisseModel(wahlbezirkID, keineVorfaelle, keineVorkommnisse, EreignisModelList);
    }

    public static EreignisModel createEreignisModelFromEntity(Ereignis ereignisEntity) {
        return new EreignisModel(ereignisEntity.getBeschreibung(), ereignisEntity.getUhrzeit(), ereignisEntity.getEreignisart());
    }

    public static EreignisseWriteModel createEreignisseWriteModelWithData(String wahlbezirkID, List<EreignisModel> EreignisModelList) {
        return new EreignisseWriteModel(wahlbezirkID, EreignisModelList);
    }

    public static EreignisseWriteModel createEreignisseWriteModelFromDto(String wahlbezirkID, EreignisseWriteDTO ereignisseWriteDTO) {
        List<EreignisModel> ereignisModelList = new ArrayList<>();
        for (EreignisDTO ereignisDto : ereignisseWriteDTO.ereigniseintraege()) {
            EreignisModel dto = new EreignisModel(ereignisDto.beschreibung(), ereignisDto.uhrzeit(), ereignisDto.ereignisart());
            ereignisModelList.add(dto);
        }
        return new EreignisseWriteModel(wahlbezirkID, ereignisModelList);
    }

    // DTOs
    public static WahlbezirkEreignisseDTO createWahlbezirkEreignisseDTOWithNoData() {
        return new WahlbezirkEreignisseDTO(null, true, true, null);
    }

    public static WahlbezirkEreignisseDTO createWahlbezirkEreignisseDTOWithData(String wahlbezirkID, boolean keineVorfaelle, boolean keineVorkommnisse,
            List<EreignisDTO> ereignisDtoList) {
        return new WahlbezirkEreignisseDTO(wahlbezirkID, keineVorfaelle, keineVorkommnisse, ereignisDtoList);
    }

    public static WahlbezirkEreignisseDTO createWahlbezirkEreignisseDTOFromModel(EreignisseModel ereignisseModel) {
        List<EreignisDTO> ereignisDtoList = new ArrayList<>();
        for (EreignisModel ereignisModel : ereignisseModel.ereigniseintraege()) {
            EreignisDTO dto = new EreignisDTO(ereignisModel.beschreibung(), ereignisModel.uhrzeit(), ereignisModel.ereignisart());
            ereignisDtoList.add(dto);
        }
        return new WahlbezirkEreignisseDTO(ereignisseModel.wahlbezirkID(), ereignisseModel.keineVorfaelle(), ereignisseModel.keineVorkommnisse(),
                ereignisDtoList);
    }

    public static EreignisseWriteDTO createEreignisseWriteDTOWithNoData() {
        return new EreignisseWriteDTO(null);
    }

    public static EreignisseWriteDTO createEreignisseWriteDTOWithData(List<EreignisDTO> ereignisDtoList) {
        return new EreignisseWriteDTO(ereignisDtoList);
    }

    public static EreignisDTO createEreignisDtoWithNoData() {
        return new EreignisDTO(null, null, null);
    }

    public static EreignisDTO createEreignisDtoWithData(String beschreibung, LocalDateTime uhrzeit, Ereignisart ereignisart) {
        return new EreignisDTO(beschreibung, uhrzeit, ereignisart);
    }
}
