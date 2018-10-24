package com.github.bingoohuang.mail;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;

import java.util.Properties;

public class Env {
    @SneakyThrows
    public static Properties loadEnvProperties(String classpathPropertiesFileName) {
        @Cleanup val is = Env.class.getClassLoader().getResourceAsStream(classpathPropertiesFileName);
        val p = new Properties();
        p.load(is);
        return p;
    }
}
