package me.shouheng.notepal.data.network.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class UserEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String account;
    private String nickName;
    private String openid;
    private String avatarUrl;
    private String password;
    private Byte gender;
    private String birthday;
    private String phone;
    private String qq;
    private String wechat;            // wechat openid
    private String email;
    private String country;//公司
    private String province;//职务职称
    private String city;//行业
    private String realName;//行业
    private Integer level;            // default 0 level 0 normal user 1 super user 2 system user
    private String token;
    private List<PPT> ppts = new ArrayList<>();
    private Integer pptCount;
}
