package com.example.edvhelper.service.idcheck;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class FiscalDocumentService {

    private static final String BASE_URL = "https://monitoring.e-kassa.gov.az/pks-monitoring/2.0.0/documents/";
    private static final Path DIRECTORY_PATH = Paths.get("/cheque");

    public File fetchAndSaveDocument(WebClient webClient, String fiscalId) {
        log.trace("Fetching and saving document with fiscalId: {}", fiscalId);

        String url = BASE_URL + fiscalId;
        log.debug("Formed URL for request: {}", url);

        byte[] imageBytes = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();

        if (imageBytes == null) {
            log.warn("No data fetched for fiscalId: {}", fiscalId);
            return null;
        }

        log.debug("Successfully fetched data for fiscalId: {}. Proceeding to save.", fiscalId);
        return saveImage(fiscalId, imageBytes);
    }

    private File saveImage(String fiscalId, byte[] imageBytes) {
        try {
            Files.createDirectories(DIRECTORY_PATH);

            Path filePath = DIRECTORY_PATH.resolve(fiscalId + ".jpeg");
            log.debug("Writing file for fiscalId: {}", fiscalId);
            Files.write(filePath, imageBytes);
            log.info("Successfully saved image for fiscalId: {}", fiscalId);
            return filePath.toFile();
        } catch (Exception e) {
            log.error("Failed to save image for fiscalId: {}", fiscalId, e);
            return null;
        }
    }
}
