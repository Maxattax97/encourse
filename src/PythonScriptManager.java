public class PythonScriptManager {
    Script[] scripts = new Script[10];
    Queue queue = new Queue();  // If data script run too long, we may need to consider a priority queue
                                // to ensure that the frontend remains responsive

    public runScript(Script script, Callback callback) {
        
    }
}

public abstract class Script {
    String path;
    int rateLimit;      // Number of seconds between runs

    public int run();   // Will most likely return output data sets
}
