/*
 * Copyright 2012 Mike Adamson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.assemblade.shell.commands;

import com.assemblade.shell.Context;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListReportDefinition {
    private String title;
    private List<ListColumn> columns = new ArrayList<ListColumn>();

    public ListReportDefinition(String title) {
        this.title = title;
    }

    public void addColumn(ListColumn column) {
        columns.add(column);
    }

    public void printHeader(Context context) {
        try {
            context.println();
            context.println(title);
            printTopSeparator(context);
            for (ListColumn column : columns) {
                context.print("| ");
                context.print(pad(column.getName(), column.getWidth()));
                context.print(" ");
            }
            context.println("|");
            printMiddleSeparator(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printLine(Context context, Object object) {
        try {
            for (ListColumn column : columns) {
                context.print("| ");
                try {
                    Method method = object.getClass().getMethod(column.getMethod(), new Class<?>[]{});
                    String className = method.getReturnType().getCanonicalName();
                    String value = null;
                    if (className.equals("java.lang.String")) {
                        value = (String)method.invoke(object);
                    } else if (className.equals("boolean")) {
                        value = ((java.lang.Boolean)method.invoke(object)) ? "yes" : "no";
                    }
                    context.print(pad(value, column.getWidth()));
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                context.print(" ");
            }
            context.println("|");
            printMiddleSeparator(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printTopSeparator(Context context) throws IOException {
        printLine(context, reportWidth());
        context.println();
    }

    private void printMiddleSeparator(Context context) throws IOException {
        context.print("--");
        boolean first = true;
        for (ListColumn column : columns) {
            if (first) {
                first = false;
            } else {
                context.print("-+-");
            }
            printLine(context, column.getWidth());
        }

        context.println("--");
    }

    private void printLine(Context context, int width) throws IOException {
        for (int count = 0; count < width; count++) {
            context.print("-");
        }
    }

    private String pad(String text, int length) {
        if (text == null) {
            text = "";
        }
        if (text.length() > length) {
            return text.substring(0, length);
        }
        for (int count = text.length(); count < length; count++) {
            text += " ";
        }
        return text;
    }

    private int reportWidth() {
        int width = 0;
        for (ListColumn column : columns) {
            width += column.getWidth();
        }
        width += 4;
        width += (columns.size() - 1) * 3;

        return width;
    }
}
