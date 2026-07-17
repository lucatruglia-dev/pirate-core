package lucatruglia.piratecore.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import lucatruglia.piratecore.PirateCore;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private static ConfigManager instance;
    private final Map<String, FileConfiguration> configs = new HashMap<>();
    private final Map<String, File> configFiles = new HashMap<>();

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public void initialize() {
        instance = this;
    }

    private JavaPlugin getPlugin() {
        return PirateCore.get();
    }

    /**
     * Carica un file di configurazione
     * @param filePath Percorso del file (es: "settings/levels.yml")
     * @return FileConfiguration caricato
     */
    public FileConfiguration getConfig(String filePath) {
        if (configs.containsKey(filePath)) {
            return configs.get(filePath);
        }

        JavaPlugin plugin = this.getPlugin();
        File file = new File(plugin.getDataFolder(), filePath);
        if (!file.exists()) {
            // Crea il file con i default se non esiste
            plugin.saveResource(filePath, false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        configs.put(filePath, config);
        configFiles.put(filePath, file);
        return config;
    }

    /**
     * Ricarica un file di configurazione
     */
    public void reloadConfig(String filePath) {
        JavaPlugin plugin = this.getPlugin();
        File file = new File(plugin.getDataFolder(), filePath);
        if (!file.exists()) {
            plugin.saveResource(filePath, false);
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        configs.put(filePath, config);
        configFiles.put(filePath, file);
    }

    /**
     * Salva un file di configurazione
     */
    public void saveConfig(String filePath) {
        if (!configs.containsKey(filePath)) return;
        
        File file = configFiles.get(filePath);
        try {
            configs.get(filePath).save(file);
        } catch (IOException e) {
            this.getPlugin().getLogger().severe("Impossibile salvare il file: " + filePath);
            e.printStackTrace();
        }
    }

    /**
     * Imposta un valore e salva automaticamente
     */
    public void set(String filePath, String path, Object value) {
        FileConfiguration config = getConfig(filePath);
        config.set(path, value);
        saveConfig(filePath);
    }

    /**
     * Ottiene un valore dal file di configurazione
     */
    public Object get(String filePath, String path) {
        return getConfig(filePath).get(path);
    }

    /**
     * Ottiene un valore con un default
     */
    public Object get(String filePath, String path, Object defaultValue) {
        return getConfig(filePath).get(path, defaultValue);
    }

    /**
     * Ottiene uno String
     */
    public String getString(String filePath, String path) {
        return getConfig(filePath).getString(path);
    }

    /**
     * Ottiene uno String con default
     */
    public String getString(String filePath, String path, String defaultValue) {
        return getConfig(filePath).getString(path, defaultValue);
    }

    /**
     * Ottiene un int
     */
    public int getInt(String filePath, String path) {
        return getConfig(filePath).getInt(path);
    }

    /**
     * Ottiene un int con default
     */
    public int getInt(String filePath, String path, int defaultValue) {
        return getConfig(filePath).getInt(path, defaultValue);
    }

    /**
     * Ottiene un double
     */
    public double getDouble(String filePath, String path) {
        return getConfig(filePath).getDouble(path);
    }

    /**
     * Ottiene un double con default
     */
    public double getDouble(String filePath, String path, double defaultValue) {
        return getConfig(filePath).getDouble(path, defaultValue);
    }

    /**
     * Ottiene un boolean
     */
    public boolean getBoolean(String filePath, String path) {
        return getConfig(filePath).getBoolean(path);
    }

    /**
     * Ottiene un boolean con default
     */
    public boolean getBoolean(String filePath, String path, boolean defaultValue) {
        return getConfig(filePath).getBoolean(path, defaultValue);
    }

    /**
     * Ottiene una lista
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String filePath, String path) {
        return (List<T>) getConfig(filePath).getList(path);
    }

    /**
     * Verifica se un path esiste
     */
    public boolean contains(String filePath, String path) {
        return getConfig(filePath).contains(path);
    }

    /**
     * Crea un nuovo file di configurazione con valori di default
     */
    public void createConfig(String filePath) {
        File file = new File(this.getPlugin().getDataFolder(), filePath);
        if (!file.exists()) {
            this.getPlugin().saveResource(filePath, false);
        }
    }

    /**
     * Ricarica tutti i file di configurazione
     */
    public void reloadAll() {
        for (String filePath : configs.keySet()) {
            reloadConfig(filePath);
        }
    }

    /**
     * Salva tutti i file di configurazione
     */
    public void saveAll() {
        for (String filePath : configs.keySet()) {
            saveConfig(filePath);
        }
    }
}