package ee.ria.riha.web;

import ee.ria.riha.domain.model.Entity;
import ee.ria.riha.service.IssueTimelineService;
import ee.ria.riha.storage.util.ApiPageableParams;
import ee.ria.riha.storage.util.Pageable;
import ee.ria.riha.storage.util.PagedResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Issue timeline
 *
 * @author Valentin Suhnjov
 */
@Controller
@Api("Issue timeline")
public class IssueTimelineController {

    @Autowired
    private IssueTimelineService issueTimelineService;

    /**
     * List various issue events. Does not include issue itself.
     *
     * @param issueId  an id of an issue
     * @param pageable paging definition
     * @return paged list of issue events
     */
    @GetMapping("/issues/{issueId}/timeline")
    @ApiOperation("Get issue timeline")
    @ApiPageableParams
    public ResponseEntity<PagedResponse<Entity>> getTimeline(@PathVariable("issueId") Long issueId, Pageable pageable) {
        return ResponseEntity.ok(issueTimelineService.listTimeline(issueId, pageable));
    }

}
