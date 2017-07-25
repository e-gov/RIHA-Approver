package ee.ria.riha.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

/**
 * Approval comment
 *
 * @author Valentin Suhnjov
 */
@Getter
@Setter
public class ApprovalComment {

    private Long id;
    private UUID infoSystemUuid;
    private Date dateCreated;
    private Long approvalId;
    private String comment;
    private String authorName;
    private String authorPersonalCode;
    private String organizationName;
    private String organizationCode;

}
