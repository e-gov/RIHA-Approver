package ee.ria.riha.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

/**
 * Issue comment
 *
 * @author Valentin Suhnjov
 */
@Getter
@Setter
public class IssueComment {

    private Long id;
    private UUID infoSystemUuid;
    private Date dateCreated;
    private Long approvalId;
    private String comment;
    private String authorName;
    private String authorPersonalCode;
    private String organizationName;
    private String organizationCode;
    private IssueStatus status;

}
