package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface WahlbezirkRepository extends CrudRepository<Wahlbezirk, UUID> {

    @Query(
        "SELECT wb FROM Wahlbezirk wb JOIN FETCH wb.stimmzettelgebiet sg JOIN FETCH sg.wahl w JOIN FETCH w.wahltag t WHERE t.tag = (:wahltag) AND t.nummer = (:nummer)"
    )
    List<Wahlbezirk> findWahlbezirkeWithStimmzettelgebietAndWahlAndWahltagByWahltagAndNummer(@Param("wahltag") LocalDate wahltag,
            @Param("nummer") String nummer);

    @Query("SELECT wb FROM Wahlbezirk wb JOIN FETCH wb.stimmzettelgebiet sg JOIN FETCH sg.wahl w JOIN FETCH w.wahltag t WHERE wb.id = (:wahlbezirkID)")
    List<Wahlbezirk> findWahlbezirkeWithStimmzettelgebietAndWahlAndWahltagByID(@Param("wahlbezirkID") UUID wahlbezirkID);
}
