package com.openapi.converter.report;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Csv report generator.
 *
 * @param <T> - report data type
 * @author Roman Batygin
 */
@Slf4j
public abstract class AbstractCsvReportGenerator<T> implements ReportGenerator<T> {

    private static final char HEADER_DELIMITER = ';';

    @Override
    public void generateReport(T data, OutputStream outputStream) throws IOException {
        @Cleanup var zipOutputStream = new ZipOutputStream(outputStream);
        @Cleanup var writer = new OutputStreamWriter(zipOutputStream, StandardCharsets.UTF_8);

        zipOutputStream.putNextEntry(new ZipEntry(getSimpleReportName()));
        var resultsPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.withHeader(getSimpleReportHeaders())
                .withDelimiter(HEADER_DELIMITER));
        printSimpleReport(resultsPrinter, data);
        writer.flush();
        zipOutputStream.flush();
        zipOutputStream.closeEntry();

        zipOutputStream.putNextEntry(new ZipEntry(getTotalReportName()));
        var totalPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.withHeader(getTotalReportHeaders())
                .withDelimiter(HEADER_DELIMITER));
        printTotalReport(totalPrinter, data);
        writer.flush();
        zipOutputStream.flush();
        zipOutputStream.closeEntry();
    }

    protected abstract String[] getSimpleReportHeaders();

    protected abstract String[] getTotalReportHeaders();

    protected abstract String getTotalReportName();

    protected abstract String getSimpleReportName();

    protected abstract void printSimpleReport(CSVPrinter csvPrinter, T data) throws IOException;

    protected abstract void printTotalReport(CSVPrinter csvPrinter, T data) throws IOException;
}
