package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.awerte;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.WahlberechtigteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteModel;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AWerteClientMapper {

    @Mapping(target = "bezirkUndWahlID.wahlID", source = "wahlID")
    @Mapping(target = "bezirkUndWahlID.wahlbezirkID", source = "wahlbezirkID")
    AWerteModel fromRemoteClientWahlberechtigteDtoToAWerteModel(WahlberechtigteDTO wahlberechtigteDTO);

    List<AWerteModel> fromRemoteClientListOfWahlberechtigteDtoToListOfAWerteModel(List<WahlberechtigteDTO> wahlberechtigteDTO);
}
