package com.hxtscjk.lipcorn.robot.listen;

import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;

@Beans
public class MessageListener {

    /**
     * 私聊消息（用来测试的）
     * @param privateMsg
     * @param sender
     */
    @OnPrivate
    public void privateMsg(PrivateMsg privateMsg, MsgSender sender) {
        //私聊消息复读
        sender.SENDER.sendPrivateMsg(privateMsg, privateMsg.getMsgContent());
    }
}
