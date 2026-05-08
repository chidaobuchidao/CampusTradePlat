package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.entity.Address;

import java.util.List;

public interface AddressService {

    Result<String> save(Address address);

    Result<String> delete(Integer id);

    Result<List<Address>> list();

    Result<String> setDefault(Integer id);
}
