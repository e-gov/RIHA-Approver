package ee.ria.riha.web;

import ee.ria.riha.domain.model.Approval;
import ee.ria.riha.domain.model.ApprovalComment;
import ee.ria.riha.service.ApprovalService;
import ee.ria.riha.storage.util.Filterable;
import ee.ria.riha.storage.util.Pageable;
import ee.ria.riha.storage.util.PagedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/approvals")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    /**
     * Retrieve paginated and filtered list of all approvals.
     *
     * @param pageable   paging definition
     * @param filterable filter definition
     * @return paginated list of approvals
     */
    @GetMapping
    public ResponseEntity<PagedResponse<Approval>> list(Pageable pageable, Filterable filterable) {
        return ResponseEntity.ok(approvalService.listApprovals(pageable, filterable));
    }

    /**
     * Retrieve paginated and filtered list of approvals for given info system.
     *
     * @param infoSystemUuid info system UUID
     * @param pageable       paging definition
     * @param filterable     filter definition
     * @return paginated list of info system approvals
     */
    @GetMapping(path = "/{infoSystemUuid}")
    public ResponseEntity<PagedResponse<Approval>> listInfoSystemApprovals(
            @PathVariable("infoSystemUuid") UUID infoSystemUuid,
            Pageable pageable, Filterable filterable) {
        return ResponseEntity.ok(approvalService.listInfoSystemApprovals(infoSystemUuid, pageable, filterable));
    }

    /**
     * Retrieve paginated and filtered list of approval comments for given info system.
     *
     * @param infoSystemUuid info system UUID
     * @param approvalId     approval id
     * @param pageable       paging definition
     * @param filterable     filter definition
     * @return paginated list of approval comments
     */
    @GetMapping(path = "/{infoSystemUuid}/{approvalId}/comments")
    public ResponseEntity<PagedResponse<ApprovalComment>> listInfoSystemApprovalComments(
            @PathVariable("infoSystemUuid") UUID infoSystemUuid,
            @PathVariable("approvalId") Long approvalId,
            Pageable pageable,
            Filterable filterable) {
        return ResponseEntity.ok(
                approvalService.listInfoSystemApprovalComments(infoSystemUuid, approvalId, pageable, filterable));
    }
}