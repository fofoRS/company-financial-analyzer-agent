package org.fofo.stock.agent.scrapper.connector.notifier;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@AllArgsConstructor
public class SubmissionLoadedEvent extends Event<SubmissionLoadedEvent.SubmissionsLoadedPayload> {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubmissionsLoadedPayload {
        private String cik;
        private String primaryDocumentDescription;
        private String primaryDocument;
        private String accessionNumber;


    }
}
