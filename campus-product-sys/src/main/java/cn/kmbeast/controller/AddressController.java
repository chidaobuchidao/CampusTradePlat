package cn.kmbeast.controller;

import cn.kmbeast.aop.Protector;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.entity.Address;
import cn.kmbeast.service.AddressService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Resource
    private AddressService addressService;

    @Protector
    @GetMapping("/list")
    public Result<List<Address>> list() {
        return addressService.list();
    }

    @Protector
    @PostMapping("/save")
    public Result<String> save(@RequestBody Address address) {
        return addressService.save(address);
    }

    @Protector
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        return addressService.delete(id);
    }

    @Protector
    @PutMapping("/default/{id}")
    public Result<String> setDefault(@PathVariable Integer id) {
        return addressService.setDefault(id);
    }
}
