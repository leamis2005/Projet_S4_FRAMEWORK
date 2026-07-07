package framework.routing;

public class RouteKey {
    private String url;
    private String httpMethod;

    public RouteKey(String url, String httpMethod) {
        this.url = url;
        this.httpMethod = httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteKey routeKey = (RouteKey) o;
        return url.equals(routeKey.url) && httpMethod.equalsIgnoreCase(routeKey.httpMethod);
    }

    @Override
    public int hashCode() {
        return url.hashCode() + httpMethod.toLowerCase().hashCode();
    }
}
