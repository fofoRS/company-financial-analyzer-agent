package org.fofo.stock.agent.scrapper.connector.writer;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component("localDiskReportWriter")
public class LocalDiskStatementReportWriterConnector implements StatementReportWriterConnector {

    private static final Path REPORTS_DIRECTORY_PATH;

    static {

        Path rootPath = FileSystems.getDefault().getPath("").toAbsolutePath();

        REPORTS_DIRECTORY_PATH = Path.of(rootPath.toString(),"statement_reports");

        if (Files.notExists(REPORTS_DIRECTORY_PATH)) {
            try {
                Files.createDirectory(REPORTS_DIRECTORY_PATH);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public StatementReportWriterResult writeReportToDestination(StatementReportWriterRequest request) {

        String cik = request.getCompanyCik();

        Path cikRootPath = buildCikDirectoryIfNotExits(cik);

        String reportFileName = buildFileName(request.getCompanyCik(), request.getAccessionNumber(),
                request.getReportName());
        try {
            Path filePath = createFile(cikRootPath, reportFileName);
            Files.write(filePath, request.getData());
            return new StatementReportWriterResult(filePath.toString(),reportFileName,"LOCAL");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path createFile(Path parentFolder, String reportFileName) throws IOException {
        Path filePath = parentFolder.resolve(reportFileName);
        return Files.createFile(filePath);
    }

    private Path  buildCikDirectoryIfNotExits(String cik) {
        Path cikFolderPath = Path.of(REPORTS_DIRECTORY_PATH.toString(),cik);
        if (Files.notExists(cikFolderPath)) {
            try {
                Files.createDirectory(cikFolderPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return cikFolderPath;
    }
}
