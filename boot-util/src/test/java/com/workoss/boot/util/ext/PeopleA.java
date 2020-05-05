package com.workoss.boot.util.ext;

/**
 * @author: workoss
 * @date: 2018-12-13 17:05
 * @version:
 */
@Extension("A")
public class PeopleA implements Person {
    @Override
    public void getName() {
        System.out.println("li wei");
    }
}
