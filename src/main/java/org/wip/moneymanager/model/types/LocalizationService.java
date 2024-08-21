package org.wip.moneymanager.model.types;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import org.wip.moneymanager.MoneyManager;
import org.wip.moneymanager.model.interfaces.Translation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LocalizationService {
    private final Map<String, Translation> localizationData = new HashMap<>();
    private final StringProperty selectedLanguage;
    private final static String base_path = "/org/wip/moneymanager/locale/";

    public LocalizationService(String locale) {
        selectedLanguage = new SimpleStringProperty(locale);
        try {
            loadAllLocales();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ReadOnlyStringProperty selectedLanguageProperty() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(String language) {
        selectedLanguage.set(language);
    }

    public StringBinding localizedStringBinding(String key) {
        return Bindings.createStringBinding(() -> getLocalizedString(key, selectedLanguage.get()), selectedLanguage);
    }

    public String getLocalizedString(String key, String locale) {
        return localizationData.get(locale).translate(key);
    }

    public void loadAllLocales() throws IOException {
        File dir = new File(MoneyManager.class.getResource(base_path).getFile());
        File[] files = dir.listFiles();
        assert files != null;
        for (File file : files) {
            System.out.println(file.getName());
            LocalizationMap properties = new LocalizationMap(file.getName());
            String localization_name = file.getName().replace(".properties", "");
            localizationData.put(localization_name, properties);
        }
    }
}
