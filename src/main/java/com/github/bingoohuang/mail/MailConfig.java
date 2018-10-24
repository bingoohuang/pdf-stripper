package com.github.bingoohuang.mail;

import java.util.Properties;

public interface MailConfig {
    Properties env = Env.loadEnvProperties("mail-config.properties");

    static String get(String name) {
        return env.getProperty(name);
    }
}
