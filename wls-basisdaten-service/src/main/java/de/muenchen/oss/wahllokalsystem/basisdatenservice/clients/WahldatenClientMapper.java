package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.BasisdatenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.BasisstrukturdatenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlbezirkDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.BasisdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.BasisstrukturdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WahldatenClientMapper {

    BasisdatenModel fromRemoteClientDTOToModel(BasisdatenDTO basisdatenDTO);

//    @Mapping(source="identifikator", target="wahlbezirkID")
//    BasisstrukturdatenModel fromRemotedtotoModel(BasisstrukturdatenDTO basisstrukturdatenDTO);

    default WahlModel wahlDTOToWahlModel(WahlDTO wahlDTO) {
        return Mappers.getMapper(WahlenClientMapper.class).toModel(wahlDTO);
    }
    default WahlbezirkModel walbezirkDTOToWahlbezirkModel(WahlbezirkDTO wahlbezirkDTO) { return Mappers.getMapper(WahlbezirkeClientMapper.class).fromClientDTOToModel(wahlbezirkDTO); }
}
