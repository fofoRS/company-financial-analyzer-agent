package org.fofo.stock.agent.scrapper.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("edgar.scrapper")
public class EdgarScrapperConfiguration {

    private Submissions submissions;

    public Submissions getSubmissions() {
        return submissions;
    }

    public void setSubmissions(Submissions submissions) {
        this.submissions = submissions;
    }

    public static class Submissions {
        private List<String> reports;

        public Submissions(List<String> reports) {
            this.reports = reports;
        }

        public Submissions() {}

        public List<String> getReports() {
            return reports;
        }

        public void setReports(List<String> reports) {
            this.reports = reports;
        }
    }
}
