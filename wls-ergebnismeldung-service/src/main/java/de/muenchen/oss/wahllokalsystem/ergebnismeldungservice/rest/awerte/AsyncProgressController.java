package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.awerte;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AsyncProgress;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/asyncProgress")
@RequiredArgsConstructor
public class AsyncProgressController {

    private final AsyncProgressDTOMapper asyncProgressDTOMapper;
    private final AsyncProgress asyncProgress;

    @GetMapping
    public AsyncProgressDTO getAsyncProgress() {
        return asyncProgressDTOMapper.toDTO(asyncProgress);
    }
}
