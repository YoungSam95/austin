package com.java3y.austin.support.config;

import com.java3y.austin.common.dto.account.WeChatOfficialAccount;
import com.java3y.austin.support.utils.WxServiceUtils;
import lombok.Data;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;


/**
 * 使用微信服务号作为登录的媒介
 * (测试环境 && 开启了配置才使用)
 *
 * @author 3y
 */
@Profile("test")
@Configuration
@ConditionalOnProperty(name = "austin.login.officialAccount.enable", havingValue = "true")
@Data
public class WeChatLoginAccountConfig {

    @Value("${austin.login.official.account.appId}")
    private String appId;
    @Value("${austin.login.official.account.secret}")
    private String secret;
    @Value("${austin.login.official.account.secret}")
    private String token;

    @Autowired
    private WxServiceUtils wxServiceUtils;

    private WxMpService officialAccountLoginService;
    private WxMpDefaultConfigImpl config;
    private WxMpMessageRouter wxMpMessageRouter;


    @PostConstruct
    private void init() {
        WeChatOfficialAccount account = WeChatOfficialAccount.builder().appId(appId).secret(secret).token(token).build();
        officialAccountLoginService = wxServiceUtils.initOfficialAccountService(account);
        initConfig();
        initRouter();
    }

    /**
     * 初始化路由器
     */
    private void initRouter() {
        wxMpMessageRouter = new WxMpMessageRouter(officialAccountLoginService);
        wxMpMessageRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).event(WxConsts.EventType.SUBSCRIBE).handler(null).end();
        wxMpMessageRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).event(WxConsts.EventType.UNSUBSCRIBE).handler(null).end();
    }

    /**
     * 初始化配置信息
     */
    private void initConfig() {
        config.setAppId(appId);
        config.setToken(token);
        config.setSecret(secret);
    }

}
