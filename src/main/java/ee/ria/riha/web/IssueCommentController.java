package ee.ria.riha.web;

import ee.ria.riha.domain.model.IssueComment;
import ee.ria.riha.service.IssueService;
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
 * Info system issue comments
 *
 * @author Valentin Suhnjov
 */
@RestController
@Api("issue comments")
public class IssueCommentController {

    @Autowired
    private IssueService issueService;

    /**
     * Retrieve paginated and filtered list of issue comments for given info system.
     *
     * @param infoSystemUuid info system UUID
     * @param issueId     issue id
     * @param pageable       paging definition
     * @param filterable     filter definition
     * @return paginated list of issue comments
     */
    @GetMapping("/systems/{infoSystemUuid}/issues/{issueId}/comments")
    @ApiOperation("List all information system issue comments")
    @ApiPageableAndFilterableParams
    public ResponseEntity<PagedResponse<IssueComment>> listInfoSystemIssueComments(
            @PathVariable("infoSystemUuid") UUID infoSystemUuid,
            @PathVariable("issueId") Long issueId,
            Pageable pageable,
            Filterable filterable) {
        return ResponseEntity.ok(
                issueService.listInfoSystemIssueComments(infoSystemUuid, issueId, pageable, filterable));
    }

    /**
     * Get single comment by its id.
     *
     * @param infoSystemUuid info system UUID
     * @param issueId     an id of an issue
     * @param commentId      comment id
     * @return single concrete comment or null
     */
    @GetMapping("/systems/{infoSystemUuid}/issues/{issueId}/comments/{commentId}")
    @ApiOperation("Get single information system issue comment")
    public ResponseEntity<IssueComment> getInfoSystemIssueComment(
            @PathVariable("infoSystemUuid") UUID infoSystemUuid,
            @PathVariable("issueId") Long issueId,
            @PathVariable("commentId") Long commentId) {
        return ResponseEntity.ok(
                issueService.getInfoSystemIssueCommentById(commentId));
    }

    /**
     * Adds single comment to the info system issue.
     *
     * @param infoSystemUuid  info system UUID
     * @param issueId      an id of an issue
     * @param issueComment comment model
     * @return created issue comment
     */
    @PostMapping("/systems/{infoSystemUuid}/issues/{issueId}/comments")
    @ApiOperation("Create new information system issue comment")
    public ResponseEntity<IssueComment> createInfoSystemIssueComment(
            @PathVariable("infoSystemUuid") UUID infoSystemUuid, @PathVariable("issueId") Long issueId,
            @RequestBody IssueComment issueComment) {
        return ResponseEntity.ok(
                issueService.createInfoSystemIssueComment(infoSystemUuid, issueId, issueComment));
    }

}
