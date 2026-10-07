package com.ai.vo.respvo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRespVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String username;
    private String realName;
    private String nickName;
    private String phone;
    private String email;
    private Integer sex;
    private String token;
}
