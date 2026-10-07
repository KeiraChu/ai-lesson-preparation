package com.ai.service;

import com.ai.entity.User;
import com.ai.result.Result;
import com.ai.vo.reqvo.LoginReqVo;
import com.ai.vo.respvo.LoginRespVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：jiang
 * @date ：2025/3/25 13:51
 * @description ：用户相关接口
 */
public interface UserService {
    /**
     * 用户注册
     * @param loginReqVo
     * @return
     */
    Result register(LoginReqVo loginReqVo);

    /**
     * 登录校验码生成服务方法
     * @return
     */
    Result<Map> getCaptchaCode();

    /**
     * 用户登录
     * @param loginReqVo
     * @return
     */
    Result<LoginRespVo> login(LoginReqVo loginReqVo);

    /**
     * 查询全部用户
     * @return
     */
    List<User> getAllUser();

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    Result updateInfo(User user);

    /**
     * 修改密码
     * @param info
     * @return
     */
    Result updatePassword(HashMap<String, String> info);
}
