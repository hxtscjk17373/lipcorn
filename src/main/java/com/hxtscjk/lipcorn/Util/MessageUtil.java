package com.hxtscjk.lipcorn.Util;

import java.util.ArrayList;
import java.util.List;

public class MessageUtil {

    /**
     * 检查消息类型
     * @param message
     * @return
     */
    public int checkMessage(String message) {
        if (!message.contains(" ")) {
            return 1;
        }
        String[] msg = message.split(" ");
        return msg.length;
    }

    private String getCommand(String message, int pos) {
        if ((!message.contains(" ")) && pos == 1) {
            return message;
        } else {
            String[] msg = message.split(" ");
            if (msg.length >= pos) {
                return msg[pos - 1];
            } else {
                return null;
            }
        }
    }

    /**
     * 获取指令类型
     * @param message
     * @return
     */
    public String getCommandType(String message) {
        return getCommand(message, 1);
    }

    /**
     * 获取指令内容
     * @param message
     * @return
     */
    public String getCommandText(String message) {
        return getCommand(message, 2);
    }

    /**
     * 获取指令值
     * @param message
     * @return
     */
    public String getCommandValue(String message) {
        return getCommand(message, 3);
    }

    public static List<String> stringToList(String str) {
        List<String> list = new ArrayList<>();
        list.add(str);
        return list;
    }
}
