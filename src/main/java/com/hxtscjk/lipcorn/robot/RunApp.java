package com.hxtscjk.lipcorn.robot;

import love.forte.simbot.annotation.SimbotApplication;
import love.forte.simbot.core.SimbotApp;
import love.forte.simbot.core.SimbotContext;

@SimbotApplication
public class RunApp {
    public static void main(String[] args) {
        final SimbotContext context = SimbotApp.run(RunApp.class, args);
    }
}
