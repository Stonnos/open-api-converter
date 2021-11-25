package com.openapi.converter.report;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Report generator.
 *
 * @param <T> - report data type
 * @author Roman Batygin
 */
public interface ReportGenerator<T> {


    /**
     * Generates report for data.
     *
     * @param data         - report data
     * @param outputStream - output stream
     * @throws IOException in case of I/O error
     */
    void generateReport(T data, OutputStream outputStream) throws IOException;
}
