package org.fofo.stock.agent.scrapper.connector.edgar;

import java.util.List;
import java.util.Optional;

public interface EdgarPlatformConnector {
    List<EdgarCompanyTicker> getEdgarCompanyInformation() throws EdgarDataFetcherException;
    Optional<EdgarCompanySubmissions> getEdgarCompanySubmissions(String cik) throws EdgarDataFetcherException;
    byte[] getSubmissionStatementReport(String cik,String accessionNumber, String documentName) throws EdgarDataFetcherException;
}
