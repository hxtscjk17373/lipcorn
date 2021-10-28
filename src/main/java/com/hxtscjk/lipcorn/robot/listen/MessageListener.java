package com.hxtscjk.lipcorn.robot.listen;

import catcode.CatCodeUtil;
import catcode.CodeBuilder;
import catcode.CodeTemplate;
import cn.hutool.core.collection.CollectionUtil;
import com.hxtscjk.lipcorn.Util.MessageUtil;
import com.hxtscjk.lipcorn.service.BlindDateService;
import lombok.extern.slf4j.Slf4j;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Listen;
import love.forte.simbot.annotation.Listener;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.api.sender.Sender;
import love.forte.simbot.component.mirai.message.MiraiMemberAccountInfo;
import love.forte.simbot.component.mirai.message.event.MiraiGroupMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;

@Beans
@Component
@Slf4j
public class MessageListener {

    @Autowired
    private BlindDateService blindDateService;

    @OnGroup
    public void GroupMsg(GroupMsg groupMsg, MsgSender sender) {
        log.info("received message : {}", groupMsg.getMsg());
        MessageUtil messageUtil = new MessageUtil();
        int messageType = messageUtil.checkMessage(groupMsg.getMsg());
        if(messageType == 1) {
            String commandType = messageUtil.getCommandType(groupMsg.getMsg());
            String commandText = messageUtil.getCommandText(groupMsg.getMsg());
            //测试内容是否已分离出
            //sender.SENDER.sendGroupMsg(groupMsg, commandText);
            if (commandType.equals("相亲")) {
                sendMessage(blindDateService.switchForOptions(groupMsg.getAccountInfo().getAccountCode(), commandText), groupMsg, sender);
            }
        }
    }

    @OnPrivate
    public void privateMsg(PrivateMsg privateMsg, MsgSender sender) {
        //私聊消息复读
        sender.SENDER.sendPrivateMsg(privateMsg, privateMsg.getMsgContent());
    }

    @OnGroup
    public void testMsg(GroupMsg groupMsg, MsgSender sender) {
        //测试图片发送
        if (groupMsg.getMsg().equals("测试图片")) {
            CatCodeUtil catCodeUtil = CatCodeUtil.INSTANCE;
            CodeTemplate<String> template = catCodeUtil.getStringTemplate();
            String image = template.image("D:\\工程文件\\hdfsdemo\\src\\main\\resources\\zz.jpg");
//            sender.SENDER.sendGroupMsg(groupMsg, "告辞"+image);
//            sender.SENDER.sendGroupMsg(groupMsg, image);
//            sender.SENDER.sendGroupMsg(groupMsg, "2");
        }
    }

    private void sendMessage(List<String> list, GroupMsg groupMsg, MsgSender sender) {
        if (CollectionUtil.isNotEmpty(list)) {
            for (String message : list) {
                sender.SENDER.sendGroupMsg(groupMsg, message);
            }
        }
    }
}
