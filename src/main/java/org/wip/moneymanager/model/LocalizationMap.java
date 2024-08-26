package org.wip.moneymanager.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import org.wip.moneymanager.model.interfaces.Translation;

public class LocalizationMap implements Translation {
    private final static String base_path = "/org/wip/moneymanager/locale/";
    private final static LocalizationMap default_properties = new LocalizationMap("en.properties", true);
    private final Properties properties = new Properties();
    private final String language;
    private final boolean isDefault = false;

    public LocalizationMap(String language) {
        this.language = language;
        try {
            load();
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot load properties file: " + language);
        }
    }

    public LocalizationMap(String language, boolean isDefault) {
        this(language);
    }

    public void load() throws IOException {
        try (FileInputStream input = new FileInputStream(Objects.requireNonNull(getClass().getResource(base_path + language)).getFile())) {
            properties.load(input);
        }
    }

    // Funziona solo se la lingua non è in resources
    // Da la possibilità all'untete di implementtare un sistema per permettere
    // all'utente di salvere le proprie traduzioni per lingue non implementate
    public void save() throws IOException {
        try (FileOutputStream output = new FileOutputStream(Objects.requireNonNull(getClass().getResource(base_path + language)).getFile())) {
            properties.store(output, null);
        }
    }

    private String get_defaultProperty(String key) {
        String value = default_properties.getProperty(key);
        if (value == null || value.equals("__MISSING__")) {
            throw new IllegalArgumentException("Missing property: " + key);
        }
        return value;
    }

    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.equals("__MISSING__")) {
            setProperty(key);
            System.out.println("Missing property: " + key + " in " + language);
            try {
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // se non lo ha nemmeno questo fail hai sbagliato qualcosa figlio mio...
            return isDefault ? get_defaultProperty(key) : "__MISSING__"+key+"__";
        }
        return value;
    }

    public void setProperty(String key) {
        properties.setProperty(key, "__MISSING__");
    }

    @Override
    public String translate(String key) {
        return getProperty(key);
    }
}