package org.fofo.stock.agent.scrapper.connector.edgar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;


public class EdgarPlatformResponsesConverter {

    public static Optional<EdgarCompanySubmissions> convertCompanySubmissionsRepose(
            String responseData, ObjectMapper objectMapper) throws JsonProcessingException {

        JsonNode rootNode = objectMapper.readTree(responseData);
        JsonNode filingRecentSubmissions = rootNode.findPath("filings").findPath("recent");
        if (filingRecentSubmissions.isMissingNode()) {
            return Optional.empty();
        } else {

            ArrayNode accessionNumberNode = (ArrayNode) filingRecentSubmissions.findPath("accessionNumber");
            if (!accessionNumberNode.isMissingNode() && !accessionNumberNode.isEmpty()) {
                ArrayNode filingDateNode = (ArrayNode) filingRecentSubmissions.findPath("filingDate");
                ArrayNode reportDateNode = (ArrayNode) filingRecentSubmissions.findPath("reportDate");
                ArrayNode primaryDocumentNode = (ArrayNode) filingRecentSubmissions.findPath("primaryDocument");
                ArrayNode primaryDocDescriptionNode = (ArrayNode) filingRecentSubmissions.findPath("primaryDocDescription");

                Set<EdgarCompanySubmissions.CompanyFiling> companyFilings = new HashSet<>();
                for (int i = 0; i < accessionNumberNode.size(); i++) {
                    String accessionNumber = accessionNumberNode.get(i).asText();
                    LocalDate filingDate = formatDateNode(filingDateNode.get(i).asText());
                    LocalDate reportDate = formatDateNode(reportDateNode.get(i).asText());
                    String primaryDocument = primaryDocumentNode.get(i).asText();
                    String primaryDocDescription = primaryDocDescriptionNode.get(i).asText();
                    companyFilings.add(new EdgarCompanySubmissions.CompanyFiling(
                            accessionNumber, filingDate, reportDate, primaryDocument, primaryDocDescription));
                }
                String cik = filingRecentSubmissions.findPath("cik").asText();
                return Optional.of(new EdgarCompanySubmissions(cik, companyFilings));
            } else {
                return Optional.empty();
            }
        }
    }

    public static List<EdgarCompanyTicker> convertEdgarCompanyTicker(String responseBody, ObjectMapper objectMapper)
            throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode dataNode = rootNode.path("data");
        if (!dataNode.isMissingNode()) {
            List<EdgarCompanyTicker> edgarCompanyTickerList = new ArrayList<>();
            Iterator<JsonNode> dataElements = dataNode.elements();
            while (dataElements.hasNext()) {
                JsonNode elementArray = dataElements.next();
                String cik = elementArray.path(0).asText();
                String name = elementArray.path(1).asText();
                String ticker = elementArray.path(2).asText();
                String exchange = elementArray.path(3).asText();
                edgarCompanyTickerList.add(new EdgarCompanyTicker(name, ticker,cik,exchange));
            }
            return edgarCompanyTickerList;
        } else {
            return List.of();
        }
    }

    private static LocalDate formatDateNode(String dateValueAsString) {
        String format = "yyyy-MM-dd";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        Pattern validStringCharacterPatter = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
        boolean isAValidDate = validStringCharacterPatter.matcher(dateValueAsString).matches();
        if (StringUtils.isNotBlank(dateValueAsString) && isAValidDate) {
            return LocalDate.parse(dateValueAsString, dateTimeFormatter);
        } else {
            return null;
        }
    }
}
