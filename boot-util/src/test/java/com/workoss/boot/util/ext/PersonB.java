package com.workoss.boot.util.ext;

/**
 * @author: workoss
 * @date: 2018-12-13 19:16
 * @version:
 */
@Extension("B")
public class PersonB implements Person {
    @Override
    public void getName() {
        System.out.println("personB");
    }
}
