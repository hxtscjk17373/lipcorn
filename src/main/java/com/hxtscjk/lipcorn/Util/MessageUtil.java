package com.hxtscjk.lipcorn.Util;

public class MessageUtil {

    /**
     * 检查消息类型
     * @param message
     * @return
     */
    public int checkMessage(String message) {
        int blankPosition = message.contains(" ") ? message.indexOf(' ') : -1;
        //若存在空格且空格不在首位，则认定为可能是实用指令语句，返回类型1
        if(blankPosition >=1) {
            return 1;
        }
        //若不是视为其他指令，返回类型2
        else {
            return 2;
        }
    }

    /**
     * 获取指令类型
     * @param message
     * @return
     */
    public String getCommandType(String message) {
        String commandType = new String();
        //消息中首个空格位置
        int blankPosition = message.indexOf(' ');
        commandType = message.substring(0, blankPosition);
        return commandType;
    }

    /**
     * 获取指令内容
     * @param message
     * @return
     */
    public String getCommandText(String message) {
        String commandText;
        //消息中首个空格位置
        int blankPosition = message.indexOf(' ');
        commandText = message.substring(blankPosition+1);
        return commandText;
    }
}
