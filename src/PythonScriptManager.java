public class PythonScriptManager {
    Script[] scripts = new Script[10];
    
    public runScript(Script script, Callback callback) {
        
    }

}

public abstract class Script {
    String path;
    int rateLimit;      // Number of seconds between runs

    public int run();   // Will most likely return output data sets
}
