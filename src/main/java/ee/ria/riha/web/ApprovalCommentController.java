package ee.ria.riha.web;

import ee.ria.riha.domain.model.ApprovalComment;
import ee.ria.riha.service.ApprovalService;
import ee.ria.riha.storage.util.Filterable;
import ee.ria.riha.storage.util.Pageable;
import ee.ria.riha.storage.util.PagedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Info system approval comments
 *
 * @author Valentin Suhnjov
 */
@RestController
public class ApprovalCommentController {

    @Autowired
    private ApprovalService approvalService;

    /**
     * Retrieve paginated and filtered list of approval comments for given info system.
     *
     * @param infoSystemUuid info system UUID
     * @param approvalId     approval id
     * @param pageable       paging definition
     * @param filterable     filter definition
     * @return paginated list of approval comments
     */
    @GetMapping("/systems/{infoSystemUuid}/approvals/{approvalId}/comments")
    public ResponseEntity<PagedResponse<ApprovalComment>> listInfoSystemApprovalComments(
            @PathVariable("infoSystemUuid") UUID infoSystemUuid,
            @PathVariable("approvalId") Long approvalId,
            Pageable pageable,
            Filterable filterable) {
        return ResponseEntity.ok(
                approvalService.listInfoSystemApprovalComments(infoSystemUuid, approvalId, pageable, filterable));
    }

    /**
     * Get single comment by its id.
     *
     * @param infoSystemUuid info system UUID
     * @param approvalId     approval id
     * @param commentId      comment id
     * @return single concrete comment or null
     */
    @GetMapping("/systems/{infoSystemUuid}/approvals/{approvalId}/comments/{commentId}")
    public ResponseEntity<ApprovalComment> getInfoSystemApprovalComment(
            @PathVariable("infoSystemUuid") UUID infoSystemUuid,
            @PathVariable("approvalId") Long approvalId,
            @PathVariable("commentId") Long commentId) {
        return ResponseEntity.ok(
                approvalService.getInfoSystemApprovalCommentById(commentId));
    }

}
