package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    //微信服务接口
    public static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;

    @Override
    public User weixinLogin(UserLoginDTO userLoginDTO) {
        String openid = getOpenid(userLoginDTO.getCode());
        // 2.判断openid是否为空
        if(openid == null) {
            throw new RuntimeException(MessageConstant.LOGIN_FAILED);
        }
        // 3.根据openid查询用户是否存在
        User user = userMapper.getUserByOpenid(openid);
        // 4.判断用户是否存在
        if(user == null) {
            // 4.1不存在，创建新用户
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        // 5.返回用户信息
        return user;
    }
        /**
         * 获取openid
         * @return
         */
    private String getOpenid(String code) {
        Map<String,String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String openid = HttpClientUtil.doGet(WX_LOGIN_URL,map);
        JSONObject jsonObject = JSONObject.parseObject(openid);
        return jsonObject.getString("openid");
    }
}
