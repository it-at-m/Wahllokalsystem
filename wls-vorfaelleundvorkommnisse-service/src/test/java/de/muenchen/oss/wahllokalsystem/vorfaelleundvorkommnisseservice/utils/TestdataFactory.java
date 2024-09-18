package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.utils;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignis;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.Ereignisart;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis.EreignisseWriteDTO;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis.WahlbezirkEreignisseDTO;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis.EreignisDTO;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.WahlbezirkEreignisseModel;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.EreignisseWriteModel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestdataFactory {

    public class CreateEreignisEntity {

        public static Ereignis withData(String wahlbezirkID) {
            return new Ereignis(wahlbezirkID, "beschreibung", LocalDateTime.now().withNano(0), Ereignisart.VORFALL);
        }

        public static Ereignis fromModel(EreignisModel ereignisModel, String wahlbezirkID) {
            return new Ereignis(wahlbezirkID, ereignisModel.beschreibung(), ereignisModel.uhrzeit(), ereignisModel.ereignisart());
        }

        public static List<Ereignis> listFromModel(EreignisseWriteModel ereignisWriteModel) {
            List<Ereignis> ereignisList = new ArrayList<>();
            for (EreignisModel ereignisModel : ereignisWriteModel.ereigniseintraege()) {
                Ereignis ereignis = new Ereignis(ereignisWriteModel.wahlbezirkID(), ereignisModel.beschreibung(), ereignisModel.uhrzeit(),
                        ereignisModel.ereignisart());
                ereignisList.add(ereignis);
            }
            return ereignisList;
        }
    }

    public class CreateEreignisModel {

        public static EreignisModel withData() {
            return new EreignisModel("beschreibung", LocalDateTime.now().withNano(0), Ereignisart.VORFALL);
        }

        public static EreignisModel withEreignisart(Ereignisart ereignisart) {
            return new EreignisModel("beschreibung", LocalDateTime.now().withNano(0), ereignisart);
        }

        public static EreignisModel fromEntity(Ereignis ereignisEntity) {
            return new EreignisModel(ereignisEntity.getBeschreibung(), ereignisEntity.getUhrzeit(), ereignisEntity.getEreignisart());
        }
    }

    public class CreateEreignisseModel {

        public static WahlbezirkEreignisseModel withData(String wahlbezirkID, boolean keineVorfaelle, boolean keineVorkommnisse,
                List<EreignisModel> EreignisModelList) {
            return new WahlbezirkEreignisseModel(wahlbezirkID, keineVorfaelle, keineVorkommnisse, EreignisModelList);
        }
    }

    public class CreateEreignisseWriteModel {

        public static EreignisseWriteModel withData(String wahlbezirkID, List<EreignisModel> EreignisModelList) {
            return new EreignisseWriteModel(wahlbezirkID, EreignisModelList);
        }

        public static EreignisseWriteModel fromDto(String wahlbezirkID, EreignisseWriteDTO ereignisseWriteDTO) {
            List<EreignisModel> ereignisModelList = new ArrayList<>();
            for (EreignisDTO ereignisDto : ereignisseWriteDTO.ereigniseintraege()) {
                EreignisModel dto = new EreignisModel(ereignisDto.beschreibung(), ereignisDto.uhrzeit(), ereignisDto.ereignisart());
                ereignisModelList.add(dto);
            }
            return new EreignisseWriteModel(wahlbezirkID, ereignisModelList);
        }
    }

    public class CreateEreignisDto {
        public static EreignisDTO withData() {
            return new EreignisDTO("beschreibung", LocalDateTime.now().withNano(0), Ereignisart.VORFALL);
        }
    }

    public class CreateEreignisseWriteDto {
        public static EreignisseWriteDTO withData(List<EreignisDTO> ereignisDtoList) {
            return new EreignisseWriteDTO(ereignisDtoList);
        }
    }

    public class CreateWahlbezirkEreignisseDto {
        public static WahlbezirkEreignisseDTO fromModel(WahlbezirkEreignisseModel ereignisseModel) {
            List<EreignisDTO> ereignisDtoList = new ArrayList<>();
            for (EreignisModel ereignisModel : ereignisseModel.ereigniseintraege()) {
                EreignisDTO dto = new EreignisDTO(ereignisModel.beschreibung(), ereignisModel.uhrzeit(), ereignisModel.ereignisart());
                ereignisDtoList.add(dto);
            }
            return new WahlbezirkEreignisseDTO(ereignisseModel.wahlbezirkID(), ereignisseModel.keineVorfaelle(), ereignisseModel.keineVorkommnisse(),
                    ereignisDtoList);
        }
    }
}
