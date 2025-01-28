package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AusdruckRepository extends CrudRepository<Ausdruck, WahlUndBezirkIDUndMeldungsart> {

    @Query("SELECT a FROM Ausdruck a WHERE a.wahlUndBezirkIDUndMeldungsart.wahlID = :wahlID AND a.wahlUndBezirkIDUndMeldungsart.wahlbezirkID = :wahlbezirkID")
    List<Ausdruck> findByWahlIdAndWahlbezirkId(@Param("wahlID") String wahlID, @Param("wahlbezirkID") String wahlbezirkID);
}
