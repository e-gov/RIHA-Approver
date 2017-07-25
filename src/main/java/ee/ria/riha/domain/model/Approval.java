package ee.ria.riha.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

/**
 * Approval
 *
 * @author Valentin Suhnjov
 */
@Getter
@Setter
public class Approval {

    private Long id;
    private UUID infoSystemUuid;
    private Date dateCreated;
    private String title;
    private String comment;
    private String authorName;
    private String authorPersonalCode;
    private String organizationName;
    private String organizationCode;
    private ApprovalStatus status;

}
