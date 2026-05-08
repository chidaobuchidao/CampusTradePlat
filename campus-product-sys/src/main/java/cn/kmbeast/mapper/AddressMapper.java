package cn.kmbeast.mapper;

import cn.kmbeast.pojo.entity.Address;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AddressMapper {

    int save(Address address);

    int update(Address address);

    int deleteById(Integer id);

    List<Address> findByUserId(Integer userId);

    Address getById(Integer id);

    int clearDefault(Integer userId);

    int setDefault(@Param("id") Integer id, @Param("userId") Integer userId);
}
