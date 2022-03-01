package com.alsw.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrintTableUtil {
    final static private char SPACE = ' ';
    final static private char LINE = '┈';
    final static private char LINE_CHAR_1 = '┼';
    final static private char LINE_CHAR_2 = '┊';
    final static private char LEFT_TOP = '┍';
    final static private char TOP_CHAR = '┬';
    final static private char RIGHT_TOP = '┑';
    final static private char LEFT_BOTTOM = '┕';
    final static private char BOTTOM_CHAR = '┴';
    final static private char RIGHT_BOTTOM = '┚';
    private Object target = null;

    public PrintTableUtil() {
    }

    public PrintTableUtil(Object target) {
        this();
        setTarget(target);
    }

    public void printTable() {
        System.out.println(objectPrint2Table(getFieldNames(getTarget()), getValues(getTarget())));
    }

    public StringBuilder getTable() {
        return getTable(getTarget());
    }

    public StringBuilder getTable(Object target) {
        return objectPrint2Table(getFieldNames(target), getValues(target));
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

    public List<String> getFieldNames(Object obj) {
        List<String> fieldNames = new ArrayList<>();
        Field[] fields = getFields(obj);

        for (Field f : fields) {
            StringBuilder fieldName = new StringBuilder(f.getName());
            for (int i = 0; i < fieldName.length(); i++) {
                if (f.getModifiers() == (Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL)) {
                    break;
                }

                if (fieldName.charAt(i) >= 65 && fieldName.charAt(i) <= 90) {
                    fieldName.insert(i++, '_');
                }
            }
            fieldNames.add(fieldName.toString().toUpperCase());
        }

        return fieldNames;
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
                String methodName = "get" + uFields.get(i);

                try {
                    methods.add(uClass.getMethod(methodName));
                } catch (NoSuchMethodException e) {
                    values.add("Can't get this value");
                    continue;
                }

                values.add((String) methods.get(i).invoke(obj));
            }

        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return values;
    }

    private List<String> fieldsFirstToUpper(Field[] fields) {
        List<String> fieldNames = new ArrayList<>();

        for (Field field : fields) {
            String fieldName = field.getName();

            if (fieldName.charAt(0) >= 65 && fieldName.charAt(0) <= 90) {
                continue;
            }
            char[] chars = fieldName.toCharArray();
            chars[0] -= 32;
            fieldNames.add(String.valueOf(chars));
        }

        return fieldNames;
    }

    private StringBuilder objectPrint2Table(List<String> names, List<String> values) {
        StringBuilder table = new StringBuilder();
        Map<String, Object> map = formatList(names, values);

        List<StringBuilder> formatList = (List<StringBuilder>) map.get("formatList");
        List<Integer> nameLengths = (List<Integer>) map.get("nameLengths");

        StringBuilder nameRow = formatList.get(0);

        int maxRow = nameRow.length();

        StringBuilder topRow = new StringBuilder(maxRow);
        StringBuilder midRow = new StringBuilder(maxRow);

        for (int i = 0; i < maxRow; i++) {
            topRow.append(LINE);
            midRow.append(LINE);
        }
        StringBuilder bottomRow = new StringBuilder(topRow);

        for (Integer i :
                nameLengths) {
            topRow.setCharAt(i - 1, TOP_CHAR);
            midRow.setCharAt(i - 1, LINE_CHAR_1);
            bottomRow.setCharAt(i - 1, BOTTOM_CHAR);
        }

        topRow.setCharAt(0, LEFT_TOP);
        topRow.setCharAt(maxRow - 1, RIGHT_TOP);

        midRow.setCharAt(0, '├');
        midRow.setCharAt(maxRow - 1, '┤');

        bottomRow.setCharAt(0, LEFT_BOTTOM);
        bottomRow.setCharAt(maxRow - 1, RIGHT_BOTTOM);

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

        formatName.append(LINE_CHAR_2);
        formatValue.append(LINE_CHAR_2);

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
            formatName.append(LINE_CHAR_2);
            nameLengths.add(formatName.length());
            formatValue.append(LINE_CHAR_2);
        }

        formatName.setCharAt(formatName.length() - 1, LINE_CHAR_2);
        formatValue.setCharAt(formatValue.length() - 1, LINE_CHAR_2);

        formatList.add(formatName);
        formatList.add(formatValue);

        listMap.put("formatList", formatList);
        listMap.put("nameLengths", nameLengths);

        return listMap;
    }

    private void getFormat(StringBuilder formatName, StringBuilder formatValue, String n, String v) {
        String vStr = SPACE + v + SPACE;
        int tSpaceNum = Math.abs(vStr.length() - n.length());

        for (int j = 0; j < tSpaceNum; j++) {
            formatName.append(SPACE);
            if (j == (tSpaceNum - 1) / 2) {
                formatName.append(n);
            }
        }

        if (tSpaceNum <= 1) {
            formatName.append(SPACE);
            vStr += SPACE;
        }

        formatValue.append(vStr);
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

}
