package com.atyichen.yiojbackenduserservice.controller;

import com.atyichen.yiojbackendmodel.model.entity.User;
import com.atyichen.yiojbackenduserservice.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 给微服务内部提供的接口inner
 */
@RestController
@RequestMapping("/inner")
public class UserInnerController {
    @Resource
    private UserService userService;

    // /api/user/inner/get/id/api/user/inner
    @GetMapping("/get/id")
    public User getById(@RequestParam("userId") long userId) {
        return userService.getById(userId);
    }

    @GetMapping("/get/ids")
    public List<User> listByIds(@RequestParam("idList") Collection<Long> idList) {
        return userService.listByIds(idList);
    }
}
