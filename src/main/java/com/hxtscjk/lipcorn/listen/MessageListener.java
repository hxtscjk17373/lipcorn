package com.hxtscjk.lipcorn.listen;

import cn.hutool.core.collection.CollectionUtil;
import com.hxtscjk.lipcorn.LipcornConst;
import com.hxtscjk.lipcorn.Util.CatcodeUtil;
import com.hxtscjk.lipcorn.Util.MessageUtil;
import com.hxtscjk.lipcorn.service.BlindDateService;
import com.hxtscjk.lipcorn.service.CatchFishService;
import com.hxtscjk.lipcorn.service.MemberCommonService;
import lombok.extern.slf4j.Slf4j;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import love.forte.simbot.api.sender.MsgSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Beans
@Component
@Slf4j
public class MessageListener implements LipcornConst {

    @Autowired
    private BlindDateService blindDateService;

    @Autowired
    private MemberCommonService memberCommonService;

    @Autowired
    private CatchFishService catchFishService;

    @OnGroup
    @Filter(groups = {GROUP_YELLOW_NUMBER, GROUP_TEST_NUMBER})
    public void GroupMsg(GroupMsg groupMsg, MsgSender sender) {
        log.info("received message : {}", groupMsg.getMsg());
        String msg = groupMsg.getMsg();
        MessageUtil messageUtil = new MessageUtil();
        int messageType = messageUtil.checkMessage(msg);
        if (messageType == 1) {
            if (msg.equals("小爆米草")) {
                sendMessage(CatcodeUtil.getFace(111), groupMsg, sender);
            }
            if (msg.equals("随机群员")) {
                sendMessage(memberCommonService.randMembers("1", groupMsg.getAccountInfo().getAccountCode()), groupMsg, sender);
            }
        } else if (messageType == 2 || messageType == 3) {
            String commandType = messageUtil.getCommandType(msg);
            String commandText = messageUtil.getCommandText(msg);
            String commandValue = messageUtil.getCommandValue(msg);
            if (commandType.equals("相亲")) {
                sendMessage(blindDateService.switchForOptions(groupMsg.getAccountInfo().getAccountCode(), commandText, commandValue), groupMsg, sender);
            }
            if (commandType.equals("随机群员")) {
                sendMessage(memberCommonService.randMembers(commandText, groupMsg.getAccountInfo().getAccountCode()), groupMsg, sender);
            }
            if (commandType.equals("填空")) {
                sendMessage(memberCommonService.fillBlankMember(commandText, groupMsg.getAccountInfo().getAccountCode()), groupMsg, sender);
            }
            if (commandType.equals("身份")) {
                sendMessage(memberCommonService.queryMemberByQq(commandText), groupMsg, sender);
            }
        }
    }

    @OnGroup
    @Filter(groups = {GROUP_CATCH_FISH_NUMBER, GROUP_TEST_NUMBER})
    public void GroupMsg2(GroupMsg groupMsg, MsgSender sender) throws InterruptedException {
        log.info("received message : {}", groupMsg.getMsg());
        String msg = groupMsg.getMsg();
        MessageUtil messageUtil = new MessageUtil();
        int messageType = messageUtil.checkMessage(msg);
        if (messageType == 1) {
            if (msg.equals("捞鱼")) {
                List<String> happyMemberId = getHappyMember(sender);
                List<String> infList = catchFishService.analyseNews(happyMemberId);
                sendMessage(infList, groupMsg, sender);
            }
            if (msg.equals("全部发送")) {
                sendMessage("开始发送，发送耗时较长，请耐心等待，在发送成功提示出现前请勿发送指令", groupMsg, sender);
                sendMessage(catchFishService.sendCatchFishInf(), groupMsg, sender);
            }
            if (msg.equals("全部法师")) {
                sendMessage("假装打错字是吧，哼哼，被我发现了！", groupMsg, sender);
            }
            if (msg.equals("菜单")) {
                sendMessage(catchFishService.getMenu(), groupMsg, sender);
            }
        } else if (messageType == 2 || messageType == 3) {
            String commandType = messageUtil.getCommandType(msg);
            String commandText = messageUtil.getCommandText(msg);
            String commandValue = messageUtil.getCommandValue(msg);
            //测试内容是否已分离出
            if (commandType.equals("删除")) {
                sendMessage(catchFishService.insertBlackList(commandText), groupMsg, sender);
            }
            if (commandType.equals("捞鱼")) {
                sendMessage(catchFishService.insertCatchList(commandText), groupMsg, sender);
            }
            if (commandType.equals("强制捞鱼")) {
                sendMessage(catchFishService.insertCatchListForce(commandText), groupMsg, sender);
            }
            if (commandType.equals("状态")) {
                sendMessage(catchFishService.getFishStatus(commandText), groupMsg, sender);
            }
        }
    }

    @OnGroup
    @Filter(groups = {GROUP_HAPPY_NUMBER, GROUP_TEST_NUMBER})
    public void GroupMsg3(GroupMsg groupMsg, MsgSender sender) throws InterruptedException {
        log.info("received message : {}", groupMsg.getMsg());
        String msg = groupMsg.getMsg();
        MessageUtil messageUtil = new MessageUtil();
        int messageType = messageUtil.checkMessage(msg);
        if (messageType == 1) {
            String commandText = messageUtil.getCommandType(msg);
            if (commandText.contains("http://www.saolei.wang/Player/Index.asp?Id=12689(群员)")) {
                sendMessage("我要求把群员改成群主", groupMsg, sender);
            }
        }
        if (messageType == 2 || messageType == 3) {
            String commandType = messageUtil.getCommandType(msg);
            String commandText = messageUtil.getCommandText(msg);
            String commandValue = messageUtil.getCommandValue(msg);
            //测试内容是否已分离出
            if (commandType.equals("QQ号") || commandType.equals("qq号")) {
                sendMessage(getHappyMemberQqNumber(commandText, sender), groupMsg, sender);
            }
        }
    }

    public List<String> getHappyMemberQqNumber(String id, MsgSender sender) {
        List<GroupMemberInfo> memberList = sender.GETTER.getGroupMemberList("585808410").getResults().stream().filter(v -> v.getAccountTitle().equals(id)).collect(Collectors.toList());
        List<String> result = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(memberList)) {
            result.add("本群中id=" + id + "的群成员QQ号如下：");
            for (GroupMemberInfo info : memberList) {
                result.add(info.getAccountCode());
            }
        } else {
            result.add("未查询到");
        }
        return MessageUtil.stringToList(String.join("\n", result));
    }

    private List<String> getHappyMember(MsgSender sender) {
        List<String> memberList = sender.GETTER.getGroupMemberList("585808410").getResults().stream().map(GroupMemberInfo::getAccountTitle).collect(Collectors.toList());
        return memberList;
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
            String image = CatcodeUtil.getImage("http://saolei.wang/Models/Images/Player/18290.jpg");
            sendMessage(image, groupMsg, sender);
        }

        if (groupMsg.getMsg().equals("go")) {
            String image = CatcodeUtil.getImage("D:\\工程文件\\hdfsdemo\\src\\main\\resources\\zz.gif");
            sendMessage(image, groupMsg, sender);
        }

        if (groupMsg.getMsg().equals("测试图片2")) {
            String image = CatcodeUtil.getImage("D:\\工程文件\\hdfsdemo\\src\\main\\resources\\dd.gif");
            sendMessage(image, groupMsg, sender);
        }
    }

    private void sendMessage(String message, GroupMsg groupMsg, MsgSender sender) {
        if (StringUtil.isNotEmpty(message)) {
            sender.SENDER.sendGroupMsg(groupMsg, message);
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
