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
import java.text.SimpleDateFormat;
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

    private File file;
    private FileOutputStream out;

    private final String directory;

    public LogFileHandler(String directory) {
        this.directory = directory;
        openFile();
    }

    @Override
    public void publish(LogRecord record) {
        try {
            String eol = System.getProperty("line.separator");
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            String start = "[" + df.format(new Date(record.getMillis())) + "] [" + record.getLevel().toString() + "]- ";
            if (record.getThrown() == null) {
                if (record.getMessage() != null) {
                    out.write((start + record.getMessage() + eol).getBytes());
                }
            } else {
                if (record.getMessage() != null) {
                    out.write((start + record.getMessage() + eol + record.getThrown() + eol).getBytes());
                } else {
                    out.write((start + record.getThrown() + eol).getBytes());
                }
            }
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
        file = new File(directory + "log.txt");
        try {
            out = new FileOutputStream(file);
            try {
                out.write(("JTill log " + new Date() + System.getProperty("line.separator")).getBytes());
            } catch (IOException ex) {
                Logger.getLogger(LogFileHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LogFileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
