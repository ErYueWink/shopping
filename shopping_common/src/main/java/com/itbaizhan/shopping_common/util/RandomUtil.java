package com.itbaizhan.shopping_common.util;

import java.util.Random;

public class RandomUtil {

    public static String buildCheckCode(int digit){
        String str = "0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0;i < digit;i++){
            char ch = str.charAt(random.nextInt(str.length())); // 返回一个[0,str.length)之间的值包括0但不包括str.length
            stringBuilder.append(ch);
        }
        return stringBuilder.toString();

    }
}
