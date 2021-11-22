package com.openapi.converter.report;

import com.openapi.converter.dto.openapi.Info;
import com.openapi.converter.model.validation.Severity;
import com.openapi.converter.model.validation.ValidationResult;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.openapi.converter.util.Utils.countBySeverity;

@Component
public class OpenApiValidationResultsCsvReportGenerator extends AbstractCsvReportGenerator<List<ValidationResult>> {

    private static final String[] SIMPLE_REPORT_HEADERS = {
            "Rule code",
            "Severity level",
            "Endpoint path",
            "Schema reference",
            "Parameter or property",
            "Response code",
            "Message",
    };

    private static final String[] TOTAL_REPORT_HEADERS = {
            "Total",
            "Critical",
            "Major",
            "Minor",
            "Info"
    };

    private static final String OPEN_API_VALIDATION_RESULTS_TOTAL_CSV = "open-api-validation-results-total.csv";
    private static final String OPEN_API_VALIDATION_RESULTS_LOG_CSV = "open-api-validation-results-log.csv";

    @Override
    protected String[] getSimpleReportHeaders() {
        return SIMPLE_REPORT_HEADERS;
    }

    @Override
    protected String[] getTotalReportHeaders() {
        return TOTAL_REPORT_HEADERS;
    }

    @Override
    protected String getTotalReportName() {
        return OPEN_API_VALIDATION_RESULTS_TOTAL_CSV;
    }

    @Override
    protected String getSimpleReportName() {
        return OPEN_API_VALIDATION_RESULTS_LOG_CSV;
    }

    @Override
    protected void printSimpleReport(CSVPrinter csvPrinter, List<ValidationResult> validationResults)
            throws IOException {
        for (var validationResult : validationResults) {
            csvPrinter.printRecord(
                    validationResult.getRule(),
                    validationResult.getSeverity(),
                    validationResult.getPath(),
                    validationResult.getSchemaRef(),
                    validationResult.getParameterOrProperty(),
                    validationResult.getResponseCode(),
                    validationResult.getMessage()
            );
        }
    }

    @Override
    protected void printTotalReport(CSVPrinter csvPrinter, List<ValidationResult> validationResults)
            throws IOException {
        csvPrinter.printRecord(
                validationResults.size(),
                countBySeverity(validationResults, Severity.CRITICAL),
                countBySeverity(validationResults, Severity.MAJOR),
                countBySeverity(validationResults, Severity.MINOR),
                countBySeverity(validationResults, Severity.INFO)
        );
    }
}
