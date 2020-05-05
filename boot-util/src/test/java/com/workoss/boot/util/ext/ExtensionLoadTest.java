package com.workoss.boot.util.ext;

import org.junit.jupiter.api.Test;

/**
 * @author: workoss
 * @date: 2018-12-13 18:56
 * @version:
 */
public class ExtensionLoadTest {

    @Test
    public void test01(){
        ExtensionLoader<Person> personExtensionLoader = ExtensionLoaderFactory.getExtensionLoader(Person.class);
        personExtensionLoader.getExtension("C");
        personExtensionLoader.getExtension("A").getName();
        personExtensionLoader.getExtension("B").getName();
    }
}
