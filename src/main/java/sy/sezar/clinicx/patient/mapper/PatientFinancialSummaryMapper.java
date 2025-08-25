package sy.sezar.clinicx.patient.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sy.sezar.clinicx.patient.dto.PatientBalanceSummaryDto;
import sy.sezar.clinicx.patient.view.PatientFinancialSummaryView;

/**
 * Mapper for converting PatientFinancialSummaryView to DTOs.
 */
@Mapper(componentModel = "spring")
public interface PatientFinancialSummaryMapper {

    /**
     * Maps PatientFinancialSummaryView to PatientBalanceSummaryDto.
     */
    @Mapping(target = "totalBalance", source = "balance")
    @Mapping(target = "balanceStatus", expression = "java(determineBalanceStatus(view.getBalance()))")
    @Mapping(target = "balanceDescription", expression = "java(createBalanceDescription(view.getBalance(), view.getTotalUnpaid()))")
    PatientBalanceSummaryDto toPatientBalanceSummaryDto(PatientFinancialSummaryView view);

    /**
     * Determines the balance status based on the balance amount.
     */
    default String determineBalanceStatus(java.math.BigDecimal balance) {
        if (balance == null) {
            return "UNKNOWN";
        } else if (balance.compareTo(java.math.BigDecimal.ZERO) == 0) {
            return "PAID_UP";
        } else if (balance.compareTo(java.math.BigDecimal.ZERO) > 0) {
            return "OUTSTANDING";
        } else {
            return "CREDIT";
        }
    }

    /**
     * Creates a human-readable balance description.
     */
    default String createBalanceDescription(java.math.BigDecimal balance,
                                          java.math.BigDecimal totalUnpaid) {
        if (balance == null || balance.compareTo(java.math.BigDecimal.ZERO) == 0) {
            return "Account is up to date";
        } else if (balance.compareTo(java.math.BigDecimal.ZERO) > 0) {
            return String.format("Outstanding balance of $%.2f", balance);
        } else {
            return String.format("Account has credit of $%.2f", balance.abs());
        }
    }
}
