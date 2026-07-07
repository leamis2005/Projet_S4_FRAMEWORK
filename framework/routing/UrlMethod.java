package framework.routing;

public class UrlMethod {
    private String url;
    private String method;

    public UrlMethod(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlMethod urlMethod = (UrlMethod) o;
        return url.equals(urlMethod.url) && method.equalsIgnoreCase(urlMethod.method);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(url, method.toLowerCase());
    }
}
