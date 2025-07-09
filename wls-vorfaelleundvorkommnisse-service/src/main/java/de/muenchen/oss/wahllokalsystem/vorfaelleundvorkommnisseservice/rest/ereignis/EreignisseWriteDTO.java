package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.rest.ereignis;

import java.util.List;
import lombok.Builder;

@Builder public record EreignisseWriteDTO(boolean keineVorfaelle,boolean keineVorkommnisse,List<EreignisDTO>ereigniseintraege){}
