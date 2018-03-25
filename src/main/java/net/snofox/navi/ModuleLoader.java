package net.snofox.navi;

import net.snofox.navi.config.IConfig;
import net.snofox.navi.module.NaviModule;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import sx.blah.discord.Discord4J;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ModuleLoader {
    private static Logger logger = Navi.getLogger(ModuleLoader.class);

    /* pkg-private */ static void loadModules() {
        final Marker marker = MarkerFactory.getMarker("Module Loader");
        final Reflections modules = new Reflections("net.snofox.navi.module");
        final Reflections configs = new Reflections("net.snofox.navi.config");
        final Set<Class<? extends IConfig>> configClasses = configs.getSubTypesOf(IConfig.class);
        final Set<Class<?>> moduleClasses = modules.getTypesAnnotatedWith(NaviModule.class);
        final Map<NaviModule.Priority, Set<Class<?>>> prioritizedModuleClasses = new LinkedHashMap<>(NaviModule.Priority.values().length);
        for (NaviModule.Priority prio : NaviModule.Priority.values())
            prioritizedModuleClasses.put(prio, new HashSet<>());
        for (final Class clazz : moduleClasses) {
            // guaranteed safe by Reflections criteria
            final NaviModule.Priority prio = ((NaviModule) clazz.getAnnotation(NaviModule.class)).priority();
            prioritizedModuleClasses.get(prio).add(clazz);
        }
        for (final Map.Entry<NaviModule.Priority, Set<Class<?>>> moduleEntry : prioritizedModuleClasses.entrySet()) {
            logger.info(marker, "Loading {} {} priority modules", moduleEntry.getValue().size(), moduleEntry.getKey().name());
            for (final Class moduleClass : moduleEntry.getValue()) {
                final String moduleName = moduleClass.getSimpleName();
                initModule(moduleClass, findConfig(moduleName, configClasses));
            }
        }
    }

    private static void initModule(final Class<?> module, final Class<? extends IConfig> config) {
        Marker marker = MarkerFactory.getMarker("Module Initializer");
        final String moduleName = module.getSimpleName();
        logger.debug(marker, "Loading {}", moduleName);
        try {
            if(module.getConstructors().length > 1) {
                throw new InstantiationException("Multiple module constructors are not supported");
            }
            final Constructor<?> constructor = module.getConstructors()[0];
            if(constructor.getParameterCount() == 0) {
                Navi.getDiscordClient().getDispatcher().registerListener(constructor.newInstance());
            } else if(constructor.getParameterCount() == 1
                    && config != null
                    && constructor.getParameterTypes()[0].isAssignableFrom(config)) {
                Navi.getDiscordClient().getDispatcher().registerListener(constructor.newInstance(initConfig(config)));
            } else {
                throw new NoSuchMethodException("Can't find an expected constructor and/or Config class");
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.error(marker, "Failed to instantiate {}: {}", moduleName, e.getClass().getSimpleName(), e);
        }
    }

    private static Class<? extends IConfig> findConfig(final String moduleName, final Set<Class<? extends IConfig>> configs) {
        for(Class<? extends IConfig> config : configs) {
            if(Navi.getConfigName(config).equalsIgnoreCase(moduleName))
                return config;
        }
        return null;
    }

    /* pkg-private */ static IConfig initConfig(final Class<? extends IConfig> configClass) {
        Marker marker = MarkerFactory.getMarker("Config Initializer");
        try {
            final String configName = Navi.getConfigName(configClass);
            final File configFile = Navi.getConfigFile(configName);
            if (!configFile.exists())
                return configClass.getDeclaredConstructor().newInstance();
            return Navi.getMapper().readValue(configFile, configClass);
        } catch (Exception e) {
            logger.error(marker, "Failed to load configuration file: {}: {}", e.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }

}
