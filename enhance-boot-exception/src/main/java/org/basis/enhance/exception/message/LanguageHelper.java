package org.basis.enhance.exception.message;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * 获取用户语言工具类
 *
 * @author Mr_wenpan@163.com 2021/10/10 12:03 上午
 */
public class LanguageHelper {
    private static volatile String defaultLanguage = "zh_CN";


    private LanguageHelper() {
    }

    public static void setDefaultLanguage(String lang) {
        LanguageHelper.defaultLanguage = lang;
    }

    public static String getDefaultLanguage() {
        return defaultLanguage;
    }

    /**
     * 根据当前登陆用户获取语言信息
     *
     * @return String
     */
    public static String language() {
        // todo 这里先不用获取登录用户的语言信息，先使用默认的中文语言
        String language = null;
//        CustomUserDetails details = DetailsHelper.getUserDetails();
//        if (details != null) {
//            language = details.getLanguage();
//        }
        if (StringUtils.isBlank(language)) {
            return defaultLanguage;
        }
        return language;
    }

    public static Locale locale() {
        return LocaleUtils.toLocale(LanguageHelper.language());
    }
}