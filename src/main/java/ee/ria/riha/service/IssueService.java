package ee.ria.riha.service;

import ee.ria.riha.domain.model.Issue;
import ee.ria.riha.domain.model.IssueComment;
import ee.ria.riha.domain.model.IssueStatus;
import ee.ria.riha.domain.model.EventType;
import ee.ria.riha.storage.domain.CommentRepository;
import ee.ria.riha.storage.domain.model.Comment;
import ee.ria.riha.storage.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Function;

import static ee.ria.riha.domain.model.IssueStatus.OPEN;
import static ee.ria.riha.domain.model.EventType.ISSUE;
import static ee.ria.riha.domain.model.EventType.ISSUE_COMMENT;
import static java.util.stream.Collectors.toList;

/**
 * Info system issues and comments service
 *
 * @author Valentin Suhnjov
 */
@Service
public class IssueService {

    private static final String PARENT_ISSUE_ID_IS_NULL_FILTER = "comment_parent_id,isnull,null";

    private static final Function<Comment, Issue> COMMENT_TO_ISSUE = comment -> {
        if (comment == null) {
            return null;
        }
        Issue issue = new Issue();
        issue.setId(comment.getComment_id());
        issue.setInfoSystemUuid(comment.getInfosystem_uuid());
        issue.setDateCreated(comment.getCreation_date());
        issue.setTitle(comment.getTitle());
        issue.setComment(comment.getComment());
        issue.setAuthorName(comment.getAuthor_name());
        issue.setAuthorPersonalCode(comment.getAuthor_personal_code());
        issue.setOrganizationName(comment.getOrganization_name());
        issue.setOrganizationCode(comment.getOrganization_code());
        issue.setStatus(comment.getStatus() != null ? IssueStatus.valueOf(comment.getStatus()) : null);

        return issue;
    };

    private static final Function<Comment, IssueComment> COMMENT_TO_ISSUE_COMMENT = comment -> {
        if (comment == null) {
            return null;
        }
        IssueComment issueComment = new IssueComment();
        issueComment.setId(comment.getComment_id());
        issueComment.setInfoSystemUuid(comment.getInfosystem_uuid());
        issueComment.setDateCreated(comment.getCreation_date());
        issueComment.setApprovalId(comment.getComment_parent_id());
        issueComment.setComment(comment.getComment());
        issueComment.setAuthorName(comment.getAuthor_name());
        issueComment.setAuthorPersonalCode(comment.getAuthor_personal_code());
        issueComment.setOrganizationName(comment.getOrganization_name());
        issueComment.setOrganizationCode(comment.getOrganization_code());

        return issueComment;
    };

