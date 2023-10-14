package com.onebytellc.imageviewer;

import com.onebytellc.imageviewer.logger.Logger;
import javafx.beans.binding.StringBinding;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18N {

    private static final Logger LOG = Logger.getInstance(I18N.class);
    private static final List<WeakReference<StringBinding>> BINDINGS = new LinkedList<>();
    private static ResourceBundle BUNDLE = ResourceBundle.getBundle("i18n.en");

    public static void setLocale(Locale locale) {
        try {
            BUNDLE = ResourceBundle.getBundle("i18n." + locale.getLanguage());
        } catch (Exception e) {
            LOG.warn("No localization found for: {0}. Using previous.", locale.getLanguage());
        }

        Iterator<WeakReference<StringBinding>> iter = BINDINGS.iterator();
        while (iter.hasNext()) {
            WeakReference<StringBinding> next = iter.next();
            if (next.get() == null) {
                iter.remove();
            } else {
                next.get().invalidate();
            }
        }
    }

    public static StringBinding get(String key) {
        StringBinding sb = new StringBinding() {
            @Override
            protected String computeValue() {
                try {
                    return BUNDLE.getString(key);
                } catch (Exception e) {
                    return "NO_TRANSLATION";
                }
            }
        };
        BINDINGS.add(new WeakReference<>(sb));
        return sb;
    }

}
