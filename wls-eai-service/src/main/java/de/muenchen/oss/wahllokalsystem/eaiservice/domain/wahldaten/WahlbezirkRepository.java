package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface WahlbezirkRepository extends CrudRepository<Wahlbezirk, UUID> {

    @Query("SELECT wb FROM Wahlbezirk wb JOIN FETCH wb.wahl w JOIN FETCH w.wahltag t WHERE t.tag = (:wahltag) AND w.nummer = (:nummer)")
    List<Wahlbezirk> findWahlbezirkeWithWahlAndWahltagForDateAndNummer(@Param("wahltag") LocalDate wahltag, @Param("nummer") String nummer);
}
