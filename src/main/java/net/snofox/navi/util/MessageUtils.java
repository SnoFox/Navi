package net.snofox.navi.util;

import sx.blah.discord.handle.obj.Permissions;

public class MessageUtils {
    final private static String[] tagLines = {
            "Hey!",
            "Listen!",
            "Watch Out!",
            "Hello!",
            "Look!"
    };

    public static String getTagline() {
        return tagLines[RandUtils.getRandom().nextInt(tagLines.length)];
    }

    public static String getFriendlyPermission(final Permissions perm) {
        switch(perm) {
            case BAN:
            case KICK:
                // <perm> Members
                return titlecase(perm.name().toLowerCase()) + " Members";
            case VOICE_CONNECT:
            case VOICE_DEAFEN_MEMBERS:
            case VOICE_MOVE_MEMBERS:
            case VOICE_MUTE_MEMBERS:
            case VOICE_SPEAK:
                // Strip "VOICE_", return titlecased
                return titlecase(perm.name()
                        .replaceFirst("^VOICE_", "")
                        .replace("_", " ")
                        .toLowerCase());
            case VOICE_USE_VAD:
                return "Use Voice Activity";
            default:
                return titlecase(perm.name().replace("_", " ").toLowerCase());

        }
    }

    public static String titlecase(final String string) {
        final StringBuilder sb = new StringBuilder(string.length());
        boolean uppercaseNext = true;
        for(final char c : string.toCharArray()) {
            if (uppercaseNext) {
                uppercaseNext = false;
                sb.append(Character.toUpperCase(c));
                continue;
            }
            uppercaseNext = c == ' ';
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }
}
