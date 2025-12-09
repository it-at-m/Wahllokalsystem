package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record FarbeDTO(@NotNull @Min(0)@Max(255)long r,

@NotNull @Min(0)@Max(255)long g,

@NotNull @Min(0)@Max(255)long b){}
