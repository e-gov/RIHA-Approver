package ee.ria.riha.service;

import ee.ria.riha.domain.model.Approval;
import ee.ria.riha.domain.model.ApprovalComment;
import ee.ria.riha.domain.model.ApprovalStatus;
import ee.ria.riha.storage.domain.CommentRepository;
import ee.ria.riha.storage.domain.model.Comment;
import ee.ria.riha.storage.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * Info system approvals and comments service
 *
 * @author Valentin Suhnjov
 */
@Service
public class ApprovalService {

    private static final Function<Comment, Approval> commentToApproval = comment -> {
        if (comment == null) {
            return null;
        }
        Approval approval = new Approval();
        approval.setId(comment.getComment_id());
        approval.setInfoSystemUuid(comment.getInfosystem_uuid());
        approval.setTitle(comment.getTitle());
        approval.setComment(comment.getComment());
        approval.setAuthor_name(comment.getAuthor_name());
        approval.setAuthor_personal_code(comment.getAuthor_personal_code());
        approval.setOrganization_name(comment.getOrganization_name());
        approval.setOrganization_code(comment.getOrganization_code());
        approval.setStatus(comment.getStatus() != null ? ApprovalStatus.valueOf(comment.getStatus()) : null);

        return approval;
    };

    private static final Function<Comment, ApprovalComment> commentToApprovalComment = comment -> {
        if (comment == null) {
            return null;
        }
        ApprovalComment approvalComment = new ApprovalComment();
        approvalComment.setId(comment.getComment_id());
        approvalComment.setInfoSystemUuid(comment.getInfosystem_uuid());
        approvalComment.setApprovalId(comment.getComment_parent_id());
        approvalComment.setComment(comment.getComment());
        approvalComment.setAuthor_name(comment.getAuthor_name());
        approvalComment.setAuthor_personal_code(comment.getAuthor_personal_code());
        approvalComment.setOrganization_name(comment.getOrganization_name());
        approvalComment.setOrganization_code(comment.getOrganization_code());

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
                .addFilter(getNullApprovalParentIdFilter());

        PagedResponse<Comment> response = commentRepository.list(pageable, filter);

        return new PagedResponse<>(new PageRequest(response.getPage(), response.getSize()),
                                   response.getTotalElements(),
                                   response.getContent().stream()
                                           .map(commentToApproval)
                                           .collect(toList()));
    }

    private String getNullApprovalParentIdFilter() {
        return "comment_parent_id,isnull,null";
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
                .addFilter(getInfoSystemUuidFilter(infoSystemUuid))
                .addFilter(getParentApprovalIdFilter(approvalId));

        PagedResponse<Comment> response = commentRepository.list(pageable, filter);

        return new PagedResponse<>(new PageRequest(response.getPage(), response.getSize()),
                                   response.getTotalElements(),
                                   response.getContent().stream()
                                           .map(commentToApprovalComment)
                                           .collect(toList()));
    }

    private String getParentApprovalIdFilter(Long parentApprovalId) {
        return "comment_parent_id,=," + parentApprovalId;
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
                .addFilter(getNullApprovalParentIdFilter())
                .addFilter(getInfoSystemUuidFilter(infoSystemUuid));

        PagedResponse<Comment> response = commentRepository.list(pageable, filter);

        return new PagedResponse<>(new PageRequest(response.getPage(), response.getSize()),
                                   response.getTotalElements(),
                                   response.getContent().stream()
                                           .map(commentToApproval)
                                           .collect(toList()));
    }

    private String getInfoSystemUuidFilter(UUID infoSystemUuid) {
        return "infosystem_uuid,=," + infoSystemUuid.toString();
    }

    public Approval getApproval(Long approvalId) {
        return commentToApproval.apply(commentRepository.get(approvalId));
    }

    /**
     * Get single approval comment by id
     *
     * @param commentId id of a comment
     * @return single comment or null
     */
    public ApprovalComment getInfoSystemApprovalComment(Long commentId) {
        return commentToApprovalComment.apply(commentRepository.get(commentId));
    }

}
