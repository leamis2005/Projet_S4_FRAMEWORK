package framework.utils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

public class JsonUtil {

    private static final boolean JACKSON_AVAILABLE;

    static {
        boolean available = false;
        try {
            Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
            available = true;
        } catch (ClassNotFoundException ignored) {
        }
        JACKSON_AVAILABLE = available;
    }

    public static String toJson(Object obj) {
        if (JACKSON_AVAILABLE) {
            try {
                return JacksonSerializer.serialize(obj);
            } catch (Exception e) {
                return toJsonManual(obj);
            }
        }
        return toJsonManual(obj);
    }

    private static String toJsonManual(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) return "\"" + escapeJson((String) obj) + "\"";
        if (obj instanceof Number || obj instanceof Boolean) return obj.toString();
        if (obj instanceof Collection) {
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object item : (Collection<?>) obj) {
                if (!first) sb.append(",");
                sb.append(toJsonManual(item));
                first = false;
            }
            sb.append("]");
            return sb.toString();
        }
        if (obj.getClass().isArray()) {
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (int i = 0; i < java.lang.reflect.Array.getLength(obj); i++) {
                if (!first) sb.append(",");
                sb.append(toJsonManual(java.lang.reflect.Array.get(obj, i)));
                first = false;
            }
            sb.append("]");
            return sb.toString();
        }
        if (obj instanceof Map) {
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
                if (!first) sb.append(",");
                sb.append("\"").append(escapeJson(String.valueOf(entry.getKey()))).append("\":");
                sb.append(toJsonManual(entry.getValue()));
                first = false;
            }
            sb.append("}");
            return sb.toString();
        }
        return toJsonObjectManual(obj);
    }

    private static String toJsonObjectManual(Object obj) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Method m : obj.getClass().getDeclaredMethods()) {
            String name = m.getName();
            if (name.startsWith("get") && name.length() > 3 && m.getParameterCount() == 0) {
                try {
                    Object value = m.invoke(obj);
                    if (value != null) {
                        String fieldName = Character.toLowerCase(name.charAt(3)) + name.substring(4);
                        if (!first) sb.append(",");
                        sb.append("\"").append(fieldName).append("\":").append(toJsonManual(value));
                        first = false;
                    }
                } catch (Exception ignored) {}
            } else if (name.startsWith("is") && name.length() > 2 && m.getParameterCount() == 0) {
                try {
                    Object value = m.invoke(obj);
                    if (value != null) {
                        String fieldName = Character.toLowerCase(name.charAt(2)) + name.substring(3);
                        if (!first) sb.append(",");
                        sb.append("\"").append(fieldName).append("\":").append(toJsonManual(value));
                        first = false;
                    }
                } catch (Exception ignored) {}
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    private static class JacksonSerializer {
        static String serialize(Object obj) throws Exception {
            Class<?> mapperClass = Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
            Object mapper = mapperClass.getDeclaredConstructor().newInstance();
            return (String) mapperClass.getMethod("writeValueAsString", Object.class).invoke(mapper, obj);
        }
    }
}