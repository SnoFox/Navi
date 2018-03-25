package net.snofox.navi.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface NaviModule {
    public Priority priority() default Priority.NORMAL;

    public enum Priority {
        HIGH,
        NORMAL,
        LOW
    }
}
