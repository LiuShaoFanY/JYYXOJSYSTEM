package com.schall.jyyxbackendserviceclient.service.config;

import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * ConfigLoader 类用于加载配置文件。
 */
public class ConfigLoader {

    /**
     * 加载配置文件。
     *
     * @return 配置文件内容
     * @throws IOException 如果配置文件未找到或读取失败
     */
    public static Map<String, Object> loadConfig() throws IOException {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream("config.yaml")) {
            if (inputStream == null) {
                throw new FileNotFoundException("config.yaml not found on classpath");
            }
            return yaml.load(inputStream);
        }
    }
}