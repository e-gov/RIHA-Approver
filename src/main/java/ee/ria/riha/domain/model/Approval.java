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
    private String comment;

}
