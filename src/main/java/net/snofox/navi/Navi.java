package net.snofox.navi;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.snofox.navi.config.CoreConfig;
import net.snofox.navi.config.IConfig;
import net.snofox.navi.config.VoiceLogConfig;
import net.snofox.navi.module.Playlist;
import net.snofox.navi.module.TestEvents;
import net.snofox.navi.module.VoiceLog;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.modules.Configuration;

import java.io.File;
/*
Hey!
Listen!
Watch Out!
Hello!
Look!
 */
public class Navi {
    private static Navi instance;
    private static CoreConfig coreConfig;
    private static String configPath;
    private static ObjectMapper mapper = new ObjectMapper();
    private IDiscordClient discordClient;
    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Config directory?");
            return;
        }
        configPath = args[0];
        coreConfig = (CoreConfig)initConfig(CoreConfig.class);
        if(coreConfig == null) System.exit(1);

        instance = new Navi();
    }

    private static File getConfigFile(final String configName) {
        return getConfigFile(configName, false);
    }

    private static File getConfigFile(final String configName, final boolean backup) {
        return new File(configPath
                + File.separator
                + configName + ".json" + (backup ? '~' : ""));
    }

    private static String getConfigName(final Class<? extends IConfig> configClass) {
        return configClass.getSimpleName().replaceFirst("Config$", "").toLowerCase();
    }

    private static IConfig initConfig(final Class<? extends IConfig> configClass) {
        try {
            final String configName = getConfigName(configClass);
            final File configFile = getConfigFile(configName);
            if(!configFile.exists())
                return configClass.getDeclaredConstructor().newInstance();
            return mapper.readValue(configFile, configClass);
        } catch (Exception e) {
            Discord4J.LOGGER.error("Failed to load configuration file: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        return null;
    }

    public static void saveConfig(final IConfig config) {
        try {
            final File configFile = getConfigFile(getConfigName(config.getClass()));
            final File backupFile = getConfigFile(getConfigName(config.getClass()), true);
            if(backupFile.exists()) backupFile.delete();
            configFile.renameTo(backupFile);
            mapper.writeValue(configFile, config);
        } catch (Exception e) {
            Discord4J.LOGGER.error("Failed to save configuration file: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public static IDiscordClient getDiscordClient() {
        return instance.discordClient;
    }

    Navi() {
        // Configure Discord4J
        ((Discord4J.Discord4JLogger)Discord4J.LOGGER).setLevel(Discord4J.Discord4JLogger.Level.DEBUG);
        Configuration.LOAD_EXTERNAL_MODULES = false;
        Configuration.AUTOMATICALLY_ENABLE_MODULES = false;
        this.discordClient = new ClientBuilder().withToken(coreConfig.getApiToken()).build();
        // load me some modules
        discordClient.getDispatcher().registerListener(new VoiceLog(initConfig(VoiceLogConfig.class)));
        discordClient.getDispatcher().registerListener(new Playlist());
        discordClient.getDispatcher().registerListener(new TestEvents());
        discordClient.login();
    }
}
