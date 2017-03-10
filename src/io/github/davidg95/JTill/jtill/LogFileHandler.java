/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.JTill.jtill;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Log file handler class for logging the servers events to the log file.
 *
 * @author David
 */
public class LogFileHandler extends Handler {

    private static LogFileHandler handler;

    private File file;
    private FileOutputStream out;

    public LogFileHandler() {
        openFile();
    }

    /**
     * Returns an instance of the log file handler. If one does not exist
     * already, then it will create one.
     *
     * @return LogFileHandler.
     */
    public static LogFileHandler getInstance() {
        if (handler == null) {
            handler = new LogFileHandler();
        }
        return handler;
    }

    @Override
    public void publish(LogRecord record) {
        try {
            String eol = System.getProperty("line.separator");
            out.write(("[" + new Date(record.getMillis()) + "] " + record.getLevel().toString() + " " + record.getMessage()+ eol).getBytes());
        } catch (IOException ex) {
            Logger.getLogger(LogFileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void flush() {
        try {
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(LogFileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void close() throws SecurityException {
        try {
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(LogFileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void openFile() {
        file = new File("log.txt");
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LogFileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
