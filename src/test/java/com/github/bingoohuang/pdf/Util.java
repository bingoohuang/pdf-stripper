package com.github.bingoohuang.pdf;

import java.io.InputStream;

public class Util {
    public static InputStream loadClassPathRes(String classpath) {
        return Util.class.getClassLoader().getResourceAsStream(classpath);
    }
}
