package ee.ria.riha.domain.model;

import lombok.Getter;
import lombok.Setter;

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
    private String title;
    private String comment;
    private String author_name;
    private String author_personal_code;
    private String organization_name;
    private String organization_code;
    private ApprovalStatus status;

}
