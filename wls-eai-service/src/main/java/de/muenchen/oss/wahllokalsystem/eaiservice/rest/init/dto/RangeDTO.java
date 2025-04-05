package de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public record RangeDTO(
        @NotNull int min,
        @NotNull int max
) {
    public int getValueInRange() {
        val delta = max - min;
        val result = ((int) (Math.random() * (delta + 1))) + min;
        log.atDebug().log("getCountInRange: range {}, result: {}", this, result);
        return result;
    }
}
