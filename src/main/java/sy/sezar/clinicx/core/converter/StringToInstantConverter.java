package sy.sezar.clinicx.core.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import sy.sezar.clinicx.core.exception.NotValidValueException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Custom converter to handle various date/time formats for Instant parameters.
 * Supports ISO-8601 instant format and falls back to date-only format.
 */
@Slf4j
@Component
public class StringToInstantConverter implements Converter<String, Instant> {
    
    private final List<DateTimeFormatter> dateTimeFormatters;
    private final DateTimeFormatter dateFormatter;
    private final ZoneId defaultZone;
    
    public StringToInstantConverter(
            @Value("${app.date.formats.datetime:yyyy-MM-dd'T'HH:mm:ss,yyyy-MM-dd'T'HH:mm:ss'Z',yyyy-MM-dd'T'HH:mm:ssX}") List<String> dateTimeFormats,
            @Value("${app.date.formats.date:yyyy-MM-dd}") String dateFormat,
            @Value("${app.date.default-timezone:UTC}") String defaultTimezone) {
        
        this.dateTimeFormatters = dateTimeFormats.stream()
                .map(DateTimeFormatter::ofPattern)
                .toList();
        this.dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
        this.defaultZone = ZoneId.of(defaultTimezone);
        
        log.info("Initialized StringToInstantConverter with {} datetime formats, date format: {}, default timezone: {}",
                dateTimeFormats.size(), dateFormat, defaultTimezone);
    }
    
    @Override
    public Instant convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            log.debug("Received null or empty date string, returning null");
            return null;
        }
        
        String trimmedSource = source.trim();
        log.debug("Converting date string '{}' to Instant", trimmedSource);
        
        // Try to parse as Instant (ISO-8601 with timezone)
        try {
            Instant instant = Instant.parse(trimmedSource);
            log.debug("Successfully parsed '{}' as Instant: {}", trimmedSource, instant);
            return instant;
        } catch (DateTimeParseException e) {
            log.trace("Failed to parse '{}' as Instant, trying other formats", trimmedSource, e);
        }
        
        // Try custom datetime formats
        for (DateTimeFormatter formatter : dateTimeFormatters) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(trimmedSource, formatter);
                Instant instant = dateTime.atZone(defaultZone).toInstant();
                log.debug("Successfully parsed '{}' as LocalDateTime with format {}, converted to Instant: {}", 
                        trimmedSource, formatter, instant);
                return instant;
            } catch (DateTimeParseException e) {
                log.trace("Failed to parse '{}' with datetime formatter {}", trimmedSource, formatter);
            }
        }
        
        // Try to parse as date only
        try {
            LocalDate date = LocalDate.parse(trimmedSource, dateFormatter);
            Instant instant = date.atStartOfDay(defaultZone).toInstant();
            log.debug("Successfully parsed '{}' as LocalDate, converted to Instant at start of day in {}: {}", 
                    trimmedSource, defaultZone, instant);
            return instant;
        } catch (DateTimeParseException e) {
            log.trace("Failed to parse '{}' as LocalDate", trimmedSource, e);
        }
        
        // Try ISO formats as fallback
        try {
            LocalDateTime dateTime = LocalDateTime.parse(trimmedSource);
            Instant instant = dateTime.atZone(defaultZone).toInstant();
            log.debug("Successfully parsed '{}' as ISO LocalDateTime, converted to Instant: {}", trimmedSource, instant);
            return instant;
        } catch (DateTimeParseException e) {
            log.trace("Failed to parse '{}' as ISO LocalDateTime", trimmedSource, e);
        }
        
        try {
            LocalDate date = LocalDate.parse(trimmedSource);
            Instant instant = date.atStartOfDay(defaultZone).toInstant();
            log.debug("Successfully parsed '{}' as ISO LocalDate, converted to Instant: {}", trimmedSource, instant);
            return instant;
        } catch (DateTimeParseException e) {
            log.trace("Failed to parse '{}' as ISO LocalDate", trimmedSource, e);
        }
        
        log.warn("Failed to parse '{}' with any supported format", trimmedSource);
        throw new NotValidValueException(
            "dateTime",
            trimmedSource,
            "ISO 8601 format (e.g., 2024-01-15T10:00:00Z or 2024-01-15)",
            String.format("Invalid date/time format. Accepted formats: ISO-8601 instant (2024-01-15T10:00:00Z), " +
                    "datetime formats %s, or date format %s. Default timezone: %s", 
                    dateTimeFormatters, dateFormatter, defaultZone)
        );
    }
}