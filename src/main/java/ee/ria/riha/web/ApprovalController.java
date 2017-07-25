package ee.ria.riha.web;

import ee.ria.riha.domain.model.Approval;
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
 * Info system approvals
 */
@RestController
@Api("approvals")
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
    @GetMapping("/systems/approvals")
    @ApiOperation("List all approvals of all information systems")
    @ApiPageableAndFilterableParams
    public ResponseEntity<PagedResponse<Approval>> listApprovals(Pageable pageable, Filterable filterable) {
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
    @GetMapping("/systems/{infoSystemUuid}/approvals")
    @ApiOperation("List all information system approvals")
    @ApiPageableAndFilterableParams
    public ResponseEntity<PagedResponse<Approval>> listInfoSystemApprovals(
            @PathVariable("infoSystemUuid") UUID infoSystemUuid,
            Pageable pageable, Filterable filterable) {
        return ResponseEntity.ok(approvalService.listInfoSystemApprovals(infoSystemUuid, pageable, filterable));
    }

    /**
     * Retrieve single approval by id.
     *
     * @param infoSystemUuid info system UUID
     * @param approvalId     id of an approval
     * @return approval or null
     */
    @GetMapping("/systems/{infoSystemUuid}/approvals/{approvalId}")
    @ApiOperation("Get single information system approval")
    public ResponseEntity<Approval> getInfoSystemApproval(@PathVariable("infoSystemUuid") UUID infoSystemUuid,
                                                          @PathVariable("approvalId") Long approvalId) {
        return ResponseEntity.ok(approvalService.getApprovalById(approvalId));
    }

    /**
     * Adds single approval to the info system.
     *
     * @param infoSystemUuid info system UUID
     * @param approval       approval model
     * @return created approval
     */
    @PostMapping("/systems/{infoSystemUuid}/approvals")
    @ApiOperation("Create new information system approval")
    public ResponseEntity<Approval> createInfoSystemApproval(@PathVariable("infoSystemUuid") UUID infoSystemUuid,
                                                             @RequestBody Approval approval) {
        return ResponseEntity.ok(approvalService.createInfoSystemApproval(infoSystemUuid, approval));
    }

}