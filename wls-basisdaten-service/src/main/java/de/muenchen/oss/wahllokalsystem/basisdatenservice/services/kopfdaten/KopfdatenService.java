package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.Kopfdaten;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.KopfdatenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.kopfdaten.KopfdatenDTOMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KopfdatenService {

    private final KopfdatenRepository kopfdatenRepository;
    private final KopfdatenDTOMapper kopfdatenDTOMapper;
    private final KopfdatenModelMapper kopfdatenModelMapper;

    public KopfdatenModel getKopfdaten(BezirkUndWahlID bezirkUndWahlID) {
        log.info("getKopfdaten");

        // TODO: Implementierung

        Optional<Kopfdaten> kopfdatenFromRepo = kopfdatenRepository.findById(bezirkUndWahlID);
        return kopfdatenModelMapper.toModel(kopfdatenFromRepo.get());
    }
}
