package dev.mouinal.config;

import dev.mouinal.commands.MouinalCommand;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MouinalConfig {
    private static MouinalConfig instance;
    private final Set<Class<?>> modules = getClassesByPackage("dev.mouinal.config.configs");
    private final File baseConfigFolder = new File("mouinal");
    private static final Map<Class<?>, Object> objects = new HashMap<>();

    public MouinalConfig() {
        instance = this;
        if (!baseConfigFolder.exists()) {
            if (!baseConfigFolder.mkdirs()) {
                throw new RuntimeException(baseConfigFolder.getAbsolutePath() + " cannot be created.");
            }
        }
    }

    public void setup() {
        Bukkit.getCommandMap().register("mouinal", new MouinalCommand(this));
    }

    public void loadConfig() {
        modules.forEach(clazz -> {
            if (!clazz.isAnnotationPresent(Configuration.class)) return;
            Object instance = null;
            try {
                instance = clazz.getDeclaredConstructor().newInstance();

                Configuration configuration = clazz.getDeclaredAnnotation(Configuration.class);
                File configFile = new File(baseConfigFolder, "mouinal_" + configuration.name() + ".yml");
                if (!configFile.exists()) {
                    if (!configFile.createNewFile()) {
                        throw new RuntimeException(configFile.getAbsolutePath() + " cannot be created.");
                    }
                }

                YamlConfiguration config = getConfig(configFile);
                for (Field field : clazz.getDeclaredFields()) {
                    int modifiers = field.getModifiers();
                    if (!field.isAnnotationPresent(ConfigInfo.class)) continue;
                    if (Modifier.isFinal(modifiers) || !(Modifier.isPublic(modifiers))) continue;

                    ConfigInfo info = field.getDeclaredAnnotation(ConfigInfo.class);
                    if (!config.contains(info.name())) {
                        Object value = field.get(instance);
                        if (value == null) throw new NullPointerException("Config value cannot be null!");

                        config.set(info.name(), value);
                        if (!info.comment().isBlank()) {
                            config.setComments(info.name(), List.of(info.comment()));
                        }
                    } else {
                        field.set(instance, config.get(info.name()));
                    }
                }

                if (!objects.containsKey(clazz)) {
                    objects.put(clazz, instance);
                }

                config.save(configFile);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IOException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Must have a no-arg constructor.");
            }
        });
    }

    public void reloadConfig() {
        objects.clear();
        loadConfig();
    }

    private static @NotNull YamlConfiguration getConfig(File configFile) throws IOException {
        try {
            YamlConfiguration configuration = new YamlConfiguration();
            configuration.load(configFile);
            return configuration;
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private Set<Class<?>> getClassesByPackage(String... packages) {
        try(ScanResult scanResult = new ClassGraph().acceptPackages(packages).scan()) {
            return scanResult.getAllClasses().stream().map(ClassInfo::loadClass)
                    .collect(Collectors.toSet());
        }
    }

    public static void reload() {
        instance.reloadConfig();
    }

    @SuppressWarnings("unchecked")
    public static <V> V get(Class<V> clazz) {
        return (V) objects.get(clazz);
    }
}
