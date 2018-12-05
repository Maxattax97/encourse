package edu.purdue.cs.encourse.service.impl;

public class PythonInterface {

    String commitFilePath;
    String commitCountFilePath;
    String visibleTestFilePath;
    String hiddenTestFilePath;
    String visibleProgressFilePath;
    String hiddenProgressFilePath;

    public PythonInterface() {

    }

    /*
        Commands to run at the start of any python call.
        Can be used to configure the shell.
     */
    private void startupCommands(){

    }
}
