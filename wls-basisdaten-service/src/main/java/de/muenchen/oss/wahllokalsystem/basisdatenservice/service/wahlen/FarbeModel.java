package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahlen;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record FarbeModel(@NotNull @Min(0)@Max(255)long r,

@NotNull @Min(0)@Max(255)long g,

@NotNull @Min(0)@Max(255)long b){}
