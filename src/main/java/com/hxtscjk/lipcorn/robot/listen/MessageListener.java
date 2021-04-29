package com.hxtscjk.lipcorn.robot.listen;

import com.hxtscjk.lipcorn.Util.MessageUtil;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.api.sender.Sender;

@Beans
public class MessageListener {

    @OnGroup
    public void GroupMsg(GroupMsg groupMsg, MsgSender sender) {
        MessageUtil messageUtil = new MessageUtil();
        int messageType = messageUtil.checkMessage(groupMsg.getMsg());
        if(messageType == 1) {
            String commandType = messageUtil.getCommandType(groupMsg.getMsg());
            String commandText = messageUtil.getCommandText(groupMsg.getMsg());
            //测试内容是否已分离出
            sender.SENDER.sendGroupMsg(groupMsg, commandText);
        }
    }
    @OnPrivate
    public void privateMsg(PrivateMsg privateMsg, MsgSender sender) {
        //私聊消息复读
        sender.SENDER.sendPrivateMsg(privateMsg, privateMsg.getMsgContent());
    }
}
