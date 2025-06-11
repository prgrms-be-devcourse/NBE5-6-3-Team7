package com.grepp.diary.infra.util.date.dto;

import java.time.LocalDate;

public record DateRangeDto(
    LocalDate start, LocalDate end
) { }
