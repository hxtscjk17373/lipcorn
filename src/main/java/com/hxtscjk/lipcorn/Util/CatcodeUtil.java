package com.hxtscjk.lipcorn.Util;

import catcode.CatCodeUtil;
import catcode.CodeTemplate;

public class CatcodeUtil {

    public static String getFace(Long faceId) {
        CatCodeUtil catCodeUtil = CatCodeUtil.INSTANCE;
        CodeTemplate<String> template = catCodeUtil.getStringTemplate();
        return template.face(faceId);
    }

    public static String getFace(Integer faceId) {
        return getFace(faceId.longValue());
    }

    public static String getFace(String faceId) {
        return getFace(Long.parseLong(faceId));
    }

    public static String getImage(String imagePath) {
        CatCodeUtil catCodeUtil = CatCodeUtil.INSTANCE;
        CodeTemplate<String> template = catCodeUtil.getStringTemplate();
        return template.image(imagePath);
    }
}
