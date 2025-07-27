package sy.sezar.clinicx.core.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import sy.sezar.clinicx.core.exception.NotValidValueException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

class StringToInstantConverterTest {
    
    private StringToInstantConverter converter;
    
    @BeforeEach
    void setUp() {
        // Initialize with default configuration
        converter = new StringToInstantConverter(
            Arrays.asList("yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ssX"),
            "yyyy-MM-dd",
            "UTC"
        );
    }
    
    @Test
    @DisplayName("Should convert ISO-8601 instant format with Z timezone")
    void convertInstantWithZ() {
        String input = "2024-01-15T10:30:00Z";
        Instant result = converter.convert(input);
        
        assertThat(result).isNotNull();
        assertThat(result.toString()).isEqualTo("2024-01-15T10:30:00Z");
    }
    
    @Test
    @DisplayName("Should convert ISO-8601 instant format with +00:00 timezone")
    void convertInstantWithOffset() {
        String input = "2024-01-15T10:30:00+00:00";
        Instant result = converter.convert(input);
        
        assertThat(result).isNotNull();
        assertThat(result.toString()).isEqualTo("2024-01-15T10:30:00Z");
    }
    
    @Test
    @DisplayName("Should convert ISO-8601 datetime without timezone")
    void convertDateTimeWithoutTimezone() {
        String input = "2024-01-15T10:30:00";
        Instant result = converter.convert(input);
        
        assertThat(result).isNotNull();
        assertThat(result.toString()).isEqualTo("2024-01-15T10:30:00Z");
    }
    
    @Test
    @DisplayName("Should convert date-only format")
    void convertDateOnly() {
        String input = "2024-01-15";
        Instant result = converter.convert(input);
        
        assertThat(result).isNotNull();
        assertThat(result.toString()).isEqualTo("2024-01-15T00:00:00Z");
    }
    
    @Test
    @DisplayName("Should convert space-separated datetime format")
    void convertSpaceSeparatedDateTime() {
        converter = new StringToInstantConverter(
            Arrays.asList("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss"),
            "yyyy-MM-dd",
            "UTC"
        );
        
        String input = "2024-01-15 10:30:00";
        Instant result = converter.convert(input);
        
        assertThat(result).isNotNull();
        assertThat(result.toString()).isEqualTo("2024-01-15T10:30:00Z");
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Should return null for null, empty, or whitespace input")
    void convertNullOrEmpty(String input) {
        Instant result = converter.convert(input);
        assertThat(result).isNull();
    }
    
    @Test
    @DisplayName("Should respect configured timezone for date-only conversion")
    void convertDateWithDifferentTimezone() {
        converter = new StringToInstantConverter(
            Arrays.asList("yyyy-MM-dd'T'HH:mm:ss"),
            "yyyy-MM-dd",
            "America/New_York"
        );
        
        String input = "2024-01-15";
        Instant result = converter.convert(input);
        
        // Should be 5 hours ahead of UTC (EST)
        ZonedDateTime expectedTime = ZonedDateTime.of(2024, 1, 15, 0, 0, 0, 0, ZoneId.of("America/New_York"));
        assertThat(result).isEqualTo(expectedTime.toInstant());
    }
    
    @Test
    @DisplayName("Should throw NotValidValueException for invalid format")
    void convertInvalidFormat() {
        String input = "invalid-date-format";
        
        assertThatThrownBy(() -> converter.convert(input))
            .isInstanceOf(NotValidValueException.class)
            .hasMessageContaining("Invalid date/time format")
            .satisfies(ex -> {
                NotValidValueException nvve = (NotValidValueException) ex;
                assertThat(nvve.getFieldName()).isEqualTo("dateTime");
                assertThat(nvve.getInvalidValue()).isEqualTo(input);
                assertThat(nvve.getExpectedFormat()).contains("ISO 8601 format");
            });
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "2024-13-01",          // Invalid month
        "2024-01-32",          // Invalid day
        "2024-01-15T25:00:00", // Invalid hour
        "2024/01/15",          // Wrong separator
        "15-01-2024",          // Wrong order
        "Jan 15, 2024"         // Wrong format
    })
    @DisplayName("Should throw NotValidValueException for various invalid dates")
    void convertVariousInvalidFormats(String input) {
        assertThatThrownBy(() -> converter.convert(input))
            .isInstanceOf(NotValidValueException.class);
    }
    
    @Test
    @DisplayName("Should handle leading and trailing whitespace")
    void convertWithWhitespace() {
        String input = "  2024-01-15T10:30:00Z  ";
        Instant result = converter.convert(input);
        
        assertThat(result).isNotNull();
        assertThat(result.toString()).isEqualTo("2024-01-15T10:30:00Z");
    }
    
    @Test
    @DisplayName("Should convert with different timezone offset")
    void convertWithTimezoneOffset() {
        String input = "2024-01-15T10:30:00+05:00";
        Instant result = converter.convert(input);
        
        assertThat(result).isNotNull();
        // Should be 5 hours earlier in UTC
        assertThat(result.toString()).isEqualTo("2024-01-15T05:30:00Z");
    }
}