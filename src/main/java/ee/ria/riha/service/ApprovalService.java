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

import static ee.ria.riha.domain.model.ApprovalStatus.OPEN;
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

    private static final Function<Approval, Comment> APPROVAL_TO_COMMENT = approval -> {
        if (approval == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setType(ApprovalType.APPROVAL.name());
        comment.setComment_id(approval.getId());
        comment.setInfosystem_uuid(approval.getInfoSystemUuid());
        comment.setTitle(approval.getTitle());
        comment.setComment(approval.getComment());
        comment.setAuthor_name(approval.getAuthorName());
        comment.setAuthor_personal_code(approval.getAuthorPersonalCode());
        comment.setOrganization_name(approval.getOrganizationName());
        comment.setOrganization_code(approval.getOrganizationCode());
        if (approval.getStatus() != null) {
            comment.setStatus(approval.getStatus().name());
        }

        return comment;
    };

    private static final Function<ApprovalComment, Comment> APPROVAL_COMMENT_TO_COMMENT = approvalComment -> {
        if (approvalComment == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setType(ApprovalType.COMMENT.name());
        comment.setComment_id(approvalComment.getId());
        comment.setInfosystem_uuid(approvalComment.getInfoSystemUuid());
        comment.setComment_parent_id(approvalComment.getApprovalId());
        comment.setComment(approvalComment.getComment());
        comment.setAuthor_name(approvalComment.getAuthorName());
        comment.setAuthor_personal_code(approvalComment.getAuthorPersonalCode());
        comment.setOrganization_name(approvalComment.getOrganizationName());
        comment.setOrganization_code(approvalComment.getOrganizationCode());

        return comment;
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

    public Approval createInfoSystemApproval(UUID infoSystemUuid, Approval approval) {
        approval.setInfoSystemUuid(infoSystemUuid);
        approval.setStatus(OPEN);
        Long approvalId = commentRepository.add(APPROVAL_TO_COMMENT.apply(approval)).get(0);
        return COMMENT_TO_APPROVAL.apply(commentRepository.get(approvalId));
    }

    public ApprovalComment createInfoSystemApprovalComment(UUID infoSystemUuid, Long approvalId, ApprovalComment approvalComment) {
        approvalComment.setApprovalId(approvalId);
        approvalComment.setInfoSystemUuid(infoSystemUuid);
        Long approvalCommentId = commentRepository.add(APPROVAL_COMMENT_TO_COMMENT.apply(approvalComment)).get(0);
        return COMMENT_TO_APPROVAL_COMMENT.apply(commentRepository.get(approvalCommentId));
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
