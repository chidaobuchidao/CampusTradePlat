package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.AddressMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.entity.Address;
import cn.kmbeast.service.AddressService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Resource
    private AddressMapper addressMapper;

    @Override
    public Result<String> save(Address address) {
        Integer userId = LocalThreadHolder.getUserId();
        address.setUserId(userId);
        if (address.getId() != null) {
            Address existing = addressMapper.getById(address.getId());
            if (existing == null || !existing.getUserId().equals(userId)) {
                return ApiResult.error("地址不存在");
            }
            addressMapper.update(address);
        } else {
            if (address.getIsDefault() == null) address.setIsDefault(0);
            if (address.getIsDefault() == 1) {
                addressMapper.clearDefault(userId);
            }
            addressMapper.save(address);
        }
        return ApiResult.success("保存成功");
    }

    @Override
    public Result<String> delete(Integer id) {
        Integer userId = LocalThreadHolder.getUserId();
        Address existing = addressMapper.getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            return ApiResult.error("地址不存在");
        }
        addressMapper.deleteById(id);
        return ApiResult.success("删除成功");
    }

    @Override
    public Result<List<Address>> list() {
        Integer userId = LocalThreadHolder.getUserId();
        List<Address> addresses = addressMapper.findByUserId(userId);
        return ApiResult.success(addresses);
    }

    @Override
    public Result<String> setDefault(Integer id) {
        Integer userId = LocalThreadHolder.getUserId();
        Address existing = addressMapper.getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            return ApiResult.error("地址不存在");
        }
        addressMapper.clearDefault(userId);
        addressMapper.setDefault(id, userId);
        return ApiResult.success("设置成功");
    }
}
