package com.alsw;

import com.alsw.entity.User;
import com.alsw.utils.PrintTableUtil;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class myTest {

    @Test
    public void testPrintJavaToConsole() {
        User user = new User();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String time = simpleDateFormat.format(new Date());

        user.setUserAct("114514");
        user.setUserBirth("1919-x-xx");
        user.setUserName("ababiauwfhuawihf");
        user.setUserPswd("1!23?45678!");
        user.setUserRegTime(time);
        String[] s = {"1", "2"};
        user.setArray(s);

        PrintTableUtil printTableUtil = new PrintTableUtil(user);
        printTableUtil.printTable();
    }

}
