package net.islandearth.rpgregions.translation;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.regex.Pattern;

public class TranslationsTest {
    
    @Test
    public void translationTest() {
        Reflections reflections = new Reflections("lang/", new ResourcesScanner());
        Set<String> fileNames = reflections.getResources(Pattern.compile(".*\\.yml"));
        fileNames.forEach(fileName -> {
            InputStream in = getClass().getClassLoader().getResourceAsStream(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);
            for (Translations translation : Translations.values()) {
                if (config.get(translation.toString().toLowerCase()) == null) {
                    Assert.fail(translation.toString() + " not found");
                }
            }
        });
    }
}
