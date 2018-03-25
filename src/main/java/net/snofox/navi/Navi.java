package net.snofox.navi;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.snofox.navi.config.CoreConfig;
import net.snofox.navi.config.IConfig;
import net.snofox.navi.config.PlaylistConfig;
import net.snofox.navi.config.VoiceLogConfig;
import net.snofox.navi.module.NaviModule;
import net.snofox.navi.module.command.CommandHandler;
import net.snofox.navi.module.playlist.Playlist;
import net.snofox.navi.module.TestEvents;
import net.snofox.navi.module.VoiceLog;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.modules.Configuration;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Navi {
    private static Navi instance;
    private static CoreConfig coreConfig;
    private static String configPath;
    private static ObjectMapper mapper = new ObjectMapper();
    private IDiscordClient discordClient;
    private Logger logger;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Config directory?");
            return;
        }
        configPath = args[0];
        coreConfig = (CoreConfig) ModuleLoader.initConfig(CoreConfig.class);
        if (coreConfig == null) System.exit(1);
        instance = new Navi();
        instance.init();
    }

    static File getConfigFile(final String configName) {
        return getConfigFile(configName, false);
    }

    private static File getConfigFile(final String configName, final boolean backup) {
        return new File(configPath
                + File.separator
                + configName + ".json" + (backup ? '~' : ""));
    }

    /* pkg-private */ static String getConfigName(final Class<? extends IConfig> configClass) {
        return configClass.getSimpleName().replaceFirst("Config$", "").toLowerCase();
    }

    public static void saveConfig(final IConfig config) {
        try {
            final File configFile = getConfigFile(getConfigName(config.getClass()));
            final File backupFile = getConfigFile(getConfigName(config.getClass()), true);
            if (backupFile.exists()) backupFile.delete();
            configFile.renameTo(backupFile);
            mapper.writeValue(configFile, config);
        } catch (Exception e) {
            getLogger(Navi.class).error("Failed to save configuration file: {}: {}", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static IDiscordClient getDiscordClient() {
        return instance.discordClient;
    }

    public static CoreConfig getCoreConfig() {
        return coreConfig;
    }

    public static Logger getLogger(final String loggerName) {
        return LoggerFactory.getLogger(loggerName);
    }

    public static Logger getLogger(final Class clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static Logger getLogger(final Object object) {
        return getLogger(object.getClass());
    }

    private Navi() {
        // Configure Logback
        if (System.getProperty("logback.configurationFile") == null
                && coreConfig.getLogbackConfig() != null
                && !coreConfig.getLogbackConfig().isEmpty()) {
            System.setProperty("logback.configurationFile", coreConfig.getLogbackConfig());
        }
        if (System.getProperty("logback.configurationFile") == null) {
            System.err.println("No Logback config provided; using default");
        } else {
            System.out.println(String.format("Logback config coming from %s", System.getProperty("logback.configurationFile")));
        }
        this.logger = getLogger(Navi.class);
        logger.info("Listen! We're now logging!");
        logger.info("Levels enabled: Trace? {} Debug? {} Error? {} Warn? {} Info? {}", logger.isTraceEnabled(), logger.isDebugEnabled(), logger.isErrorEnabled(), logger.isWarnEnabled(), logger.isInfoEnabled());

        // Configure Discord4J
        //((Discord4J.Discord4JLogger)Discord4J.LOGGER).setLevel(Discord4J.Discord4JLogger.Level.DEBUG);
        Configuration.LOAD_EXTERNAL_MODULES = false;
        Configuration.AUTOMATICALLY_ENABLE_MODULES = false;
        this.discordClient = new ClientBuilder().withToken(coreConfig.getApiToken()).build();
    }

    private void init() {
        ModuleLoader.loadModules();
        discordClient.login();
    }
}