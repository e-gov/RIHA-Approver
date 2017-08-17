package ee.ria.riha.web;

import ee.ria.riha.domain.model.Issue;
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
 * Info system issues
 */
@RestController
@Api("Issues")
public class IssueController {

    @Autowired
    private IssueService issueService;

    /**
     * Retrieve paginated and filtered list of issues for given info system.
     *
     * @param infoSystemUuid info system UUID
     * @param pageable       paging definition
     * @param filterable     filter definition
     * @return paginated list of info system issues
     */
    @GetMapping("/systems/{infoSystemUuid}/issues")
    @ApiOperation("List all issues of information system")
    @ApiPageableAndFilterableParams
    public ResponseEntity<PagedResponse<Issue>> listInfoSystemIssues(
            @PathVariable("infoSystemUuid") UUID infoSystemUuid,
            Pageable pageable, Filterable filterable) {
        return ResponseEntity.ok(issueService.listInfoSystemIssues(infoSystemUuid, pageable, filterable));
    }

    /**
     * Adds single issue to the info system.
     *
     * @param infoSystemUuid info system UUID
     * @param issue          issue model
     * @return created issue
     */
    @PostMapping("/systems/{infoSystemUuid}/issues")
    @ApiOperation("Create new issue for information system")
    public ResponseEntity<Issue> createInfoSystemIssue(@PathVariable("infoSystemUuid") UUID infoSystemUuid,
                                                       @RequestBody Issue issue) {
        return ResponseEntity.ok(issueService.createInfoSystemIssue(infoSystemUuid, issue));
    }

    /**
     * Retrieve paginated and filtered list of all issues.
     *
     * @param pageable   paging definition
     * @param filterable filter definition
     * @return paginated list of issues
     */
    @GetMapping("/issues")
    @ApiOperation("List all issues of all information systems")
    @ApiPageableAndFilterableParams
    public ResponseEntity<PagedResponse<Issue>> listIssues(Pageable pageable, Filterable filterable) {
        return ResponseEntity.ok(issueService.listIssues(pageable, filterable));
    }

    /**
     * Retrieve single issue by id.
     *
     * @param issueId id of an issue
     * @return issue or null
     */
    @GetMapping("/issues/{issueId}")
    @ApiOperation("Get single information system issue")
    public ResponseEntity<Issue> getInfoSystemIssue(@PathVariable("issueId") Long issueId) {
        return ResponseEntity.ok(issueService.getIssueById(issueId));
    }

}