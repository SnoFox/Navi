package net.snofox.navi.module.command;

import sx.blah.discord.handle.obj.Permissions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandRequires {
    Permissions permission() default Permissions.SEND_MESSAGES;
    boolean voice() default false;
}
