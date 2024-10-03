package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltermindaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltermindaten.AsyncProgress;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions/asyncProgress")
@RequiredArgsConstructor
public class AsyncProgressController {

    private final AsyncProgress asyncProgress;
    private final AsyncProgressDTOMapper asyncProgressDTOMapper;

    @GetMapping
    public AsyncProgressDTO getAsyncProgress() {
        return asyncProgressDTOMapper.toDto(asyncProgress);
    }
}
