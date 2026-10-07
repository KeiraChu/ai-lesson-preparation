package com.ai.service.impl;

import com.ai.context.UserContext;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.ai.constant.CommonConstant;
import com.ai.constant.JwtClaimsConstant;
import com.ai.constant.MessageConstant;
import com.ai.entity.User;
import com.ai.mapper.UserMapper;
import com.ai.properties.JwtProperties;
import com.ai.result.Result;
import com.ai.service.UserService;
import com.ai.utils.IdWorker;
import com.ai.utils.JwtUtil;
import com.ai.vo.reqvo.LoginReqVo;
import com.ai.vo.respvo.LoginRespVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ：褚婧雯
 * @date ：2025/3/25 13:57
 * @description ：用户相关接口实现
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Autowired
    private UserMapper userMapper;


    /**
     * 分布式环境保证生成的id唯一
     */
    @Autowired
    private IdWorker idWorker;


    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 用户注册
     * @param loginReqVo
     * @return
     */
    @Override
    public Result register(LoginReqVo loginReqVo) {
        log.info("用户注册请求，用户名：{}", loginReqVo == null ? null : loginReqVo.getUsername());
        // 检验参数合法性
        if (loginReqVo == null || StringUtils.isBlank(loginReqVo.getUsername()) || StringUtils.isBlank(loginReqVo.getPassword())) {
            return Result.error(MessageConstant.REGISTER_FAILED);
        }

        //校验验证码和sessionid是否有效
        if (StringUtils.isBlank(loginReqVo.getCode()) || StringUtils.isBlank(loginReqVo.getSessionId())) {
            return Result.error(MessageConstant.DATA_ERROR);
        }
        // 根据key从redis中获取缓存的校验码
        String rcode = (String) redisTemplate.opsForValue().get(CommonConstant.CHECK_PREFIX + loginReqVo.getSessionId());
        // 判断获取的验证码是否存在，以及是否与输入的验证码相同
        if (StringUtils.isBlank(rcode) || !rcode.equalsIgnoreCase(loginReqVo.getCode())) {
            return Result.error(MessageConstant.CODE_ERROR);
        }
        //根据用户名查询数据库
        User dbUser = userMapper.getUser(loginReqVo.getUsername());
        if (dbUser != null) {
            return Result.error(MessageConstant.ALREADY_EXISTS);
        }

        User user = User.builder()
                .id(idWorker.nextId())
                .username(loginReqVo.getUsername())
                .password(passwordEncoder.encode(loginReqVo.getPassword()))
                .createTime(LocalDateTime.now().toDate())
                .build();
        userMapper.insert(user);
        redisTemplate.delete(CommonConstant.CHECK_PREFIX + loginReqVo.getSessionId());

        return Result.success(MessageConstant.REGISTER_SUCCESS);
    }
    /**
     * 登录校验码生成服务方法
     *
     * @return
     */
    @Override
    public Result<Map> getCaptchaCode() {
        //参数分别是宽、高、验证码长度、干扰线数量
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(250, 40, 4, 5);
        //设置背景颜色清灰
        captcha.setBackground(Color.lightGray);
        //自定义校验码生成方式
//        captcha.setGenerator(new CodeGenerator() {
//            @Override
//         tCode) {
//               public String generate() {
////                return RandomStringUtils.randomNumeric(4);
////            }
////            @Override
////            public boolean verify(String code, String userInpu    return code.equalsIgnoreCase(userInputCode);
//            }
//        });
        //获取图片中的验证码，默认生成的校验码包含文字和数字，长度为4
        String checkCode = captcha.getCode();
        //生成sessionId
        String sessionId = String.valueOf(idWorker.nextId());
        //将sessionId和校验码保存在redis下，并设置缓存中数据存活时间一分钟
        redisTemplate.opsForValue().set(CommonConstant.CHECK_PREFIX + sessionId, checkCode, 3, TimeUnit.MINUTES);
        //组装响应数据
        HashMap<String, String> info = new HashMap<>();
        info.put("sessionId", sessionId);
        info.put("imageData", captcha.getImageBase64());//获取base64格式的图片数据
        //设置响应数据格式
        return Result.success(info);
    }

    /**
     * 用户登录
     * @param loginReqVo
     * @return
     */
    @Override
    public Result<LoginRespVo> login(LoginReqVo loginReqVo) {
        log.info("用户登录请求，用户名：{}", loginReqVo == null ? null : loginReqVo.getUsername());
        // 检验参数合法性
        if (loginReqVo == null || StringUtils.isBlank(loginReqVo.getUsername()) || StringUtils.isBlank(loginReqVo.getPassword())) {
            return Result.error(MessageConstant.LOGIN_FAILED);
        }

        //校验验证码和sessionid是否有效
        if (StringUtils.isBlank(loginReqVo.getCode()) || StringUtils.isBlank(loginReqVo.getSessionId())) {
            return Result.error(MessageConstant.DATA_ERROR);
        }
        // 根据key从redis中获取缓存的校验码
        String rcode = (String) redisTemplate.opsForValue().get(CommonConstant.CHECK_PREFIX + loginReqVo.getSessionId());
        // 判断获取的验证码是否存在，以及是否与输入的验证码相同
        if (StringUtils.isBlank(rcode) || !rcode.equalsIgnoreCase(loginReqVo.getCode())) {
            return Result.error(MessageConstant.CODE_ERROR);
        }
        //根据用户名查询数据库
        User dbUser = userMapper.getUser(loginReqVo.getUsername());
        if (dbUser == null) {
            // 账号不存在
//            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
            return Result.error(MessageConstant.ACCOUNT_NOT_FOUND);
        }
//        if (!dbUser.getPassword().equals(user.getPassword())) {
//            // 密码错误
////            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
//            return Result.error(MessageConstant.PASSWORD_ERROR);
//        }
        //调用密码匹配器比对
        if (!passwordEncoder.matches(loginReqVo.getPassword(), dbUser.getPassword())) {
            //密码错误
            return Result.error(MessageConstant.PASSWORD_ERROR);
        }
        redisTemplate.delete(CommonConstant.CHECK_PREFIX + loginReqVo.getSessionId());

        //登录成功后，生成jwt令牌
        HashMap<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USERNAME, dbUser.getUsername());
        claims.put(JwtClaimsConstant.USER_ID, dbUser.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        LoginRespVo loginRespVo = LoginRespVo.builder()
                .id(dbUser.getId())
                .username(dbUser.getUsername())
                .token(token).build();

        return Result.success(loginRespVo);
    }

    /**
     * 查询全部用户
     * @return
     */
    @Override
    public List<User> getAllUser() {
        return userMapper.getAllUser();
    }

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    @Override
    public Result updateInfo(User user) {
        if (user == null) {
            return Result.error("用户信息不能为空");
        }
        User dbUser = userMapper.selectById(UserContext.getUserId());
        if (dbUser == null) {
            return Result.error("用户不存在");
        }
        dbUser.setUpdateTime(LocalDateTime.now().toDate());
        dbUser.setPhone(user.getPhone());
        dbUser.setRealName(user.getRealName());
        dbUser.setNickName(user.getNickName());
        dbUser.setEmail(user.getEmail());
        dbUser.setSex(user.getSex());
        userMapper.updateById(dbUser);
        return Result.success("修改成功");
    }

    /**
     * 修改密码
     * @param info
     * @return
     */
    @Override
    public Result updatePassword(HashMap<String, String> info) {
        User dbUser = userMapper.selectById(UserContext.getUserId());
        if (dbUser == null) {
            return Result.error("用户不存在");
        }

        if (info == null || StringUtils.isBlank(info.get("oldPassword")) || StringUtils.isBlank(info.get("newPassword"))) {
            return Result.error("原密码和新密码不能为空");
        }
        String oldPassword = info.get("oldPassword");
        //调用密码匹配器比对
        if (!passwordEncoder.matches(oldPassword, dbUser.getPassword())) {
            //密码错误
            return Result.error(MessageConstant.OLD_PASSWORD_ERROR);
        }

        String newPassword = info.get("newPassword");
        dbUser.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(dbUser);
        return Result.success("修改成功");
    }

}
