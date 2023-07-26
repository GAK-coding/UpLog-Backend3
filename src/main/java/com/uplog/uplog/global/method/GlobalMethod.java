package com.uplog.uplog.global.method;

import java.util.Random;

public class GlobalMethod {

    public String makeRandNum(int type){
        Character[] special = {'!','@','#','$','%','^','&','*'};
        StringBuffer rn = new StringBuffer();
        Random random = new Random();
        int num = 0;

        if(type == 0){
            num = 6;
        }
        else if(type == 1){
            num = 8;
        }

        for(int i = 0; i < num ; i++){
            int index = random.nextInt(4); //0~3까지 랜덤, 영어 대문자, 소문자, 숫자, 특수문자의 종류가 결정

            switch(index){
                case 0:
                    rn.append((char)(random.nextInt(26) + 65));
                    break;
                case 1:
                    rn.append((char)(random.nextInt(26)+97));
                    break;
                case 2:
                    rn.append(random.nextInt(10));
                    break;
                case 3:
                    rn.append(special[random.nextInt(special.length)]);
                    break;

            }
        }
        return rn.toString();
    }
}
