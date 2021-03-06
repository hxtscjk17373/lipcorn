package com.hxtscjk.lipcorn.service;

import com.hxtscjk.lipcorn.Util.CommonUtil;
import com.hxtscjk.lipcorn.Util.MessageUtil;
import com.hxtscjk.lipcorn.bean.HappyFishRecordBean;
import com.hxtscjk.lipcorn.bean.HappyFishVersionBean;
import com.hxtscjk.lipcorn.mapper.HappyFishRecordMapper;
import com.hxtscjk.lipcorn.mapper.HappyFishVersionMapper;
import love.forte.common.ioc.annotation.Beans;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Beans
@Service
public class CatchFishService {

    @Value("${catch.fish.time.interval}")
    private String sendInterval;

    @Value("${catch.fish.my.saolei.account}")
    private String myAccount;

    @Value("${catch.fish.my.saolei.password}")
    private String myPassword;

    @Value("${catch.fish.invite.sentence}")
    private String inviteSentence;

    @Autowired
    private HappyFishRecordMapper happyFishRecordMapper;

    @Autowired
    private HappyFishVersionMapper happyFishVersionMapper;

    private String newVersion;

    public List<String> analyseNews(List<String> happyMembers) {
        updateHappyMember(happyMembers);
        String version = getVersionNow();
        List<HappyFishRecordBean> fish = getMaybeFishFromSaoleiWang(version);
        pickFishAndUpdate(fish);
        return outputFishList();
    }

    public List<String> sendCatchFishInf() throws InterruptedException {
        List<String> result = new ArrayList<>();
        List<String> ids = happyFishRecordMapper.selectAll().stream()
                .filter(v -> v.getPlayerStatus() == 0 && v.getDeleted().equals("0"))
                .map(HappyFishRecordBean::getPlayerId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            result.add("?????????????????????id");
            return MessageUtil.stringToList(String.join( "\n", result));
        }
        List<String> successIds = sendMessageWithChrome(ids);

        result.add("?????????:" + MessageUtil.stringToList(String.join( ",", successIds)));
        updateVersion();
        return MessageUtil.stringToList(String.join( "\n", result));
    }

    public List<String> getMenu() {
        List<String> result = new ArrayList<>();
        result.add("1.??????");
        result.add("2.?????? id");
        result.add("3.?????? id");
        result.add("4.???????????? id");
        result.add("5.?????? id");
        return MessageUtil.stringToList(String.join( "\n", result));
    }

    public List<String> insertBlackList(String id) {
        List<String> list = new ArrayList<>();
        try {
            Long l = Long.parseLong(id);
        } catch (Exception e) {
            list.add("??????????????????????????????id??????");
            return MessageUtil.stringToList(String.join( "\n", list));
        }
        HappyFishRecordBean exist = findIdExist(id, happyFishRecordMapper.selectAll().stream().filter(v -> v.getDeleted().equals("0")).collect(Collectors.toList()));
        if (exist != null) {
            if (exist.getPlayerStatus() == 99) {
                list.add("id" + id + "?????????????????????");
            } else {
                HappyFishRecordBean player = new HappyFishRecordBean();
                player.setPlayerId(id);
                player.setPlayerStatus(99);
                Example example = new Example(HappyFishRecordBean.class);
                example.createCriteria().andEqualTo("playerId", id);
                happyFishRecordMapper.updateByExampleSelective(player, example);
                list.add("????????????[" + id + "]??????????????????");
            }
        } else {
            list.add("?????????????????????????????????????????????????????????????????????????????????");
        }
        return MessageUtil.stringToList(String.join( "\n", list));
    }

    public List<String> getFishStatus(String id) {
        List<String> list = new ArrayList<>();
        try {
            Long l = Long.parseLong(id);
        } catch (Exception e) {
            list.add("??????????????????????????????id??????");
            return MessageUtil.stringToList(String.join( "\n", list));
        }
        HappyFishRecordBean exist = findIdExist(id, happyFishRecordMapper.selectAll().stream().filter(v -> v.getDeleted().equals("0")).collect(Collectors.toList()));
        if (exist != null) {
            list.add("id???" + exist.getPlayerId());
            list.add("?????????" + exist.getPlayerName());
            String status = "";
            if (exist.getPlayerStatus() == 1) {
                status = "?????????";
            } else if (exist.getPlayerStatus() == 2) {
                status = "????????????";
            } else if (exist.getPlayerStatus() == 99) {
                status = "?????????";
            }
            list.add("?????????" + status);
            if (exist.getPlayerStatus() == 1) {
                list.add("???????????????" + exist.getSendCount());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                list.add("?????????????????????" + sdf.format(exist.getLastSend()));
            }
        } else {
            list.add("????????????????????????");
        }
        return MessageUtil.stringToList(String.join( "\n", list));
    }

