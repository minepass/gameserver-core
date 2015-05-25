/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx.log;

import net.minepass.api.gameserver.embed.solidtx.TxLog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TxLogFormatter {

    protected static final SimpleDateFormat logTimestampFormat = new SimpleDateFormat("HH:mm:ss");

    public TxLogFormatter() {
    }

    public String message(TxLog.Level level, String message, String sender, String component) {
        StringBuilder sb = new StringBuilder();

        // Date.
        sb.append("[").append(logTimestampFormat.format(new Date())).append("]");

        // Sender and Level.
        sb.append(" [").append(sender).append("/").append(formatLevel(level)).append("]");

        // Component.
        if (component != null) {
            sb.append(" [").append(component).append("]");
        }

        // Message.
        sb.append(": ").append(message);

        return sb.toString();
    }

    public String formatLevel(TxLog.Level level) {
        switch (level) {
            case DEBUG:
                return "DEBUG";
            case INFO:
                return "INFO";
            case WARN:
                return "WARN";
            case ERROR:
                return "ERROR";
        }

        return null;
    }

}
