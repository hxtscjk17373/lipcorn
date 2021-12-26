package com.hxtscjk.lipcorn.service;

import com.hxtscjk.lipcorn.LipcornConst;
import com.hxtscjk.lipcorn.Util.CommonUtil;
import com.hxtscjk.lipcorn.bean.BlindDateBean;
import com.hxtscjk.lipcorn.bean.BlindDateResult;
import com.hxtscjk.lipcorn.mapper.BlindDateResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BlindDateResultService implements LipcornConst {

    @Autowired
    private BlindDateResultMapper blindDateResultMapper;

    /**
     * 插入单条匹配结果
     * @param member1
     * @param member2
     * @param type
     */
    public void insertResultList(BlindDateBean member1, BlindDateBean member2, String type) {
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateTime = sdf.format(currentDate.getTime());
        BlindDateResult result = new BlindDateResult();
        result.setId(CommonUtil.getUuid());
        result.setQqNumber(member1.getQqNumber());
        result.setMatchDate(dateTime);
        result.setMatchType(type);
        if (type.equals(MATCH_SOLO) || member2 == null || member2.getQqNumber() == null) {
            result.setMatchNumber(null);
        }
        else {
            result.setMatchNumber(member2.getQqNumber());
        }
        result.setCreateTime(new Date());
        result.setUpdateTime(new Date());
        result.setDeleted(BOOL_NO);
        blindDateResultMapper.insert(result);
    }

    /**
     * 根据QQ查找个人匹配记录
     * @param qq
     * @return
     */
    public List<BlindDateResult> queryMemberCPRecord(String qq) {
        Example example = new Example(BlindDateResult.class);
        example.createCriteria().andEqualTo("qqNumber", qq).andEqualTo("deleted", BOOL_NO);
        List<BlindDateResult> results = blindDateResultMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(results)) {
            return new ArrayList<>();
        }
        return results.stream().sorted(Comparator.comparing(BlindDateResult::getMatchDate).reversed()).collect(Collectors.toList());
    }

}
