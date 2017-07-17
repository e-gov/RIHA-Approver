package ee.ria.riha.service;

import ee.ria.riha.domain.model.Approval;
import ee.ria.riha.domain.model.ApprovalComment;
import ee.ria.riha.storage.domain.CommentRepository;
import ee.ria.riha.storage.domain.model.Comment;
import ee.ria.riha.storage.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * Info system approvals services.
 *
 * @author Valentin Suhnjov
 */
@Service
public class ApprovalService {

    private static final Function<Comment, Approval> commentToApproval = comment -> {
        Approval approval = new Approval();
        approval.setId(comment.getComment_id());
        approval.setInfoSystemId(comment.getInfosystem_uuid());
        approval.setComment(comment.getComment());

        return approval;
    };

    private static final Function<Comment, ApprovalComment> commentToApprovalComment = comment -> {
        ApprovalComment approvalComment = new ApprovalComment();
        approvalComment.setId(comment.getComment_id());
        approvalComment.setComment(comment.getComment());

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
        PagedResponse<Comment> response = commentRepository.list(pageable, addNullApprovalParentId(filterable));

        return new PagedResponse<>(new PageRequest(response.getPage(), response.getSize()),
                                   response.getTotalElements(),
                                   response.getContent().stream()
                                           .map(commentToApproval)
                                           .collect(toList()));
    }

    private Filterable addNullApprovalParentId(Filterable filterable) {
        return addFilter(filterable, "comment_parent_id,isnull,null");
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
        PagedResponse<Comment> response = commentRepository.list(pageable, addInfoSystemUuid(
                addParentApprovalId(filterable, approvalId), infoSystemUuid));

        return new PagedResponse<>(new PageRequest(response.getPage(), response.getSize()),
                                   response.getTotalElements(),
                                   response.getContent().stream()
                                           .map(commentToApprovalComment)
                                           .collect(toList()));
    }

    private Filterable addParentApprovalId(Filterable filterable, Long parentApprovalId) {
        return addFilter(filterable, "comment_parent_id,=," + parentApprovalId);
    }

    private Filterable addFilter(Filterable filterable, String filter) {
        String result = filterable.getFilter() != null
                ? filterable.getFilter() + "," + filter
                : filter;

        return new FilterRequest(result, filterable.getSort(), filterable.getFields());
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
        PagedResponse<Comment> response = commentRepository.list(pageable,
                                                                 addNullApprovalParentId(
                                                                         addInfoSystemUuid(filterable,
                                                                                           infoSystemUuid)));

        return new PagedResponse<>(new PageRequest(response.getPage(), response.getSize()),
                                   response.getTotalElements(),
                                   response.getContent().stream()
                                           .map(commentToApproval)
                                           .collect(toList()));
    }

    private Filterable addInfoSystemUuid(Filterable filterable, UUID infoSystemUuid) {
        return addFilter(filterable, "infosystem_uuid,=," + infoSystemUuid.toString());
    }
}
