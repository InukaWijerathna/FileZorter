import java.util.regex.Pattern
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

/**
 * Constants and Configuration for FileZorter
 */
class ZorterConstants {
    // Default categories
    static Map DEFAULT_CATEGORIES = [
        'Images'     : ['.jpg', '.jpeg', '.png', '.gif', '.svg', '.heic', '.webp', '.bmp', '.ico', '.tif', '.tiff'],
        'Documents'  : ['.pdf', '.docx', '.txt', '.xlsx', '.pptx', '.csv', '.rtf', '.odt', '.ods', '.odp', '.md'],
        'Code'       : ['.groovy', '.java', '.py', '.html', '.css', '.js', '.json', '.c', '.cpp', '.h', '.cs', '.php', '.sql', '.xml', '.yaml', '.yml'],
        'Media'      : ['.mp4', '.mov', '.mp3', '.wav', '.mkv', '.avi', '.wmv', '.flv', '.m4a', '.flac', '.ogg'],
        'Compressed' : ['.zip', '.rar', '.7z', '.tar', '.gz', '.bz2', '.xz', '.iso', '.dmg', '.pkg'],
        'Fonts'      : ['.ttf', '.otf', '.woff', '.woff2', '.eot'],
        'Design'     : ['.psd', '.ai', '.xd', '.fig', '.sketch', '.dwg', '.dxf'],
        'Installers' : ['.exe', '.msi', '.apk', '.app', '.jar']
    ]

    // Configurable state
    static Map CATEGORIES = [:]
    static String junkPatternString = '(?i).*\\.(tmp|log|crdownload|bak)$'
    static boolean alwaysOnTop = true

    static final String CONFIG_FILE = "config.json"

    static {
        loadConfig()
    }

    static Pattern getJunkPattern() {
        try {
            return Pattern.compile(junkPatternString)
        } catch (Exception e) {
            return ~/(?i).*\.(tmp|log|crdownload|bak)$/
        }
    }

    static void loadConfig() {
        File file = new File(CONFIG_FILE)
        if (file.exists()) {
            try {
                def config = new JsonSlurper().parse(file)
                CATEGORIES = config.categories ?: new HashMap(DEFAULT_CATEGORIES)
                junkPatternString = config.junkPatternString ?: '(?i).*\\.(tmp|log|crdownload|bak)$'
                alwaysOnTop = config.alwaysOnTop != null ? config.alwaysOnTop : true
            } catch (Exception e) {
                println "Error loading config: ${e.message}. Using defaults."
                resetToDefaults()
            }
        } else {
            resetToDefaults()
            saveConfig()
        }
    }

    static void resetToDefaults() {
        CATEGORIES = new HashMap(DEFAULT_CATEGORIES)
        junkPatternString = '(?i).*\\.(tmp|log|crdownload|bak)$'
        alwaysOnTop = true
    }

    static void saveConfig() {
        def config = [
            categories: CATEGORIES,
            junkPatternString: junkPatternString,
            alwaysOnTop: alwaysOnTop
        ]
        File file = new File(CONFIG_FILE)
        file.text = JsonOutput.prettyPrint(JsonOutput.toJson(config))
    }
}
