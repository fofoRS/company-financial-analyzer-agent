package org.fofo.stock.agent.scrapper.connector.edgar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class HttpClientEdgarPlatformConnector implements EdgarPlatformConnector {

    private static final Logger log = LoggerFactory.getLogger(HttpClientEdgarPlatformConnector.class);
    private static final String USER_AGENT = "ffrojas.19@gmail.com";

    private static final int maxRequestRate = 10;
    private LocalDateTime timeWindow;
    private Duration windowDuration = Duration.ofSeconds(1);
    private int operationsCounter;


    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public HttpClientEdgarPlatformConnector(ObjectMapper objectMapper) {
        restClient = RestClient.create();
        this.objectMapper = objectMapper;
        this.timeWindow = LocalDateTime.now().plus(windowDuration);
        this.operationsCounter = 0;
        this.windowDuration = Duration.ofSeconds(1L);
    }

    @Override
    public List<EdgarCompanyTicker> getEdgarCompanyInformation() throws EdgarDataFetcherException {
        throttlingRequestRate();
        ResponseEntity<String> responseEntity = restClient.get()
                .uri(String.format("%s/%s","https://www.sec.gov","/files/company_tickers_exchange.json"))
                .accept(MediaType.APPLICATION_JSON)
                .header("user-agent", USER_AGENT)
                .retrieve().toEntity(String.class);
        registerOperation();
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            try {
                return EdgarPlatformResponsesConverter.convertEdgarCompanyTicker(responseEntity.getBody(), objectMapper);
            } catch (JsonProcessingException e) {
                throw new EdgarDataFetcherException("Error parsing response body", e);
            }
        } else {
            throw new EdgarDataFetcherException(STR."Error Fetching Edgar Company Information with code: \{responseEntity.getStatusCode()}");
        }
    }

    @Override
    public Optional<EdgarCompanySubmissions> getEdgarCompanySubmissions(String cik) throws EdgarDataFetcherException {
        throttlingRequestRate();
        String normalizedCik = normalizeCikNumber(cik);

        ResponseEntity<String> responseEntity = restClient.get()
                .uri(STR."https://data.sec.gov/submissions/CIK\{normalizedCik}.json")
                .accept(MediaType.APPLICATION_JSON)
                .header("user-agent", USER_AGENT)
                .retrieve().toEntity(String.class);

        registerOperation();
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            try {
                return EdgarPlatformResponsesConverter.convertCompanySubmissionsRepose(responseEntity.getBody(),objectMapper);
            } catch (JsonProcessingException e) {
                log.error("Error parsing response body", e);
                throw new EdgarDataFetcherException("Error parsing response body", e);
            }
        } else {
            log.error("Error fetching Edgar Company Submissions with code: {}", responseEntity.getStatusCode());
            throw new EdgarDataFetcherException(
                    STR."Error Fetching Edgar Company Submissions with code: \{responseEntity.getStatusCode()}");
        }
    }

    @Override
    public byte[] getSubmissionStatementReport(String cik, String accessionNumber, String documentName) throws EdgarDataFetcherException {
        accessionNumber = accessionNumber.trim().replaceAll("[\\s-_]","");
        ResponseEntity<byte[]> response = restClient.get()
                .uri(String.format("%s/Archives/edgar/data/%s/%s/%s","https://www.sec.gov",cik,accessionNumber,documentName))
                .header("user-agent",USER_AGENT)
                .retrieve().toEntity(byte[].class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            log.error("Error fetching statement report {} for cik {} with error {}", documentName,cik, response.getStatusCode());
            throw new EdgarDataFetcherException(STR."""
            Error fetching statement report \{documentName} for cik \{cik} with code \{response.getStatusCode()}
            """);
        }
    }

    private String normalizeCikNumber(String cikNumber) {
        if (StringUtils.isNotBlank(cikNumber) && cikNumber.length() < 10) {
            int missingDigits = 10 - cikNumber.length();
            StringBuilder normalizedCikNumberBuilder = new StringBuilder(cikNumber);
            for (int i = 0; i < missingDigits; i++ ) {
                normalizedCikNumberBuilder.insert(0,"0");
            }
            return normalizedCikNumberBuilder.toString();
        } else {
            return cikNumber;
        }
    }

    private void throttlingRequestRate() {
        if (operationsCounter >= maxRequestRate) {
            Duration interval = Duration.between(timeWindow,LocalDateTime.now());
            if (interval.isNegative()) {
                try {
                    Thread.currentThread().wait(interval.abs().toMillis());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                synchronized (this) {
                    restartOperationWindow();
                }
            }
        }
    }

    private void registerOperation() {
        synchronized (this) {
            this.operationsCounter += 1;
        }
    }

    private void restartOperationWindow() {
        synchronized (this) {
            timeWindow = LocalDateTime.now().plus(windowDuration);
            operationsCounter = 0;
        }
    }
}
