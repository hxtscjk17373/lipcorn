package com.hxtscjk.lipcorn.service;

import com.hxtscjk.lipcorn.Util.MessageUtil;
import com.hxtscjk.lipcorn.bean.MemberNickBean;
import com.hxtscjk.lipcorn.mapper.MemberNickMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MemberCommonService {

    @Autowired
    private MemberNickMapper memberNickMapper;

    public List<String> randMembers(String numStr) {
        List<String> result = new ArrayList<>();
        int num = 0;
        try {
            num = Integer.parseInt(numStr);
        } catch (Exception e) {
            result.add("输入错误！格式：【随机群员 数量（1-5）】");
            return MessageUtil.stringToList(String.join("\n", result));
        }

        if (num > 5 || num < 1) {
            result.add("随机群员数量限制1-5个哦");
            return MessageUtil.stringToList(String.join("\n", result));
        }
        List<MemberNickBean> memberNicks = memberNickMapper.selectAll();
        Collections.shuffle(memberNicks);
        for (int i = 0; i < num; i++) {
            result.add(memberNicks.get(i).getNickName());
        }
        return MessageUtil.stringToList(String.join("\n", result));
    }

    public List<String> fillBlankMember(String inputStr) {
        List<String> result = new ArrayList<>();
        if (!inputStr.contains("xx")) {
            result.add("语句不包含填充昵称xx，请重新输入");
            return MessageUtil.stringToList(String.join("\n", result));
        }
        else {
            String tempStr = inputStr.replace("xx", "");
            int sub = inputStr.length() - tempStr.length();
            if (sub > 20) {
                result.add("最多包含10个填充昵称，请重新输入");
                return MessageUtil.stringToList(String.join("\n", result));
            }
            List<MemberNickBean> memberNicks = memberNickMapper.selectAll();
            Collections.shuffle(memberNicks);
            int timeCount = 0;
            while(inputStr.contains("xx")) {
                inputStr = inputStr.replaceFirst("xx", memberNicks.get(timeCount++).getNickName());
            }
            result.add(inputStr);
            return MessageUtil.stringToList(String.join("\n", result));
        }
    }
}