    private static final Function<Issue, Comment> ISSUE_TO_COMMENT = issue -> {
        if (issue == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setType(EventType.ISSUE.name());
        comment.setComment_id(issue.getId());
        comment.setInfosystem_uuid(issue.getInfoSystemUuid());
        comment.setTitle(issue.getTitle());
        comment.setComment(issue.getComment());
        comment.setAuthor_name(issue.getAuthorName());
        comment.setAuthor_personal_code(issue.getAuthorPersonalCode());
        comment.setOrganization_name(issue.getOrganizationName());
        comment.setOrganization_code(issue.getOrganizationCode());
        if (issue.getStatus() != null) {
            comment.setStatus(issue.getStatus().name());
        }

        return comment;
    };

    private static final Function<IssueComment, Comment> ISSUE_COMMENT_TO_COMMENT = issueComment -> {
        if (issueComment == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setType(EventType.ISSUE_COMMENT.name());
        comment.setComment_id(issueComment.getId());
        comment.setInfosystem_uuid(issueComment.getInfoSystemUuid());
        comment.setComment_parent_id(issueComment.getApprovalId());
        comment.setComment(issueComment.getComment());
        comment.setAuthor_name(issueComment.getAuthorName());
        comment.setAuthor_personal_code(issueComment.getAuthorPersonalCode());
        comment.setOrganization_name(issueComment.getOrganizationName());
        comment.setOrganization_code(issueComment.getOrganizationCode());

        return comment;
    };

    @Autowired
    private CommentRepository commentRepository;

    /**
     * List issues of all info systems.
     *
     * @param pageable   paging definition
     * @param filterable filter definition
     * @return paginated list of issues
     */
    public PagedResponse<Issue> listIssues(Pageable pageable, Filterable filterable) {
        Filterable filter = new FilterRequest(filterable.getFilter(), filterable.getSort(), filterable.getFields())
                .addFilter(getIssueTypeEqFilter(ISSUE))
                .addFilter(getParentIssueIdIsNullFilter());

        PagedResponse<Comment> response = commentRepository.list(pageable, filter);

        return new PagedResponse<>(new PageRequest(response.getPage(), response.getSize()),
                                   response.getTotalElements(),
                                   response.getContent().stream()
                                           .map(COMMENT_TO_ISSUE)
                                           .collect(toList()));
    }

    /**
     * List concrete info system concrete issue comments.
     *
     * @param infoSystemUuid info system UUID
     * @param issueId     issue id
     * @param pageable       paging definition
     * @param filterable     filter definition
     * @return paginated list of issue comments
     */
    public PagedResponse<IssueComment> listInfoSystemIssueComments(UUID infoSystemUuid, Long issueId,
                                                                   Pageable pageable,
                                                                   Filterable filterable) {
        Filterable filter = new FilterRequest(filterable.getFilter(), filterable.getSort(), filterable.getFields())
                .addFilter(getIssueTypeEqFilter(ISSUE_COMMENT))
                .addFilter(getInfoSystemUuidEqFilter(infoSystemUuid))
                .addFilter(getParentIssueIdEqFilter(issueId));

        PagedResponse<Comment> response = commentRepository.list(pageable, filter);

        return new PagedResponse<>(new PageRequest(response.getPage(), response.getSize()),
                                   response.getTotalElements(),
                                   response.getContent().stream()
                                           .map(COMMENT_TO_ISSUE_COMMENT)
                                           .collect(toList()));
    }

    /**
     * List concrete info system issues.
     *
     * @param infoSystemUuid info system UUID
     * @param pageable       paging definition
     * @param filterable     filter definition
     * @return paginated list of issues
     */
    public PagedResponse<Issue> listInfoSystemIssues(UUID infoSystemUuid, Pageable pageable,
                                                     Filterable filterable) {
        Filterable filter = new FilterRequest(filterable.getFilter(), filterable.getSort(), filterable.getFields())
                .addFilter(getIssueTypeEqFilter(ISSUE))
                .addFilter(getParentIssueIdIsNullFilter())
                .addFilter(getInfoSystemUuidEqFilter(infoSystemUuid));

        PagedResponse<Comment> response = commentRepository.list(pageable, filter);

        return new PagedResponse<>(new PageRequest(response.getPage(), response.getSize()),
                                   response.getTotalElements(),
                                   response.getContent().stream()
                                           .map(COMMENT_TO_ISSUE)
                                           .collect(toList()));
    }

    /**
     * Retrieves single issue by id
     *
     * @param issueId id of an issue
     * @return single issue
     */
    public Issue getIssueById(Long issueId) {
        Comment issue = commentRepository.get(issueId);

        if (EventType.valueOf(issue.getType()) != ISSUE) {
            throw new IllegalArgumentException("Not an issue");
        }

        return COMMENT_TO_ISSUE.apply(issue);
    }

    /**
     * Get single issue comment by id
     *
     * @param issueCommentId id of a comment
     * @return single comment or null
     */
    public IssueComment getInfoSystemIssueCommentById(Long issueCommentId) {
        Comment comment = commentRepository.get(issueCommentId);

        if (EventType.valueOf(comment.getType()) != ISSUE_COMMENT) {
            throw new IllegalArgumentException("Not a comment");
        }

        return COMMENT_TO_ISSUE_COMMENT.apply(comment);
    }

    public Issue createInfoSystemIssue(UUID infoSystemUuid, Issue issue) {
        issue.setInfoSystemUuid(infoSystemUuid);
        issue.setStatus(OPEN);
        Long issueId = commentRepository.add(ISSUE_TO_COMMENT.apply(issue)).get(0);
        return COMMENT_TO_ISSUE.apply(commentRepository.get(issueId));
    }

    public IssueComment createInfoSystemIssueComment(UUID infoSystemUuid, Long issueId,
                                                     IssueComment issueComment) {
        issueComment.setApprovalId(issueId);
        issueComment.setInfoSystemUuid(infoSystemUuid);
        Long issueCommentId = commentRepository.add(ISSUE_COMMENT_TO_COMMENT.apply(issueComment)).get(0);
        return COMMENT_TO_ISSUE_COMMENT.apply(commentRepository.get(issueCommentId));
    }

    private String getParentIssueIdIsNullFilter() {
        return PARENT_ISSUE_ID_IS_NULL_FILTER;
    }

    private String getIssueTypeEqFilter(EventType eventType) {
        return "type,=," + eventType.name();
    }

    private String getParentIssueIdEqFilter(Long parentIssueId) {
        return "comment_parent_id,=," + parentIssueId;
    }

    private String getInfoSystemUuidEqFilter(UUID infoSystemUuid) {
        return "infosystem_uuid,=," + infoSystemUuid.toString();
    }

}
