package com.alsw.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrintTableUtil {
    Object target = null;

    private char space = ' ';
    private char line = '┈';
    private char lineChar = '┼';
    private char lineChar2 = '┊';

    private char leftTop = '┍';
    private char topChar = '┬';
    private char rightTop = '┑';

    private char leftBottom = '┕';
    private char bottomChar = '┴';
    private char rightBottom = '┚';

    public PrintTableUtil() {
    }

    public PrintTableUtil(Object target) {
        this.target = target;
    }

    public void print() {
        System.out.println(objectPrint2Table(getFieldNames(target), getValues(target)));
    }

    public StringBuilder getTable() {
        return getTable(target);
    }

    public StringBuilder getTable(Object target) {
        List<String> fieldNames = getFieldNames(target);
        List<String> values = getValues(target);
        return objectPrint2Table(fieldNames, values);
    }

    public List<String> getFieldNames(Object obj) {
        List<String> fieldNames = new ArrayList<>();
        Field[] fields = getFields(obj);

        for (Field f :
                fields) {
            fieldNames.add(f.getName().toUpperCase());
        }

        return fieldNames;
    }

    public Field[] getFields(Object obj) {
        Class<?> tClass;
        Field[] fields = new Field[0];

        try {
            tClass = Class.forName(obj.getClass().getCanonicalName());
            fields = tClass.getDeclaredFields();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return fields;
    }

    public List<String> getValues(Object obj) {

        Class<?> uClass;
        Field[] fields;
        List<String> uFields;
        List<Method> methods = new ArrayList<>();
        List<String> values = new ArrayList<>();

        try {
            uClass = Class.forName(obj.getClass().getCanonicalName());

            fields = getFields(obj);
            uFields = fieldsFirstToUpper(fields);

            for (int i = 0; i < uFields.size(); i++) {
                methods.add(uClass.getMethod("get" + uFields.get(i)));
                values.add((String) methods.get(i).invoke(obj));
            }

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return values;
    }

    public List<String> fieldsFirstToUpper(Field[] fields) {
        List<String> fieldNames = new ArrayList<>();

        for (Field field :
                fields) {
            char[] chars = field.getName().toCharArray();
            chars[0] -= 32;
            fieldNames.add(String.valueOf(chars));
        }

        return fieldNames;
    }

    public StringBuilder objectPrint2Table(List<String> names, List<String> values) {
        StringBuilder table = new StringBuilder();
        Map<String, Object> map = formatList(names, values);

        List<StringBuilder> formatList = (List<StringBuilder>) map.get("formatList");
        List<Integer> nameLengths = (List<Integer>) map.get("nameLengths");

        StringBuilder nameRow = formatList.get(0);

        int maxRow = nameRow.length();

        StringBuilder topRow = new StringBuilder(maxRow);
        StringBuilder midRow = new StringBuilder(maxRow);

        for (int i = 0; i < maxRow; i++) {
            topRow.append(line);
            midRow.append(line);
        }
        StringBuilder bottomRow = new StringBuilder(topRow);

        for (Integer i :
                nameLengths) {
            topRow.setCharAt(i - 1, topChar);
            midRow.setCharAt(i - 1, lineChar);
            bottomRow.setCharAt(i - 1, bottomChar);
        }

        topRow.setCharAt(0, leftTop);
        topRow.setCharAt(maxRow - 1, rightTop);

        midRow.setCharAt(0, '├');
        midRow.setCharAt(maxRow - 1, '┤');

        bottomRow.setCharAt(0, leftBottom);
        bottomRow.setCharAt(maxRow - 1, rightBottom);

        table.append(topRow);
        table.append('\n');
        for (int i = 0; i < formatList.size(); i++) {
            table.append(formatList.get(i));
            table.append('\n');

            if (i < formatList.size() - 1) {
                table.append(midRow);
                table.append('\n');

            }
        }
        table.append(bottomRow);

        return table;
    }

    private Map<String, Object> formatList(List<String> names, List<String> values) {
        Map<String, Object> listMap = new HashMap<>();

        List<Object> formatList = new ArrayList<>();
        StringBuilder formatName = new StringBuilder();
        StringBuilder formatValue = new StringBuilder();

        List<Integer> nameLengths = new ArrayList<>();

        formatName.append(lineChar2);
        formatValue.append(lineChar2);

        for (int i = 0; i < names.size(); i++) {
            String n = names.get(i);
            String v;

            if (values.get(i) == null) {
                v = "null";
            } else {
                v = values.get(i);
            }

            if (v.length() >= n.length()) {
                getFormat(formatName, formatValue, n, v);
            } else {
                getFormat(formatValue, formatName, v, n);
            }
            formatName.append(lineChar2);
            nameLengths.add(formatName.length());
            formatValue.append(lineChar2);
        }

        formatName.setCharAt(formatName.length() - 1, lineChar2);
        formatValue.setCharAt(formatValue.length() - 1, lineChar2);

        formatList.add(formatName);
        formatList.add(formatValue);

        listMap.put("formatList", formatList);
        listMap.put("nameLengths", nameLengths);

        return listMap;
    }

    private void getFormat(StringBuilder formatName, StringBuilder formatValue, String n, String v) {
        String vStr = space + v + space;
        int tSpaceNum = Math.abs(vStr.length() - n.length());

        for (int j = 0; j < tSpaceNum; j++) {
            formatName.append(space);
            if (j == (tSpaceNum - 1) / 2) {
                formatName.append(n);
            }
        }

        if (tSpaceNum <= 1) {
            formatName.append(space);
            vStr += space;
        }

        formatValue.append(vStr);
    }


    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public char getSpace() {
        return space;
    }

    public void setSpace(char space) {
        this.space = space;
    }

    public char getLine() {
        return line;
    }

    public void setLine(char line) {
        this.line = line;
    }

    public char getLineChar() {
        return lineChar;
    }

    public void setLineChar(char lineChar) {
        this.lineChar = lineChar;
    }

    public char getLineChar2() {
        return lineChar2;
    }

    public void setLineChar2(char lineChar2) {
        this.lineChar2 = lineChar2;
    }

    public char getLeftTop() {
        return leftTop;
    }

    public void setLeftTop(char leftTop) {
        this.leftTop = leftTop;
    }

    public char getTopChar() {
        return topChar;
    }

    public void setTopChar(char topChar) {
        this.topChar = topChar;
    }

    public char getRightTop() {
        return rightTop;
    }

    public void setRightTop(char rightTop) {
        this.rightTop = rightTop;
    }

    public char getLeftBottom() {
        return leftBottom;
    }

    public void setLeftBottom(char leftBottom) {
        this.leftBottom = leftBottom;
    }

    public char getBottomChar() {
        return bottomChar;
    }

    public void setBottomChar(char bottomChar) {
        this.bottomChar = bottomChar;
    }

    public char getRightBottom() {
        return rightBottom;
    }

    public void setRightBottom(char rightBottom) {
        this.rightBottom = rightBottom;
    }


}
