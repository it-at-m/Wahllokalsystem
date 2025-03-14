package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlschliessungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.AbstractController;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlschliessungsuhrzeit.UrnenwahlSchliessungsUhrzeitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/urnenwahlSchliessungsUhrzeit")
@RequiredArgsConstructor
public class UrnenwahlSchliessungsUhrzeitController extends AbstractController {

    private final UrnenwahlSchliessungsUhrzeitService urnenwahlSchliessungsUhrzeitService;
    private final UrnenwahlSchliessungsUhrzeitDTOMapper urnenwahlSchliessungsUhrzeitDTOMapper;

    @Operation(
            description = "Laden der Urnenwahlschliessungsuhrzeit des Urnenwahllokals {wahlbezirkID}",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Urnenwahlschliessungsuhrzeit erfolgreich zurückgegeben."
                    ) }
    )
    @GetMapping("{wahlbezirkID}")
    public ResponseEntity<UrnenwahlSchliessungsUhrzeitDTO> getUrnenwahlSchliessungsUhrzeit(@PathVariable("wahlbezirkID") final String wahlbezirkID) {
        val urnenwahlSchliessungsUhrzeitModel = urnenwahlSchliessungsUhrzeitService.getUrnenwahlSchliessungsUhrzeit(wahlbezirkID);

        return okWithBodyOrNoContent(urnenwahlSchliessungsUhrzeitModel.map(urnenwahlSchliessungsUhrzeitDTOMapper::toDTO));
    }

    @Operation(
            description = "Aktualisiert die Urnenwahlschliessungsuhrzeit des Urnenwahllokals {wahlbezirkID}",
            responses = {
                    @ApiResponse(
                            responseCode = "201", description = "Urnenwahlschliessungsuhrzeit erfolgreich gespeichert."
                    ) }
    )
    @PostMapping("{wahlbezirkID}")
    @ResponseStatus(HttpStatus.CREATED)
    public void postUrnenwahlSchliessungsUhrzeit(@PathVariable("wahlbezirkID") final String wahlbezirkID,
            @RequestBody final UrnenwahlSchliessungsUhrzeitWriteDTO urnenwahlSchliessungsUhrzeitWriteDTO) {
        val urnenwahlSchliessungsUhrzeitToSet = urnenwahlSchliessungsUhrzeitDTOMapper.toModel(wahlbezirkID, urnenwahlSchliessungsUhrzeitWriteDTO);
        urnenwahlSchliessungsUhrzeitService.setUrnenwahlSchliessungsUhrzeit(urnenwahlSchliessungsUhrzeitToSet);
    }

}
