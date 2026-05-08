package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.InteractionMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.PageResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.entity.Interaction;
import cn.kmbeast.service.InteractionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InteractionServiceImpl implements InteractionService {

    private static final Integer ACTION_SAVE = 1;
    private static final Integer ACTION_VIEW = 2;

    @Resource
    private InteractionMapper interactionMapper;

    @Override
    public Result<String> toggleSave(Integer productId) {
        Integer userId = LocalThreadHolder.getUserId();
        Interaction existing = interactionMapper.getByUserAndProduct(userId, productId, ACTION_SAVE);
        if (existing != null) {
            interactionMapper.delete(userId, productId, ACTION_SAVE);
            return ApiResult.success("取消收藏");
        }
        Interaction interaction = Interaction.builder()
                .userId(userId)
                .productId(productId)
                .actionType(ACTION_SAVE)
                .actionTime(LocalDateTime.now())
                .build();
        interactionMapper.save(interaction);
        return ApiResult.success("收藏成功");
    }

    @Override
    public Result<String> recordView(Integer productId) {
        Integer userId = LocalThreadHolder.getUserId();
        if (userId == null) {
            return ApiResult.success();
        }
        Interaction interaction = Interaction.builder()
                .userId(userId)
                .productId(productId)
                .actionType(ACTION_VIEW)
                .actionTime(LocalDateTime.now())
                .build();
        interactionMapper.save(interaction);
        return ApiResult.success();
    }

    @Override
    public Result<List<Interaction>> getMySave(Integer current, Integer size) {
        Integer userId = LocalThreadHolder.getUserId();
        int total = interactionMapper.queryCount(userId, ACTION_SAVE);
        List<Interaction> list = interactionMapper.query(userId, ACTION_SAVE,
                (current - 1) * size, size);
        return PageResult.success(list, total);
    }

    @Override
    public Result<List<Interaction>> getMyView(Integer current, Integer size) {
        Integer userId = LocalThreadHolder.getUserId();
        int total = interactionMapper.queryCount(userId, ACTION_VIEW);
        List<Interaction> list = interactionMapper.query(userId, ACTION_VIEW,
                (current - 1) * size, size);
        return PageResult.success(list, total);
    }
}
