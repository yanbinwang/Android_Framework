package com.dataqin.common.http.encryption;

import java.util.Random;

/**
 * Created by liuzhi on 15/11/16.
 */
public class RandomNum {
    private static Random random = new Random();
    private static char[] ch = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
            'x', 'y', 'z', '0', '1'};//最后又重复两个0和1，因为需要凑足数组长度为64

    public static String getRandomStringRanomLength(int intMin, int intMax) {
        String strResult;
        if (intMax < intMin) {
            int temp = intMax;
            intMax = intMin;
            intMin = temp;
        }
        if (intMax == 0) {
            return "最大位数不为正数的字符串无法生成";
        }
        Random r = new Random();
        int randomNum = r.nextInt();
        int randLength = randomNum % (intMax - intMin + 1) + intMin;
        strResult = createRandomString(randLength);
        return strResult;
    }

    //生成指定长度的随机字符串
    public static synchronized String createRandomString(int length) {
        if (length > 0) {
            int index = 0;
            char[] temp = new char[length];
            int num = random.nextInt();
            for (int i = 0; i < length % 5; i++) {
                temp[index++] = ch[num & 63];//取后面六位，记得对应的二进制是以补码形式存在的。
                num >>= 6;//63的二进制为:111111
                // 为什么要右移6位？因为数组里面一共有64个有效字符。为什么要除5取余？因为一个int型要用4个字节表示，也就是32位。
            }
            for (int i = 0; i < length / 5; i++) {
                num = random.nextInt();
                for (int j = 0; j < 5; j++) {
                    temp[index++] = ch[num & 63];
                    num >>= 6;
                }
            }
            return new String(temp, 0, length);
        } else if (length == 0) {
            return "";
        } else {
            throw new IllegalArgumentException();
        }
    }

}
