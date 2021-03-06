package com.hxtscjk.lipcorn.service;

import com.hxtscjk.lipcorn.Util.MessageUtil;
import com.hxtscjk.lipcorn.bean.FillBlankBean;
import com.hxtscjk.lipcorn.bean.MemberNickBean;
import com.hxtscjk.lipcorn.mapper.FillBlankMapper;
import com.hxtscjk.lipcorn.mapper.MemberNickMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberCommonService {

    @Autowired
    private MemberNickMapper memberNickMapper;

    @Autowired
    private FillBlankMapper fillBlankMapper;

    private boolean checkTodayTimes(String qq) {
        List<MemberNickBean> nicks = memberNickMapper.selectAll().stream().filter(v -> v.getDeleted().equals("0")).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(nicks.stream().filter(v -> v.getQqNumber().equals(qq)).collect(Collectors.toList()))) {
            return false;
        }
        FillBlankBean fillBlankBean = new FillBlankBean();
        fillBlankBean.setQqNumber(qq);
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        fillBlankBean.setDate(date);
        FillBlankBean result = fillBlankMapper.selectOne(fillBlankBean);
        if (result != null) {
            return result.getTimes() < 2;
        }
        return true;
    }

    private void addTodayTimes(String qq) {
        FillBlankBean fillBlankBean = new FillBlankBean();
        fillBlankBean.setQqNumber(qq);
        String datee = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        fillBlankBean.setDate(datee);
        FillBlankBean result = fillBlankMapper.selectOne(fillBlankBean);
        if (result == null) {
            fillBlankBean.setTimes(1);
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            fillBlankBean.setDate(date);
            fillBlankMapper.insert(fillBlankBean);
        }
        else {
            fillBlankBean.setId(result.getId());
            fillBlankBean.setTimes(result.getTimes() + 1);
            fillBlankMapper.updateByPrimaryKeySelective(fillBlankBean);
        }
    }

    public List<String> randMembers(String numStr, String qq) {
        boolean flag = checkTodayTimes(qq);
        if (!flag) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        int num = 0;
        try {
            num = Integer.parseInt(numStr);
        } catch (Exception e) {
            result.add("??????????????????????????????????????? ?????????1-5??????");
            return MessageUtil.stringToList(String.join("\n", result));
        }

        if (num > 5 || num < 1) {
            result.add("????????????????????????1-5??????");
            return MessageUtil.stringToList(String.join("\n", result));
        }
        List<MemberNickBean> memberNicks = memberNickMapper.selectAll().stream().filter(v -> v.getDeleted().equals("0")).collect(Collectors.toList());
        Collections.shuffle(memberNicks);
        for (int i = 0; i < num; i++) {
            result.add(memberNicks.get(i).getNickName());
        }
        addTodayTimes(qq);
        return MessageUtil.stringToList(String.join("\n", result));
    }

    public List<String> fillBlankMember(String inputStr, String qq) {
        inputStr = inputStr.replace("X", "x");
        boolean flag = checkTodayTimes(qq);
        if (!flag) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        if (!inputStr.contains("xx")) {
            result.add("???????????????????????????xx??????????????????");
            return MessageUtil.stringToList(String.join("\n", result));
        }
        else {
            String tempStr = inputStr.replace("xx", "");
            int sub = inputStr.length() - tempStr.length();
            if (sub > 20) {
                result.add("????????????10?????????????????????????????????");
                return MessageUtil.stringToList(String.join("\n", result));
            }
            List<MemberNickBean> memberNicks = memberNickMapper.selectAll().stream().filter(v -> v.getDeleted().equals("0")).collect(Collectors.toList());
            Collections.shuffle(memberNicks);
            int timeCount = 0;
            while(inputStr.contains("xx")) {
                inputStr = inputStr.replaceFirst("xx", memberNicks.get(timeCount++).getNickName());
            }
            result.add(inputStr);
            addTodayTimes(qq);
            return MessageUtil.stringToList(String.join("\n", result));
        }
    }

    public List<String> queryMemberByQq(String qq) {
        List<String> result = new ArrayList<>();
        Example example = new Example(MemberNickBean.class);
        example.createCriteria().andEqualTo("qqNumber", qq).orEqualTo("nickName", qq);
        List<MemberNickBean> list = memberNickMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            MemberNickBean member = list.get(0);
            result.add("ID???" + member.getId());
            result.add("QQ???" + member.getQqNumber());
            result.add("?????????" + member.getNickName());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            result.add("???????????????" + formatter.format(member.getCreateTime()));
        } else {
            result.add("??????????????????");
        }
        return MessageUtil.stringToList(String.join("\n", result));
    }
}
