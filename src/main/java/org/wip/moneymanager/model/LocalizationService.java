package org.wip.moneymanager.model;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import org.wip.moneymanager.MoneyManager;
import org.wip.moneymanager.model.interfaces.Translation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static List<String> extractTextWithinBrackets(String input) {
        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[(.*?)]");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            matches.add(matcher.group(1));
        }
        return matches;
    }

    public StringBinding lsb(String key) {
        return localizedStringBinding(key);
    }
    public StringBinding localizedStringBinding(String key) {
        return Bindings.createStringBinding(() -> {
            String localizedString = getLocalizedString(key, selectedLanguage.get());
            List<String> to_replace = extractTextWithinBrackets(localizedString);
            for (String s : to_replace) {
                localizedString = localizedString.replace("[" + s + "]", getLocalizedString(s, selectedLanguage.get()));
            }
            return localizedString;
        }, selectedLanguage);
    }

    @SafeVarargs
    public final StringBinding lsb(String key, ObservableValue<String>... arg) {
        return localizedStringBinding(key, arg);
    }
    public final StringBinding localizedStringBinding(String key, ObservableValue<String>... args) {
        Observable[] observables = new Observable[args.length + 1];
        observables[0] = selectedLanguage;
        System.arraycopy(args, 0, observables, 1, args.length);

        return Bindings.createStringBinding(() -> {
            String localizedString = getLocalizedString(key, selectedLanguage.get());
            for (int i = 0; i < args.length; i++) {
                localizedString = localizedString.replace("{" + i + "}", args[i].getValue());
            }
            List<String> to_replace = extractTextWithinBrackets(localizedString);
            for (String s : to_replace) {
                localizedString = localizedString.replace("[" + s + "]", getLocalizedString(s, selectedLanguage.get()));
            }
            return localizedString;
        }, observables);
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
