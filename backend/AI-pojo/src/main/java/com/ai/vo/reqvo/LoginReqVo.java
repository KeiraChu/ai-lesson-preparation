package com.ai.vo.reqvo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginReqVo {
    private String username;
    private String password;
    /**
     * 验证码
     */
    private String code;

    /**
     * 会话ID
     */
    private String sessionId;

}
