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
    final static private char TOP_LEFT_TOP = '┍';
    final static private char TOP_RIGHT_TOP = '┑';
    final static private char TOP_CHAR = '┬';
    final static private char MID_LEFT = '├';
    final static private char MID_RIGHT = '├';
    final static private char BOTTOM_LEFT_BOTTOM = '┕';
    final static private char BOTTOM_RIGHT_BOTTOM = '┚';
    final static private char BOTTOM_CHAR = '┴';
    final static private char FIELD_NAME_SEPARATOR = '_';
    final static private String TYPE_STRING_ARRAY = String[].class.getTypeName();
    private Object target = null;

    public PrintTableUtil() {
    }

    /**
     * @param target 打印的对象
     */
    public PrintTableUtil(Object target) {
        this();
        setTarget(target);
    }

    /**
     * 将表格打印至控制台
     */
    public void printTable() {
        System.out.println(objectPrint2Table(getFieldNames(getTarget()), getValues(getTarget())));
    }

    /**
     * @return 获取表格
     */
    public StringBuilder getTable() {
        return getTable(getTarget());
    }

    /**
     * @param target 打印的对象
     * @return 获取表格
     */
    public StringBuilder getTable(Object target) {
        return objectPrint2Table(getFieldNames(target), getValues(target));
    }

    /**
     * 获取对象属性数组
     *
     * @param obj 打印的对象
     * @return 对象所有属性
     */
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

    /**
     * 获取对象属性名list 以常量的形式展示
     *
     * @param obj 打印的对象
     * @return 对象所有属性名
     */
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
                    fieldName.insert(i++, FIELD_NAME_SEPARATOR);
                }
            }
            fieldNames.add(fieldName.toString().toUpperCase());
        }

        return fieldNames;
    }

    /**
     * 获取对象所有属性值list
     * 按照属性类型获取不同的值
     * 将值直接输出：String
     * 值需要格式化：String[]
     *
     * @param obj 打印的对象
     * @return 对象所有属性值
     */
    public List<String> getValues(Object obj) {

        int validMethodNums = -1;
        Object methodReturn;
        String methodReturnType;
        Class<?> uClass;
        Field[] fields;
        List<String> uFields;
        List<Method> methods = new ArrayList<>();
        List<String> values = new ArrayList<>();

        try {
            uClass = Class.forName(obj.getClass().getCanonicalName());

            fields = getFields(obj);
            uFields = fieldsFirstToUpper(fields);

            for (String uField : uFields) {
                String methodName = "get" + uField;
                validMethodNums++;
                try {
                    methods.add(uClass.getMethod(methodName));
                } catch (NoSuchMethodException e) {
                    values.add("Can't get this value");
                    validMethodNums--;
                    continue;
                }

                methodReturn = methods.get(validMethodNums).invoke(obj);
                methodReturnType = methodReturn.getClass().getTypeName();

                if (TYPE_STRING_ARRAY.equals(methodReturnType)) {
                    methodReturn = formatStringArray((String[]) methodReturn);
                }
                values.add(String.valueOf(methodReturn));
            }

        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return values;
    }

    /**
     * 将对象所有属性名首字母大写，以调用get方法
     *
     * @param fields 对象属性集合
     * @return 对象所有属性名
     */
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

    /**
     * 将对象转换为表格主方法
     *
     * @param names  对象属性名list
     * @param values 对象属性值list
     * @return 对象转换为表格
     */
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

        for (Integer i : nameLengths) {
            topRow.setCharAt(i - 1, TOP_CHAR);
            midRow.setCharAt(i - 1, LINE_CHAR_1);
            bottomRow.setCharAt(i - 1, BOTTOM_CHAR);
        }

        topRow.setCharAt(0, TOP_LEFT_TOP);
        topRow.setCharAt(maxRow - 1, TOP_RIGHT_TOP);

        midRow.setCharAt(0, MID_LEFT);
        midRow.setCharAt(maxRow - 1, MID_RIGHT);

        bottomRow.setCharAt(0, BOTTOM_LEFT_BOTTOM);
        bottomRow.setCharAt(maxRow - 1, BOTTOM_RIGHT_BOTTOM);

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

    /**
     * 按属性类型
     * 格式化属性名与属性值
     *
     * @param names  对象属性名list
     * @param values 对象属性值list
     * @return 存放格式化属性名、属性值、属性名
     */
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

    /**
     * 以n为格式化对象，通过计算n的长度来判断
     * v在表格中的位置
     *
     * @param formatName  属性名
     * @param formatValue 属性值
     * @param n           格式化对象
     * @param v           格式化的值
     */
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

    /**
     * String[]类型特定格式化方法
     *
     * @param methodReturn 调用get方法后的返回值
     * @return 格式化后的值
     */
    private String formatStringArray(String[] methodReturn) {
        StringBuilder formatValue = new StringBuilder(methodReturn.length);
        for (String v :
                methodReturn) {
            formatValue.append(v);
            formatValue.append('\n');
        }
        formatValue.deleteCharAt(formatValue.length() - 1);

        System.out.println(formatValue.lastIndexOf("\n"));

        return String.valueOf(formatValue);
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

}
