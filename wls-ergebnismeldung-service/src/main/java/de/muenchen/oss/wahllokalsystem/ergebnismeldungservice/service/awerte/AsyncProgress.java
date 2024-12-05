package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsyncProgress {

    private LocalDateTime lastStartTime;
    private LocalDateTime lastFinishTime = LocalDateTime.now();
    private boolean aWerteLoadingActive = false;
    private int aWerteTotal = 0;
    private int aWerteFinished = 0;
    private String aWerteNext = "";

    public synchronized void incAWerteFinished() {
        aWerteFinished++;
        if (aWerteFinished >= getAWerteTotal()) {
            this.setAWerteLoadingActive(false);
            this.setLastFinishTime(LocalDateTime.now());
        }
    }

    public void reset(int size) {
        lastStartTime = LocalDateTime.now();
        lastFinishTime = null;
        aWerteFinished = 0;
        aWerteTotal = size;
        aWerteLoadingActive = true;
    }
}
