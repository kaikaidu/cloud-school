/**
 * Created by dk on 2018/7/10.
 */

package com.amway.acti.base.util;

import com.amway.acti.model.Cert;

public class RandomUtil {

    /**
     * 随机指定范围内N个不重复的数
     * 最简单最基本的方法
     * @param min 指定范围最小值
     * @param max 指定范围最大值
     * @param n 随机数个数
     */
    public static int[] randomCommon(int min, int max, int n){
        int[] result = null;
        try {
            if (n > (max - min + 1) || max < min) {
                return null;
            }
            result = new int[n];
            int count = 0;
            while(count < n) {
                int num = (int) (Math.random() * (max - min)) + min;
                boolean flag = true;
                for (int j = 0; j < n; j++) {
                    if(num == result[j]){
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    result[count] = num;
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void main(String[] args) {
        Cert c = new Cert();
        int[] aa = RandomUtil.randomCommon(1,11,10);
        for (int a:aa) {
            System.out.println(a);
        }

    }
}
