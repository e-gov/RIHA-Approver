package ee.ria.riha.web;

import ee.ria.riha.domain.model.ApprovalComment;
import ee.ria.riha.service.ApprovalService;
import ee.ria.riha.storage.util.ApiPageableAndFilterableParams;
import ee.ria.riha.storage.util.Filterable;
import ee.ria.riha.storage.util.Pageable;
import ee.ria.riha.storage.util.PagedResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Info system approval comments
 *
 * @author Valentin Suhnjov
 */
@RestController
@Api("approval comments")
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
    @ApiOperation("List all information system approval comments")
    @ApiPageableAndFilterableParams
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
    @ApiOperation("Get single information system approval comment")
    public ResponseEntity<ApprovalComment> getInfoSystemApprovalComment(
            @PathVariable("infoSystemUuid") UUID infoSystemUuid,
            @PathVariable("approvalId") Long approvalId,
            @PathVariable("commentId") Long commentId) {
        return ResponseEntity.ok(
                approvalService.getInfoSystemApprovalCommentById(commentId));
    }

    /**
     * Adds single comment to the info system approval.
     *
     * @param infoSystemUuid  info system UUID
     * @param approvalId      an id of approval
     * @param approvalComment comment model
     * @return created approval comment
     */
    @PostMapping("/systems/{infoSystemUuid}/approvals/{approvalId}/comments")
    @ApiOperation("Create new information system approval comment")
    public ResponseEntity<ApprovalComment> createInfoSystemApprovalComment(
            @PathVariable("infoSystemUuid") UUID infoSystemUuid, @PathVariable("approvalId") Long approvalId,
            @RequestBody ApprovalComment approvalComment) {
        return ResponseEntity.ok(
                approvalService.createInfoSystemApprovalComment(infoSystemUuid, approvalId, approvalComment));
    }

}
