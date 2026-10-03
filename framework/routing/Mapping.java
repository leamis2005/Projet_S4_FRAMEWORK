package framework.routing;

import java.lang.reflect.Method;

public class Mapping {
    private Class<?> controllerClass;
    private Method method;
    private boolean api;

    public Mapping(Class<?> controllerClass, Method method, boolean api) {
        this.controllerClass = controllerClass;
        this.method = method;
        this.api = api;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public Method getMethod() {
        return method;
    }

    public boolean isApi() {
        return api;
    }
}