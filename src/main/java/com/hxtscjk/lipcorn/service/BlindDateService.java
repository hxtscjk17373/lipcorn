package com.hxtscjk.lipcorn.service;

import com.hxtscjk.lipcorn.LipcornConst;
import com.hxtscjk.lipcorn.Util.MessageUtil;
import com.hxtscjk.lipcorn.bean.BlindDateBean;
import com.hxtscjk.lipcorn.bean.BlindDateResult;
import com.hxtscjk.lipcorn.bean.MemberNickBean;
import com.hxtscjk.lipcorn.mapper.BlindDateMapper;
import com.hxtscjk.lipcorn.mapper.MemberNickMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class BlindDateService implements LipcornConst {

    @Autowired
    private BlindDateMapper blindDateMapper;

    @Autowired
    private MemberNickMapper memberNickMapper;

    @Autowired
    private BlindDateResultService blindDateResultService;

    //QQ号-昵称
    private final Map<String, String> nickMap = new HashMap<>();

    //昵称-QQ号
    private final Map<String, String> nickReverseMap = new HashMap<>();

    /**
     * 启动时导入一次QQ号和昵称列表
     */
    @PostConstruct
    public void getAllNick() {
        nickMap.clear();
        nickReverseMap.clear();
        List<MemberNickBean> nickList = memberNickMapper.selectAll();
        if (!CollectionUtils.isEmpty(nickList)) {
            for (MemberNickBean memberNick : nickList) {
                //TODO: nickname后续需要修改为逗号分隔的字符串，此处需获取mainNick
                nickMap.put(memberNick.getQqNumber(), memberNick.getNickName());
                nickReverseMap.put(memberNick.getNickName(), memberNick.getQqNumber());
            }
        }
    }

    /**
     * 相亲模块所有功能选项
     * @param qqNumber
     * @param playerInput
     * @return
     */
    public List<String> switchForOptions(String qqNumber, String playerInput) {
        if (playerInput.equals("开始分配") && qqNumber.equals("540248302")) {
            //开始分配时再导入一次
            getAllNick();
            return getCoupleInf();
        }
        switch (playerInput) {
            case "菜单": {
                List<String> list = new ArrayList<>();
                list.add("1.游戏说明");
                list.add("2.个人信息");
                list.add("3.报名格式");
                list.add("4.个人CP历史");
                list.add("注：选项前都要加上相亲二字，用空格分隔。例[相亲 游戏说明]");
                return MessageUtil.stringToList(String.join("\n", list));
            }
            case "游戏说明": {
                List<String> list = new ArrayList<>();
                list.add("相亲游戏说明");
                list.add("    若您参与本游戏，在游戏的每个周期开始时，将根据您设置的个人信息随机分配一名CP。在此周期内，请对TA负责。");
                list.add("    参加游戏：请发送[相亲 报名格式]获取报名格式模板");
                list.add("    请谨慎填写个人信息，个人信息修改需联系主人");
                list.add("    请谨慎选择是否服从调剂，若恰好无合适CP分配，您将solo！");
                return MessageUtil.stringToList(String.join("\n", list));
            }
            case "报名格式": {
                List<String> list = new ArrayList<>();
                list.add("报名格式");
                list.add("相亲 性别+取向+是否服从调剂");
                list.add("性别：男/女");
                list.add("取向：男/女/双");
                list.add("是否服从调剂：是/否");
                list.add("示例：[相亲 男+女+是]");
                return MessageUtil.stringToList(String.join( "\n", list));
            }
            case "个人信息": {
                List<String> list = new ArrayList<>();
                BlindDateBean blindDateBean = new BlindDateBean();
                blindDateBean.setQqNumber(qqNumber);
                blindDateBean = blindDateMapper.selectOne(blindDateBean);
                if (blindDateBean == null) {
                    list.add("没有您的信息");
                } else {
                    list.add("QQ号:" + blindDateBean.getQqNumber());
                    list.add("性别：" + blindDateBean.getSex());
                    list.add("取向：" + blindDateBean.getOrientation());
                    list.add("是否服从调剂：" + blindDateBean.getObeyAdjust());
                }
                return MessageUtil.stringToList(String.join("\n", list));
            }
            case "个人CP历史": {
                List<String> list = new ArrayList<>();
                List<BlindDateResult> resultList = blindDateResultService.queryMemberCPRecord(qqNumber);
                if (CollectionUtils.isEmpty(resultList)) {
                    list.add("没有记录哦TAT");
                } else {
                    list.add(nickMap.get(qqNumber) + "共参与" + resultList.size() + "轮游戏，CP记录如下：");
                    for (BlindDateResult result : resultList) {
                        StringBuilder perRecord = new StringBuilder();
                        perRecord.append(result.getMatchDate()).append("：");
                        if (result.getMatchType().equals(MATCH_SOLO)) {
                            perRecord.append("solo");
                        } else {
                            perRecord.append(nickMap.get(result.getMatchNumber()));
                        }
                        list.add(perRecord.toString());
                    }
                }
                return MessageUtil.stringToList(String.join("\n", list));
            }
            default:
                return MessageUtil.stringToList(insertGameInformation(qqNumber, playerInput));
        }
    }

    /**
     * 玩家报名，格式【性别+取向+调剂选项】
     * @param qqnumber
     * @param playerInput
     * @return
     */
    public String insertGameInformation(String qqnumber, String playerInput) {
        //排除掉不包含+号和+号位置不对的
        if (!playerInput.contains("+") || playerInput.contains("++") || playerInput.startsWith("+") || playerInput.endsWith("+")) {
            return "输入错误！";
        }
        else {
            String strTmp = playerInput.replaceAll("\\+", "");
            int i = playerInput.length() - strTmp.length();
            //排除+号不是2个的
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

    public List<String> getCoupleInf() {
        List <BlindDateBean> list = blindDateMapper.selectAll();
        List <List<BlindDateBean>> successList = new ArrayList<>();//成功列表
        List <List<BlindDateBean>> adjustList = new ArrayList<>();//调剂列表
        List <BlindDateBean> waitList = new ArrayList<>();//等待列表
        List <BlindDateBean> failList = new ArrayList<>();//失败列表
        //打乱list
        Collections.shuffle(list);
        while (!CollectionUtils.isEmpty(list)) {
            BlindDateBean member = list.get(0);
            list.remove(member);
            //取得第一位成员后将其去掉
            boolean found = false;
            for (BlindDateBean chosen : list) {
                if (isPerfectMatching(member, chosen)) {
                    List<BlindDateBean> matchCouple = new ArrayList<>();
                    matchCouple.add(member);
                    matchCouple.add(chosen);
                    successList.add(matchCouple);
                    list.remove(chosen);
                    found = true;
                    break;
                }
            }
            if (!found) {
                //如果匹配失败，放入失败队列等待调剂
                waitList.add(member);
            }
        }
        //此处结束后，得出匹配成功列表，开始进行调剂
        while (!CollectionUtils.isEmpty(waitList)) {
            BlindDateBean member = waitList.get(0);
            waitList.remove(member);
            //取得第一位成员后将其去掉
            boolean found = false;
            for (BlindDateBean chosen : waitList) {
                if (isPerfectMatching(member, chosen) || isAdjustMatching(member, chosen)) {
                    List<BlindDateBean> matchCouple = new ArrayList<>();
                    matchCouple.add(member);
                    matchCouple.add(chosen);
                    adjustList.add(matchCouple);
                    waitList.remove(chosen);
                    found = true;
                    break;
                }
            }
            if (!found) {
                //如果调剂失败，进入solo列表
                failList.add(member);
            }
        }
        return printCoupleInf(successList, adjustList, failList);
    }

    /**
     * 拼接匹配结果
     * @param successList
     * @param adjustList
     * @param failList
     * @return
     */
    private List<String> printCoupleInf(List<List<BlindDateBean>> successList, List<List<BlindDateBean>> adjustList, List<BlindDateBean> failList) {
        List<String> printStr = new ArrayList<>();
        printStr.add("配对结果如下：");
        printStr.add("------");
        printStr.add("匹配成功：");
        getPrintStrList(printStr, successList, MATCH_SUCCESS);
        printStr.add("------");
        printStr.add("调剂成功：");
        getPrintStrList(printStr, adjustList, MATCH_ADJUST_SUCCESS);
        printStr.add("------");
        printStr.add("solo：");
        getPrintStr(printStr, failList, MATCH_SOLO);
        printStr.add("------");
        return MessageUtil.stringToList(String.join( "\n", printStr));
    }

    /**
     * 向匹配结果中插入每一条匹配数据、插入结果库
     * 匹配成功部分
     * @param printStr
     * @param matchList
     * @param type
     */
    private void getPrintStrList(List<String> printStr, List<List<BlindDateBean>> matchList, String type) {
        if (!CollectionUtils.isEmpty(matchList)) {
            for(List<BlindDateBean> beanList : matchList) {
                if (!CollectionUtils.isEmpty(beanList) && beanList.size() == 2) {
                    BlindDateBean member1 = beanList.get(0);
                    BlindDateBean member2 = beanList.get(1);
                    String matchStr = nickMap.get(member1.getQqNumber()) + "(" + member1.getSex() + ")" + "&" + nickMap.get(member2.getQqNumber()) + "(" + member2.getSex() + ")";
                    printStr.add(matchStr);
                    blindDateResultService.insertResultList(member1, member2, type);
                    blindDateResultService.insertResultList(member2, member1, type);
                }
            }
        }
        else {
            printStr.add("无");
        }
    }

    /**
     * 向匹配结果中插入每一条匹配数据、插入结果库
     * 匹配失败部分
     * @param printStr
     * @param soloList
     * @param type
     */
    private void getPrintStr(List<String> printStr, List<BlindDateBean> soloList, String type) {
        if (!CollectionUtils.isEmpty(soloList)) {
            for(BlindDateBean bean : soloList) {
                printStr.add(nickMap.get(bean.getQqNumber()));
                blindDateResultService.insertResultList(bean, null, type);
            }
        }
        else {
            printStr.add("无");
        }
    }

    /**
     * 完美匹配，双方均符合对方要求
     * 正反两个单侧匹配均成功，构成完美匹配
     * @param member
     * @param chosen
     * @return
     */
    private Boolean isPerfectMatching(BlindDateBean member, BlindDateBean chosen) {
        return isSingleMatching(chosen.getOrientation(), member.getSex()) && isSingleMatching(member.getOrientation(), chosen.getSex());
    }

    /**
     * 调剂匹配，双方不都符合对方要求，但都接受调剂
     * @param member
     * @param chosen
     * @return
     */
    private Boolean isAdjustMatching(BlindDateBean member, BlindDateBean chosen) {
        return member.getObeyAdjust().equals("是") && chosen.getObeyAdjust().equals("是");
    }

    /**
     * 单侧匹配，一方符合另一方要求
     * @param orientation
     * @param sex
     * @return
     */
    private Boolean isSingleMatching(String orientation, String sex) {
        if (orientation.equals("双")) {
            return true;
        }
        else return orientation.equals(sex);
    }

    /**
     * id随机
     * @return
     */
    private static String getUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
