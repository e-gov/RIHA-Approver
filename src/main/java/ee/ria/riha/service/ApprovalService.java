package ee.ria.riha.service;

import ee.ria.riha.domain.model.Approval;
import ee.ria.riha.domain.model.ApprovalComment;
import ee.ria.riha.domain.model.ApprovalStatus;
import ee.ria.riha.domain.model.ApprovalType;
import ee.ria.riha.storage.domain.CommentRepository;
import ee.ria.riha.storage.domain.model.Comment;
import ee.ria.riha.storage.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Function;

import static ee.ria.riha.domain.model.ApprovalType.APPROVAL;
import static ee.ria.riha.domain.model.ApprovalType.COMMENT;
import static java.util.stream.Collectors.toList;

/**
 * Info system approvals and comments service
 *
 * @author Valentin Suhnjov
 */
@Service
public class ApprovalService {

    private static final String PARENT_APPROVAL_ID_IS_NULL_FILTER = "comment_parent_id,isnull,null";

    private static final Function<Comment, Approval> COMMENT_TO_APPROVAL = comment -> {
        if (comment == null) {
            return null;
        }
        Approval approval = new Approval();
        approval.setId(comment.getComment_id());
        approval.setInfoSystemUuid(comment.getInfosystem_uuid());
        approval.setTitle(comment.getTitle());
        approval.setComment(comment.getComment());
        approval.setAuthorName(comment.getAuthor_name());
        approval.setAuthorPersonalCode(comment.getAuthor_personal_code());
        approval.setOrganizationName(comment.getOrganization_name());
        approval.setOrganizationCode(comment.getOrganization_code());
        approval.setStatus(comment.getStatus() != null ? ApprovalStatus.valueOf(comment.getStatus()) : null);

        return approval;
    };

    private static final Function<Comment, ApprovalComment> COMMENT_TO_APPROVAL_COMMENT = comment -> {
        if (comment == null) {
            return null;
        }
        ApprovalComment approvalComment = new ApprovalComment();
        approvalComment.setId(comment.getComment_id());
        approvalComment.setInfoSystemUuid(comment.getInfosystem_uuid());
        approvalComment.setApprovalId(comment.getComment_parent_id());
        approvalComment.setComment(comment.getComment());
        approvalComment.setAuthorName(comment.getAuthor_name());
        approvalComment.setAuthorPersonalCode(comment.getAuthor_personal_code());
        approvalComment.setOrganizationName(comment.getOrganization_name());
        approvalComment.setOrganizationCode(comment.getOrganization_code());

        return approvalComment;
    };

    @Autowired
    private CommentRepository commentRepository;

    /**
     * List approvals of all info systems.
     *
     * @param pageable   paging definition
     * @param filterable filter definition
     * @return paginated list of approvals
     */
    public PagedResponse<Approval> listApprovals(Pageable pageable, Filterable filterable) {
        Filterable filter = new FilterRequest(filterable.getFilter(), filterable.getSort(), filterable.getFields())
                .addFilter(getApprovalTypeEqFilter(APPROVAL))
                .addFilter(getParentApprovalIdIsNullFilter());

        PagedResponse<Comment> response = commentRepository.list(pageable, filter);

        return new PagedResponse<>(new PageRequest(response.getPage(), response.getSize()),
                                   response.getTotalElements(),
                                   response.getContent().stream()
                                           .map(COMMENT_TO_APPROVAL)
                                           .collect(toList()));
    }

    /**
     * List concrete info system concrete approval comments.
     *
     * @param infoSystemUuid info system UUID
     * @param approvalId     approval id
     * @param pageable       paging definition
     * @param filterable     filter definition
     * @return paginated list of approval comments
     */
    public PagedResponse<ApprovalComment> listInfoSystemApprovalComments(UUID infoSystemUuid, Long approvalId,
                                                                         Pageable pageable,
                                                                         Filterable filterable) {
        Filterable filter = new FilterRequest(filterable.getFilter(), filterable.getSort(), filterable.getFields())
                .addFilter(getApprovalTypeEqFilter(COMMENT))
                .addFilter(getInfoSystemUuidEqFilter(infoSystemUuid))
                .addFilter(getParentApprovalIdEqFilter(approvalId));

        PagedResponse<Comment> response = commentRepository.list(pageable, filter);

        return new PagedResponse<>(new PageRequest(response.getPage(), response.getSize()),
                                   response.getTotalElements(),
                                   response.getContent().stream()
                                           .map(COMMENT_TO_APPROVAL_COMMENT)
                                           .collect(toList()));
    }

    /**
     * List concrete info system approvals.
     *
     * @param infoSystemUuid info system UUID
     * @param pageable       paging definition
     * @param filterable     filter definition
     * @return paginated list of approvals
     */
    public PagedResponse<Approval> listInfoSystemApprovals(UUID infoSystemUuid, Pageable pageable,
                                                           Filterable filterable) {
        Filterable filter = new FilterRequest(filterable.getFilter(), filterable.getSort(), filterable.getFields())
                .addFilter(getApprovalTypeEqFilter(APPROVAL))
                .addFilter(getParentApprovalIdIsNullFilter())
                .addFilter(getInfoSystemUuidEqFilter(infoSystemUuid));

        PagedResponse<Comment> response = commentRepository.list(pageable, filter);

        return new PagedResponse<>(new PageRequest(response.getPage(), response.getSize()),
                                   response.getTotalElements(),
                                   response.getContent().stream()
                                           .map(COMMENT_TO_APPROVAL)
                                           .collect(toList()));
    }

    /**
     * Retrieves single approval by id
     *
     * @param approvalId id of an approval
     * @return single approval
     */
    public Approval getApprovalById(Long approvalId) {
        Comment approval = commentRepository.get(approvalId);

        if (ApprovalType.valueOf(approval.getType()) != APPROVAL) {
            throw new IllegalArgumentException("Not an approval");
        }

        return COMMENT_TO_APPROVAL.apply(approval);
    }

    /**
     * Get single approval comment by id
     *
     * @param commentId id of a comment
     * @return single comment or null
     */
    public ApprovalComment getInfoSystemApprovalCommentById(Long commentId) {
        Comment comment = commentRepository.get(commentId);

        if (ApprovalType.valueOf(comment.getType()) != COMMENT) {
            throw new IllegalArgumentException("Not a comment");
        }

        return COMMENT_TO_APPROVAL_COMMENT.apply(comment);
    }

    private String getParentApprovalIdIsNullFilter() {
        return PARENT_APPROVAL_ID_IS_NULL_FILTER;
    }

    private String getApprovalTypeEqFilter(ApprovalType approvalType) {
        return "type,=," + approvalType.name();
    }

    private String getParentApprovalIdEqFilter(Long parentApprovalId) {
        return "comment_parent_id,=," + parentApprovalId;
    }

    private String getInfoSystemUuidEqFilter(UUID infoSystemUuid) {
        return "infosystem_uuid,=," + infoSystemUuid.toString();
    }
}
