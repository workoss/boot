
package com.workoss.boot.util.id;

/**
 * 雪花id生成
 *
 * @author workoss
 */
@SuppressWarnings("ALL")
public class SnowflakeUtil {
    public static final Sequence SEQUENCE = new Sequence();

    public static Long nextId() {
        return SEQUENCE.nextId();
    }

    public static Long parseTimestamp(Long id){
        return SEQUENCE.parseTimestamp(id);
    }

}
