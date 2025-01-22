package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface AusdruckRepository extends CrudRepository<Ausdruck, WahlUndBezirkIDUndMeldungsart> {

    List<Ausdruck> findAllByWahlUndBezirkIDUndMeldungsart_WahlIDAndWahlUndBezirkIDUndMeldungsart_WahlbezirkID(String wahlId, String wahlbezirkId);
}
