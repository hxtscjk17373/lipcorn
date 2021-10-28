package com.hxtscjk.lipcorn.service;

import cn.hutool.core.collection.ListUtil;
import com.hxtscjk.lipcorn.Util.MessageUtil;
import com.hxtscjk.lipcorn.bean.BlindDateBean;
import com.hxtscjk.lipcorn.mapper.BlindDateMapper;
import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.Listener;
import love.forte.simbot.annotation.OnGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.IdGenerator;

import java.util.*;

@Service
public class BlindDateService {

    @Autowired
    private BlindDateMapper blindDateMapper;

    public List<String> switchForOptions(String qqNumber, String playerInput) {
        if (playerInput.equals("菜单")) {
            List<String> list = new ArrayList<>();
            list.add("1.游戏说明");
            list.add("2.个人信息");
            list.add("3.报名格式");
            list.add("注：选项前都要加上相亲二字，用空格分隔。例[相亲 游戏说明]");
            return MessageUtil.stringToList(String.join( "\n", list));
        }
        if (playerInput.equals("游戏说明")) {
            List<String> list = new ArrayList<>();
            list.add("相亲游戏说明");
            list.add("    若您参与本游戏，在游戏的每个周期开始时，将根据您设置的个人信息随机分配一名CP。在此周期内，请对TA负责。");
            list.add("    参加游戏：请发送[相亲 报名格式]获取报名格式模板");
            list.add("    请谨慎填写个人信息，个人信息修改需联系主人");
            list.add("    请谨慎选择是否服从调剂，若恰好无合适CP分配，您将solo！");
            return MessageUtil.stringToList(String.join( "\n", list));
        }
        if (playerInput.equals("个人信息")) {
            List<String> list = new ArrayList<>();
            BlindDateBean blindDateBean = new BlindDateBean();
            blindDateBean.setQqNumber(qqNumber);
            blindDateBean = blindDateMapper.selectOne(blindDateBean);
            if (blindDateBean == null) {
                list.add("没有您的信息");
            }
            else {
                list.add("QQ号:" + blindDateBean.getQqNumber());
                list.add("性别：" + blindDateBean.getSex());
                list.add("取向：" + blindDateBean.getOrientation());
                list.add("是否服从调剂：" + blindDateBean.getObeyAdjust());
            }
            return MessageUtil.stringToList(String.join( "\n", list));
        }
        if (playerInput.equals("报名格式")) {
            List<String> list = new ArrayList<>();
            list.add("报名格式");
            list.add("相亲 性别+取向+是否服从调剂");
            list.add("性别：男/女");
            list.add("取向：男/女/双");
            list.add("是否服从调剂：是/否");
            list.add("示例：[相亲 男+女+是]");
            return MessageUtil.stringToList(String.join( "\n", list));
        }
        else {
            return MessageUtil.stringToList(insertGameInformation(qqNumber, playerInput));
        }
    }

    public String insertGameInformation(String qqnumber, String playerInput) {
        if (!playerInput.contains("+") || playerInput.contains("++") || playerInput.startsWith("+") || playerInput.endsWith("+")) {
            return "输入错误！";
        }
        else {
            String strTmp = playerInput.replaceAll("\\+", "");
            int i = playerInput.length() - strTmp.length();
            if (i != 2) {
                return "输入错误！";
            }
        }
        List<String> list = new ArrayList<>(Arrays.asList(playerInput.split("\\+")));
        BlindDateBean blindDateBean = new BlindDateBean();
        blindDateBean.setQqNumber(qqnumber);
        if (!list.get(0).equals("男") && !list.get(0).equals("女")) {
            return "性别输入错误！请输入男/女";
        }
        blindDateBean.setSex(list.get(0));
        if (!list.get(1).equals("男") && !list.get(1).equals("女") && !list.get(1).equals("双")) {
            return "取向输入错误！请输入男/女/双";
        }
        blindDateBean.setOrientation(list.get(1));
        if (!list.get(2).equals("是") && !list.get(2).equals("否")) {
            return "调剂选项输入错误！请输入是/否";
        }
        blindDateBean.setObeyAdjust(list.get(2));
        blindDateBean.setCreateTime(new Date());
        blindDateBean.setUpdateTime(new Date());
        blindDateBean.setDeleted("0");
        blindDateBean.setId(getUuid());
        BlindDateBean oldinf = new BlindDateBean();
        oldinf.setQqNumber(qqnumber);
        oldinf = blindDateMapper.selectOne(oldinf);
        if (oldinf != null) {
            return "只能报名一次哦~";
        }
        blindDateMapper.insert(blindDateBean);
        return "耶！成功辣！";
    }

    private static String getUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
