package com.futoshita.api.server.resource.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityUtil.class);

    public static List<String> checkMissingData(Object entity) throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<String> missingData = checkMissingData(entity, null);

        if (LOGGER.isDebugEnabled()) {
            if (null != missingData) {
                LOGGER.debug("missing data: {}", missingData.toString());
            }
        }

        return missingData;
    }

    private static List<String> checkMissingData(Object entity, String classNamePrefix) throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<String> missingData = null;

        if (null != entity && null != entity.getClass().getAnnotation(XmlRootElement.class)) {
            missingData = new ArrayList<String>();
            Field[] fields = entity.getClass().getDeclaredFields();

            String className = entity.getClass().getSimpleName();
            if (null != classNamePrefix)
                className = classNamePrefix + "." + className;
            classNamePrefix = className;

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("{} entity number of fields: {}", className, fields.length);
            }

            for (Field field : fields) {
                XmlAttribute xmlAttribute = field.getAnnotation(XmlAttribute.class);
                XmlElement xmlElement = field.getAnnotation(XmlElement.class);
                XmlElementWrapper xmlElementWrapper = field.getAnnotation(XmlElementWrapper.class);

                boolean verifiable = null != xmlAttribute || null != xmlElement || null != xmlElementWrapper;

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("field {} is verifiable: {}", field.getName(), verifiable);
                }

                if (verifiable) {
                    boolean isList = List.class.isAssignableFrom(field.getType()) ? true : false;
                    boolean isMap = Map.class.isAssignableFrom(field.getType()) ? true : false;

                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("field {} {} a list", field.getName(), isList ? "is" : "is not");
                        LOGGER.debug("field {} {} a map", field.getName(), isMap ? "is" : "is not");
                    }

                    boolean required = (null != xmlAttribute && xmlAttribute.required())
                            || (null != xmlElement && xmlElement.required())
                            || (null != xmlElementWrapper && xmlElementWrapper.required());

                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("field {} is: {}", field.getName(), required ? "required" : "optional");
                    }

                    if (!isNull(field.getName(), entity, isList, isMap)) {
                        if (isList) {
                            int count = 0;

                            for (Object object : getList(field.getName(), entity)) {
                                List<String> cmd = checkMissingData(object, classNamePrefix + "." + count);
                                count++;

                                if (null != cmd) {
                                    missingData.addAll(cmd);
                                }
                            }
                        } else if (isMap) {
                            int count = 0;

                            for (Map.Entry<Object, Object> entry : getMap(field.getName(), entity).entrySet()) {
                                List<String> cmd = checkMissingData(entry.getValue(), classNamePrefix + "." + count);
                                count++;

                                if (null != cmd) {
                                    missingData.addAll(cmd);
                                }
                            }
                        } else {
                            List<String> cmd = checkMissingData(getFieldValue(field.getName(), entity), classNamePrefix);

                            if (null != cmd) {
                                missingData.addAll(cmd);
                            }
                        }
                    } else {
                        if (required) {
                            missingData.add("[" + className + "] " + field.getName());
                        }
                    }
                }
            }
        }

        return missingData;
    }

    private static boolean isNull(String fieldName, Object entity, boolean isList, boolean isMap)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        boolean isNull = true;

        if (isList) {
            List<Object> list = getList(fieldName, entity);

            if (null != list && !list.isEmpty()) {
                isNull = false;
            }
        } else if (isMap) {
            Map<Object, Object> map = getMap(fieldName, entity);

            if (null != map && !map.isEmpty()) {
                isNull = false;
            }
        } else {
            Object value = getFieldValue(fieldName, entity);

            if (null != value) {
                isNull = false;
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{} field value {} null", fieldName, isNull ? "is" : "is not");
        }

        return isNull;
    }

    private static Object getFieldValue(String fieldName, Object entity) throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method m = buildGetMethod(formatFieldName(fieldName), entity);
        Object value = m.invoke(entity, (Object[]) null);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{} field value: {}", fieldName, value);
        }

        return value;
    }

    @SuppressWarnings("unchecked")
    private static List<Object> getList(String fieldName, Object entity) throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method m = buildGetMethod(formatFieldName(fieldName), entity);
        Object value = m.invoke(entity, (Object[]) null);
        return (List<Object>) value;
    }

    @SuppressWarnings("unchecked")
    private static Map<Object, Object> getMap(String fieldName, Object entity) throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method m = buildGetMethod(formatFieldName(fieldName), entity);
        Object value = m.invoke(entity, (Object[]) null);
        return (Map<Object, Object>) value;
    }

    private static Method buildGetMethod(String fieldName, Object entity)
            throws NoSuchMethodException, SecurityException {
        String methodName = "get" + formatFieldName(fieldName);
        Method method = entity.getClass().getDeclaredMethod(methodName);
        return method;
    }

    private static String formatFieldName(String fieldName) {
        int firstLen = fieldName.offsetByCodePoints(0, 1);
        return fieldName.substring(0, firstLen).toUpperCase().concat(fieldName.substring(firstLen));
    }

}
