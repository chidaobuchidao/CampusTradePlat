package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.EvaluationsMapper;
import cn.kmbeast.mapper.OrdersMapper;
import cn.kmbeast.mapper.UserMapper;
import cn.kmbeast.pojo.entity.Orders;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.PageResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.EvaluationsQueryDto;
import cn.kmbeast.pojo.entity.Evaluations;
import cn.kmbeast.pojo.entity.User;
import cn.kmbeast.pojo.vo.CommentChildVO;
import cn.kmbeast.pojo.vo.CommentParentVO;
import cn.kmbeast.pojo.vo.EvaluationsVO;
import cn.kmbeast.service.EvaluationsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 评论服务实现类
 */
@Service
public class EvaluationsServiceImpl implements EvaluationsService {

    @Resource
    private EvaluationsMapper evaluationsMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private OrdersMapper ordersMapper;

    /**
     * 评论
     *
     * @return Result<String>
     */
    @Override
    public Result<Object> insert(Evaluations evaluations) {
        evaluations.setCommenterId(LocalThreadHolder.getUserId());
        User queryConditionEntity = User.builder().id(LocalThreadHolder.getUserId()).build();
        User user = userMapper.getByActive(queryConditionEntity);
        if (user == null) {
            return ApiResult.error("用户不存在");
        }
        if (user.getIsWord()) {
            return ApiResult.error("账户已被禁言");
        }

        // 用户评价校验
        if ("USER".equals(evaluations.getContentType())) {
            if (evaluations.getOrderId() == null) {
                return ApiResult.error("用户评价必须关联订单");
            }
            Orders order = ordersMapper.getById(evaluations.getOrderId());
            if (order == null) {
                return ApiResult.error("关联订单不存在");
            }
            if (order.getStatus() != 3 && order.getStatus() != 4) {
                return ApiResult.error("只能评价已收货或已完成的订单");
            }
            if (!LocalThreadHolder.getUserId().equals(order.getUserId())) {
                return ApiResult.error("只有买家可以评价");
            }
            if (evaluationsMapper.countByOrderId(evaluations.getOrderId()) > 0) {
                return ApiResult.error("该订单已评价，不可重复评价");
            }
            // contentId = 被评价用户ID（卖家）
            if (evaluations.getRating() == null || evaluations.getRating() < 1 || evaluations.getRating() > 5) {
                return ApiResult.error("评分必须在1-5之间");
            }
        }

        evaluations.setCreateTime(LocalDateTime.now());
        evaluationsMapper.save(evaluations);
        return ApiResult.success("评论成功");
    }

    /**
     * 查询全部评论
     *
     * @return Result<String>
     */
    @Override
    public Result<Object> list(Integer contentId, String contentType) {
        List<CommentParentVO> parentComments = evaluationsMapper.getParentComments(contentId, contentType);
        setUpvoteFlag(parentComments);
        Integer count = evaluationsMapper.totalCount(contentId, contentType);
        return ApiResult.success(new EvaluationsVO(count, parentComments));
    }

    /**
     * 设置点赞状态
     *
     * @param parentComments 评论数据列表
     */
    private void setUpvoteFlag(List<CommentParentVO> parentComments) {
        String userId = LocalThreadHolder.getUserId().toString(); // 预先获取用户ID
        parentComments.forEach(parentComment -> {
            parentComment.setUpvoteFlag(isUserUpvote(parentComment.getUpvoteList(), userId));
            parentComment.setUpvoteCount(countVotes(parentComment.getUpvoteList()));
            Optional.ofNullable(parentComment.getCommentChildVOS())
                    .orElse(Collections.emptyList())
                    .forEach(child -> {
                        child.setUpvoteFlag(isUserUpvote(child.getUpvoteList(), userId));
                        child.setUpvoteCount(countVotes(child.getUpvoteList()));
                    });
        });
    }

    /**
     * 判断用户是否已点赞
     *
     * @param voteStr 点赞用户ID字符串（逗号分隔）
     * @param userId  用户ID
     * @return 是否已点赞
     */
    private boolean isUserUpvote(String voteStr, String userId) {
        return Optional.ofNullable(voteStr)
                .map(s -> Arrays.asList(s.split(",")))
                .orElse(Collections.emptyList())
                .contains(userId);
    }

    /**
     * 计算点赞数
     *
     * @param voteStr 点赞用户ID字符串（逗号分隔）
     * @return 点赞数
     */
    private int countVotes(String voteStr) {
        if (voteStr == null || voteStr.isEmpty()) return 0;
        return voteStr.split(",").length;
    }

    /**
     * 分页查询评论
     *
     * @return Result<String>
     */
    @Override
    public Result<Object> query(EvaluationsQueryDto evaluationsQueryDto) {
        List<CommentChildVO> list = evaluationsMapper.query(evaluationsQueryDto);
        Integer totalPage = evaluationsMapper.queryCount(evaluationsQueryDto);
        return PageResult.success(list, totalPage);
    }

    /**
     * 批量删除评论数据
     *
     * @return Result<String>
     */
    @Override
    public Result<Object> batchDelete(List<Integer> ids) {
        evaluationsMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 评论删除
     *
     * @return Result<String>
     */
    @Override
    public Result<String> delete(Integer id) {
        evaluationsMapper.batchDelete(Collections.singletonList(id));
        return ApiResult.success();
    }

    /**
     * 评论修改
     *
     * @return Result<String>
     */
    @Override
    public Result<Void> update(Evaluations evaluations) {
        // TODO 点赞需要做通知
        evaluationsMapper.update(evaluations);
        return ApiResult.success();
    }

    @Override
    public Result<Map<String, Object>> avgRating(Integer userId) {
        Map<String, Object> result = evaluationsMapper.avgRating(userId);
        return ApiResult.success(result);
    }
}
