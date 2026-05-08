package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.entity.Interaction;

import java.util.List;

/**
 * 用户行为业务逻辑接口
 */
public interface InteractionService {

    Result<String> toggleSave(Integer productId);

    Result<String> recordView(Integer productId);

    Result<List<Interaction>> getMySave(Integer current, Integer size);

    Result<List<Interaction>> getMyView(Integer current, Integer size);
}
