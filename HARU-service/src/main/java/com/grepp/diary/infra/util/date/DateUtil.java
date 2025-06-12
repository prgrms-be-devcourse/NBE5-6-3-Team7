package com.grepp.diary.infra.util.date;

import com.grepp.diary.infra.util.date.code.DatePeriod;
import com.grepp.diary.infra.util.date.dto.DateRangeDto;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class DateUtil {
    public DateRangeDto toDateRangeDto(DatePeriod period, LocalDate date) {
        LocalDate now = (date != null)?date:LocalDate.now();
        LocalDate start, end;

        if(period == DatePeriod.MONTH) {
            start = now.withDayOfMonth(1);
            end = now.withDayOfMonth(now.lengthOfMonth());
        } else if(period == DatePeriod.YEAR) {
            start = now.withDayOfYear(1);
            end = now.withDayOfYear(now.lengthOfYear());
        } else {
            throw new IllegalArgumentException("Invalid period value: " + period);
        }

        return new DateRangeDto(start, end);
    }
}
