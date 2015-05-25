/*
 *  This file is part of MinePass, licensed under the MIT License (MIT).
 *
 *  Copyright (c) MinePass.net <http://www.minepass.net>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package net.minepass.api.gameserver;

public class MPAsciiArt {
    public static String[] getLogo(String msg1) {
        return getLogo(msg1, "");
    }

    public static String[] getLogo(String msg1, String msg2) {
        String[] output = {
                "",
                " ---------------------------------------------------- ",
                "|           .        o                               |",
                "|_      . ~   ~ .    o                              _|",
                "  \\    ..       ..   o       M I N E P A S S       /  ",
                "   )   |  ~ . ~  |   o                            (   ",
                " _/    |    |    |   o" + wrapSpace(msg1, 29) + "\\_ ",
                "|      ..   |   ..   o" + wrapSpace(msg2, 31) + "|",
                "|         ~ v ~      o                               |",
                " ---------------------------------------------------- ",
                ""
        };

        return output;
    }

    public static String[] getNotice(String message) {
        String[] output = {
                "",
                "================ MinePass ================",
                wrapSpace(message, 42),
                "==========================================",
                ""
        };

        return output;
    }

    private static String wrapSpace(String input, int width) {
        int space = width - input.length();
        int preSpace = (space / 2);
        int postSpace = space - preSpace;

        String output = "";

        for (int i=0; i<preSpace; i++) {
            output = output.concat(" ");
        }

        output = output.concat(input);

        for (int i=0; i<postSpace; i++) {
            output = output.concat(" ");
        }

        return output;
    }

}
