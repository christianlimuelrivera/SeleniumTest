package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 📖 CONFIGREADER - The Framework's Memory
 * This class reads the 'config.properties' file once and stores the settings.
 * It prevents you from hardcoding values like URLs and Browser names in your tests.
 */
public class ConfigReader {

    // 📦 Properties is a built-in Java object that stores data in "Key = Value" pairs.
    private static Properties properties;

    // ⚡ STATIC BLOCK: This runs automatically the VERY FIRST time you mention "ConfigReader" in your code.
    // It loads the file into memory immediately so it's ready when you need it.
    static {
        try {
            // 1️⃣ Define where the file is located (relative to your project root).
            String path = "src/main/resources/config.properties";

            // 2️⃣ Open a "stream" to read the raw bytes of the file.
            FileInputStream input = new FileInputStream(path);

            // 3️⃣ Initialize the Properties object and "load" the data from the file into it.
            properties = new Properties();
            properties.load(input);

            // 4️⃣ Close the stream to save computer memory (good practice!).
            input.close();

        } catch (IOException e) {
            // 🚨 Error Handling: If the file is missing or renamed, the framework stops here
            // instead of crashing later with confusing errors.
            e.printStackTrace();
            throw new RuntimeException("Could not load config.properties file.");
        }
    }

    /**
     * 🔍 GET PROPERTY: This is the method you actually call in your tests.
     * Example: ConfigReader.getProperty("url") returns "https://practicetest..."
     * * @param key The name of the setting you want (e.g., "browser")
     * @return The value associated with that key (e.g., "chrome")
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}