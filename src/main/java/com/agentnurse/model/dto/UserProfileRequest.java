package com.agentnurse.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户资料更新请求
 */
@Data
public class UserProfileRequest {

    @Email(message = "邮箱格式不正确")
    private String email;

    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String realName;

    @Size(max = 20, message = "手机号长度不能超过20个字符")
    private String phone;

    private String gender;

    private String birthDate;

    @Size(max = 200, message = "地址长度不能超过200个字符")
    private String address;

    @Size(max = 500, message = "头像URL长度不能超过500个字符")
    private String avatarUrl;
}
