package com.swipeid.util;

import java.io.IOException;
import java.io.Writer;

public class ConsoleWriter extends Writer {

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        System.out.println(new String(cbuf, off, len));
    }

    @Override
    public void flush() throws IOException {
        System.out.flush();
    }

    @Override
    public void close() throws IOException {
        // Do nothing
    }
}

