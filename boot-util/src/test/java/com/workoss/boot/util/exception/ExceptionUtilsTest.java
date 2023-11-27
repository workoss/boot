package com.workoss.boot.util.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author workoss
 */
class ExceptionUtilsTest {

    @Test
    void newInstance() {
        for (int i = 0; i < 10; i++) {
            System.out.println(ExceptionUtils.newInstance(BootException.class, "boot-"+i, "2"));
            System.out.println(ExceptionUtils.newInstance(TestException.class, "test-"+i, "2"));
        }

    }

    @Test
    void getConstructor() {
    }
}