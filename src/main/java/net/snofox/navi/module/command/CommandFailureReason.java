package net.snofox.navi.module.command;

enum CommandFailureReason {
    NOT_FOUND,
    MISDEFINED,
    NO_PERMISSIONS,
    REQUIRE_VOICE,
    MISSING_ARGUMENTS,
    REQUIRE_GUILD,
    NOT_FAILED
}