    public List<String> insertCatchList(String id) {
        List<String> list = new ArrayList<>();
        try {
            Long l = Long.parseLong(id);
        } catch (Exception e) {
            list.add("??????????????????????????????id??????");
            return MessageUtil.stringToList(String.join( "\n", list));
        }
        HappyFishRecordBean exist = findIdExist(id, happyFishRecordMapper.selectAll().stream().filter(v -> v.getDeleted().equals("0")).collect(Collectors.toList()));
        if (exist != null) {
            if (exist.getPlayerStatus() == 2) {
                list.add("[" + id + "]????????????");
            } else if (exist.getPlayerStatus() == 99) {
                list.add("[" + id + "]???????????????????????????");
            } else if (exist.getPlayerStatus() == 1) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date lastDate = exist.getLastSend();
                Date nextDate = new Date(lastDate.getTime() + Long.parseLong(sendInterval));
                String lastDateTime = sdf.format(lastDate);
                String nextDateTime = sdf.format(nextDate);
                list.add("[" + id + "]??????" + lastDateTime + "?????????????????????????????????????????????"
                        + nextDateTime + "??????????????????????????????????????????????????? " + id + "???");
            }
        } else {
            HappyFishRecordBean fish = new HappyFishRecordBean();
            fish.setPlayerId(id);
            fish.setPlayerName("????????????");
            insertFishInf(fish, 1);
            List<String> fishId = new ArrayList<>();
            fishId.add(id);
            try {
                sendMessageWithChrome(fishId);
                list.add("????????????");
            } catch (Exception e) {
                list.add("?????????????????????????????????");
            }
        }
        return MessageUtil.stringToList(String.join( "\n", list));
    }

    public List<String> insertCatchListForce(String id) {
        List<String> list = new ArrayList<>();
        try {
            Long l = Long.parseLong(id);
        } catch (Exception e) {
            list.add("??????????????????????????????id??????");
            return MessageUtil.stringToList(String.join( "\n", list));
        }
        HappyFishRecordBean exist = findIdExist(id, happyFishRecordMapper.selectAll().stream().filter(v -> v.getDeleted().equals("0")).collect(Collectors.toList()));
        if (exist != null) {
            if (exist.getPlayerStatus() == 2) {
                list.add("[" + id + "]????????????");
            } else if (exist.getPlayerStatus() == 99) {
                list.add("[" + id + "]???????????????????????????");
            } else if (exist.getPlayerStatus() == 1) {
                List<String> fishId = new ArrayList<>();
                fishId.add(id);
                try {
                    sendMessageWithChrome(fishId);
                    list.add("????????????");
                } catch (Exception e) {
                    list.add("?????????????????????????????????");
                }
            }
        } else {
            HappyFishRecordBean fish = new HappyFishRecordBean();
            fish.setPlayerId(id);
            fish.setPlayerName("????????????");
            insertFishInf(fish, 1);
            List<String> fishId = new ArrayList<>();
            fishId.add(id);
            try {
                sendMessageWithChrome(fishId);
                list.add("????????????");
            } catch (Exception e) {
                list.add("?????????????????????????????????");
            }
        }
        return MessageUtil.stringToList(String.join( "\n", list));
    }

    private void updateVersion() {
        HappyFishVersionBean versionBean = new HappyFishVersionBean();
        versionBean.setId("1");
        versionBean.setRecordId(this.newVersion);
        versionBean.setUpdateTime(new Date());
        happyFishVersionMapper.updateByPrimaryKeySelective(versionBean);
    }

    private void updateHappyMember(List<String> happyMemberIds) {
        List<HappyFishRecordBean> fishList = happyFishRecordMapper.selectAll().stream().filter(v -> v.getDeleted().equals("0")).collect(Collectors.toList());
        happyMemberIds = happyMemberIds.stream().distinct().collect(Collectors.toList());
        for (String id : happyMemberIds) {
            try {
                Long l = Long.parseLong(id);
            } catch (Exception e) {
                continue;
            }
            HappyFishRecordBean bean = findIdExist(id, fishList);
            if (bean == null) {
                HappyFishRecordBean recordBean = new HappyFishRecordBean();
                recordBean.setPlayerId(id);
                insertFishInf(recordBean, 2);
            }
            else {
                HappyFishRecordBean changeBean = new HappyFishRecordBean();
                changeBean.setId(bean.getId());
                changeBean.setPlayerStatus(2);
                happyFishRecordMapper.updateByPrimaryKeySelective(changeBean);
            }
        }
    }

    public List<String> outputFishList() {
        List<HappyFishRecordBean> fishList = getSendFishList();
        List<String> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(fishList)) {
            list.add("????????????????????????");
            return list;
        }
        list.add("???????????????" + fishList.size() + "???");
        for (HappyFishRecordBean fish : fishList) {
            String fishInf = "";
            fishInf += (fish.getPlayerId());
            fishInf += (",");
            fishInf += (fish.getPlayerName());
            fishInf += (",exp=");
            fishInf += (fish.getRecordTime());
            list.add(fishInf);
        }
        list.add("????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
        list.add("????????????????????????");
        list.add("?????????????????? xxxxx???");
        return MessageUtil.stringToList(String.join( "\n", list));
    }

    private List<HappyFishRecordBean> getSendFishList() {
        List<HappyFishRecordBean> fishList = new ArrayList<>();
        Example example = new Example(HappyFishRecordBean.class);
        example.createCriteria().andEqualTo("playerStatus", 0);
        fishList = happyFishRecordMapper.selectByExample(example);
        return fishList;
    }

    public void pickFishAndUpdate(List<HappyFishRecordBean> oneFish) {
        //??????????????????????????????
        List<HappyFishRecordBean> twoFish = simpFishList(oneFish);
        List<HappyFishRecordBean> oldFish = happyFishRecordMapper.selectAll().stream().filter(v -> v.getDeleted().equals("0")).collect(Collectors.toList());
        for (HappyFishRecordBean fish : twoFish) {
            HappyFishRecordBean existFish = findIdExist(fish.getPlayerId(), oldFish);
            if (existFish == null) {
                //???????????????
                insertFishInf(fish, 0);
            } else {
                //????????????
                if (existFish.getPlayerStatus() == 2 || existFish.getPlayerStatus() == 99) {
                    //??????????????????????????????(2/99)
                    ;
                } else if (existFish.getPlayerStatus() == 1) {
                    //???????????????????????????????????????(1)
                    Date lastDate = existFish.getLastSend();
                    Date nowDate = new Date();
                    if (nowDate.getTime() - Long.parseLong(this.sendInterval) > lastDate.getTime()) {
                        //????????????????????????
                        updateFishInf(fish, existFish, 1);
                    }

                } else {
                    //??????????????????????????????(0)
                    updateFishInf(fish, existFish, 0);
                }
            }
        }
    }

    private void updateFishInf(HappyFishRecordBean newFish, HappyFishRecordBean oldFish, int countIncrease) {
        HappyFishRecordBean resultFish = new HappyFishRecordBean();
        BeanUtils.copyProperties(oldFish, resultFish);
        resultFish.setRecordTime(newFish.getRecordTime());
        resultFish.setPlayerStatus(0);
        resultFish.setUpdateTime(new Date());
        happyFishRecordMapper.updateByPrimaryKeySelective(resultFish);
    }

    private List<HappyFishRecordBean> simpFishList(List<HappyFishRecordBean> oneFish) {
        oneFish = oneFish.stream().sorted(Comparator.comparing(HappyFishRecordBean::getPlayerId)).collect(Collectors.toList());
        boolean flag = true;
        while (flag) {
            boolean flag1 = true;
            for (int i = 0; i < oneFish.size() -1; i++) {
                if (oneFish.get(i).getPlayerId().equals(oneFish.get(i+1).getPlayerId())) {
                    if (oneFish.get(i).getRecordTime() <= oneFish.get(i+1).getRecordTime()) {
                        oneFish.remove(i+1);
                    } else {
                        oneFish.remove(i);
                    }
                    flag1 = false;
                    break;
                }
            }
            if (flag1) {
                flag = false;
            }
        }
        return oneFish;
    }

    private void insertFishInf(HappyFishRecordBean fish, int type) {
        if (!StringUtils.hasLength(fish.getPlayerId())) {
            return ;
        }
        if (!StringUtils.hasLength(fish.getPlayerName())) {
            fish.setPlayerName("?????????");
        }
        if (fish.getRecordTime() == null) {
            fish.setRecordTime(149.99);
        }
        fish.setId(CommonUtil.getUuid());
        fish.setPlayerStatus(type);
        fish.setCreateTime(new Date());
        fish.setUpdateTime(new Date());
        fish.setDeleted("0");
        fish.setSendCount(0);
        happyFishRecordMapper.insert(fish);
    }

    private HappyFishRecordBean findIdExist(String id, List<HappyFishRecordBean> list) {
        if (!CollectionUtils.isEmpty(list)) {
            for (HappyFishRecordBean bean : list) {
                String listId = bean.getPlayerId();
                if (listId.equals(id)) {
                    return bean;
                }
            }
        }
        return null;
    }

    public List<HappyFishRecordBean> getMaybeFishFromSaoleiWang(String version) {
        String line;
        boolean flag = true;
        int pageCount = 1;
        int numberflag = 0;
        List<HappyFishRecordBean> fishList = new ArrayList<>();
        try {
            while (flag) {
                URL url=new URL("http://saolei.wang/News/Index.asp?Page=" + pageCount++);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                int responsecode = urlConnection.getResponseCode();
                if(responsecode == 200) {
                    //?????????????????????????????????????????????
                    BufferedReader reader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"GBK"));
                    while((line=reader.readLine())!=null){
                        if (line.contains("??????????????????????????????")) {
                            String videoId = getInfFromLine(line, "/Video/Show.asp?Id=", "');\" title=\"??????????????????", 0);
                            if (Integer.parseInt(videoId) <= Integer.parseInt(version)) {
                                flag = false;
                                break;
                            }
                            HappyFishRecordBean fish = new HappyFishRecordBean();
                            fish.setPlayerId(getInfFromLine(line, "Player/Show.asp?Id=", "');\" title", 0));
                            fish.setPlayerName(getInfFromLine(line, "??????????????????", "a>", 1));
                            fish.setRecordTime(Double.parseDouble(getInfFromLine(line, "class=\"Highest\">", "</a> <span", 0)));
                            if (fish.getRecordTime() < 150) {
                                fishList.add(fish);
                            }
                            if (numberflag == 0) {
                                this.newVersion = videoId;
                                numberflag = 1;
                            }
                        }
                    }
                }
                else{
                    System.out.println("?????????????????????????????????????????????????????????"+responsecode);
                }
            }
            return fishList;
        }
        catch(Exception e){
            System.out.println("???????????????????????????,???????????????"+e);
        }
        return new ArrayList<>();
    }

    private String getVersionNow() {
        HappyFishVersionBean versionBean = happyFishVersionMapper.selectAll().get(0);
        return versionBean.getRecordId();
    }

    private String getInfFromLine(String line, String preStr, String backStr, int type) {
        int indexLeft = line.indexOf(preStr);
        int indexRight = line.indexOf(backStr);
        String result = "";
        if (indexLeft >= 0 && indexRight >= 0) {
            if (type == 0) {
                result = line.substring(indexLeft + preStr.length(), indexRight);
            }
            if (type == 1) {
                line = line.substring(indexLeft + preStr.length(), indexRight);
                indexLeft = line.indexOf(">");
                indexRight = line.indexOf("<");
                if (indexLeft >= 0 && indexRight >= 0) {
                    result = line.substring(indexLeft + 1, indexRight);
                }
            }
            return result;
        }
        else return "";
    }

    /**
     * ??????????????????
     * @param playerIds
     * @throws InterruptedException
     */
    public List<String> sendMessageWithChrome(List<String> playerIds) throws InterruptedException {
        //WebDriverManager.chromedriver().setup();
        playerIds.add("20998");
        List<HappyFishRecordBean> allFishList = happyFishRecordMapper.selectAll().stream().filter(v -> v.getDeleted().equals("0")).collect(Collectors.toList());
        List<String> resultList = new ArrayList<>();
        System.setProperty("webdriver.gecko.driver",
                "src/main/tools/geckodriver.exe");
        WebDriver webDriver = new FirefoxDriver();
        //????????????
        webDriver.get("http://saolei.wang/Player/Login.asp");
        TimeUnit.SECONDS.sleep(2);
        webDriver.findElement(By.xpath("/html/body/form/table/tbody/tr/td/table[2]/tbody/tr/td[1]/input[1]")).sendKeys(myAccount);
        webDriver.findElement(By.xpath("/html/body/form/table/tbody/tr/td/table[2]/tbody/tr/td[1]/input[2]")).sendKeys(myPassword);
        webDriver.findElement(By.xpath("/html/body/form/table/tbody/tr/td/table[2]/tbody/tr/td[2]/table")).click();
        TimeUnit.SECONDS.sleep(2);
        if (!CollectionUtils.isEmpty(playerIds)) {
            for (String id : playerIds) {
                webDriver.get("http://saolei.wang/Message/Send.asp?Id=" + id);
                webDriver.findElement(By.xpath("//*[@id=\"Window_Table\"]/tbody/tr/td/table[4]/tbody/tr[1]/td[2]/span/textarea")).sendKeys(inviteSentence);
                webDriver.findElement(By.xpath("//*[@id=\"Window_Table\"]/tbody/tr/td/table[4]/tbody/tr[2]/td/table/tbody/tr/td[1]/table/tbody/tr/td")).click();
                HappyFishRecordBean changeBean = findIdExist(id, allFishList);
                if (changeBean != null) {
                    changeBean.setLastSend(new Date());
                    changeBean.setSendCount(changeBean.getSendCount() + 1);
                    changeBean.setPlayerStatus(1);
                    if (!id.equals("20998")) {
                        happyFishRecordMapper.updateByPrimaryKeySelective(changeBean);
                        resultList.add(id);
                    }
                }
                TimeUnit.SECONDS.sleep(1);
            }
        }
        //????????????
        webDriver.get("http://saolei.wang/Main/Index.asp");
        webDriver.quit();
        return resultList;
    }
}
