/*
 * This file is part of the Solid TX project.
 *
 * Copyright (c) 2015. sha1(OWNER) = df334a7237f10846a0ca302bd323e35ee1463931
 * --> See LICENSE.txt for more information.
 *
 * @author BinaryBabel OSS (http://code.binbab.org)
 */

package net.minepass.api.gameserver.embed.solidtx;

import net.minepass.api.gameserver.embed.solidtx.log.TxLogFormatter;
import net.minepass.api.gameserver.embed.solidtx.log.TxLogOutput;

public final class TxLog {

    public enum Level {
        DEBUG, INFO, WARN, ERROR
    }

    public static TxLogOutput output = new TxLogOutput() {
        @Override
        public void sendLine(Level level, String output) {
            if (level != Level.DEBUG || TxStack.debug) {
                System.out.println(output);
            }
        }
    };

    public static TxLogFormatter formatter = new TxLogFormatter();

    protected TxStack stack;

    public TxLog(TxStack stack) {
        this.stack = stack;
    }

    public void debug(String message, Object sender) {
        log(Level.DEBUG, message, sender);
    }

    public void info(String message, Object sender) {
        log(Level.INFO, message, sender);
    }

    public void warn(String message, Object sender) {
        log(Level.WARN, message, sender);
    }

    public void error(String message, Object sender) {
        log(Level.ERROR, message, sender);
    }

    private void log(Level level, String message, Object sender) {
        String component = null;
        if (sender != null && !(sender instanceof TxStack)) {
            component = getClassName(sender);
        }
        log(level, message, stack.getStackName(), component);
    }

    static public void log(Level level, String message) {
        log(level, message, null, null);
    }

    static public void log(Level level, String message, String sender) {
        log(level, message, sender, null);
    }

    static public void log(Level level, String message, String sender, String component) {
        sender = (sender == null ? "SolidTx" : sender);
        output.sendLine(level, formatter.message(level, message, sender, component));
    }

    static protected void debugComponent(String message, Object component) {
        log(Level.DEBUG, message, null, getClassName(component));
    }

    static protected String getClassName(Object sender) {
        if (sender.getClass().equals(Class.class)) {
            return ((Class) sender).getSimpleName();
        } else {
            return sender.getClass().getSimpleName();
        }
    }

}
