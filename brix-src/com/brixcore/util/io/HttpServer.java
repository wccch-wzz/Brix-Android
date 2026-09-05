package com.brixcore.util.io;

import com.brixcore.util.Lang;
import com.brixcore.util.Logging;
import com.brixcore.util.function.ExceptionalFunction;
import com.brixcore.util.gson.JsonUtils;
import com.google.gson.JsonParseException;
import fi.iki.elonen.NanoHTTPD;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes3.dex */
public class HttpServer extends NanoHTTPD {
    protected final List<Route> routes;
    private int traceId;

    public HttpServer(int port) {
        super(port);
        this.traceId = 0;
        this.routes = new ArrayList();
    }

    public HttpServer(String hostname, int port) {
        super(hostname, port);
        this.traceId = 0;
        this.routes = new ArrayList();
    }

    public String getRootUrl() {
        return "http://localhost:" + getListeningPort();
    }

    protected void addRoute(NanoHTTPD.Method method, Pattern path, ExceptionalFunction<Request, NanoHTTPD.Response, ?> server) {
        this.routes.add(new DefaultRoute(method, path, server));
    }

    protected static NanoHTTPD.Response ok(Object response) {
        Logging.LOG.info(String.format("Response %s", JsonUtils.GSON.toJson(response)));
        return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/json", JsonUtils.GSON.toJson(response));
    }

    protected static NanoHTTPD.Response notFound() {
        return newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_HTML, "404 not found");
    }

    protected static NanoHTTPD.Response noContent() {
        return newFixedLengthResponse(NanoHTTPD.Response.Status.NO_CONTENT, NanoHTTPD.MIME_HTML, "");
    }

    protected static NanoHTTPD.Response badRequest() {
        return newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, NanoHTTPD.MIME_HTML, "400 bad request");
    }

    protected static NanoHTTPD.Response internalError() {
        return newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_HTML, "500 internal error");
    }

    @Override // fi.iki.elonen.NanoHTTPD
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        int currentId = this.traceId;
        this.traceId = currentId + 1;
        Logging.LOG.info(String.format("[%d] %s --> %s", Integer.valueOf(currentId), session.getMethod().name(), session.getUri() + ((String) Optional.ofNullable(session.getQueryParameterString()).map(new Function() { // from class: com.brixcore.util.io.HttpServer$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return HttpServer.lambda$serve$0((String) obj);
            }
        }).orElse(""))));
        NanoHTTPD.Response response = null;
        for (Route route : this.routes) {
            if (route.method == session.getMethod()) {
                Matcher pathMatcher = route.pathPattern.matcher(session.getUri());
                if (pathMatcher.find()) {
                    response = route.serve(new Request(pathMatcher, Lang.mapOf(NetworkUtils.parseQuery(session.getQueryParameterString())), session));
                    break;
                }
            }
        }
        if (response == null) {
            response = notFound();
        }
        Logging.LOG.info(String.format("[%d] %s <--", Integer.valueOf(currentId), response.getStatus()));
        return response;
    }

    static /* synthetic */ String lambda$serve$0(String s) {
        return "?" + s;
    }

    public static abstract class Route {
        NanoHTTPD.Method method;
        Pattern pathPattern;

        public abstract NanoHTTPD.Response serve(Request request);

        public Route(NanoHTTPD.Method method, Pattern pathPattern) {
            this.method = method;
            this.pathPattern = pathPattern;
        }

        public NanoHTTPD.Method getMethod() {
            return this.method;
        }

        public Pattern getPathPattern() {
            return this.pathPattern;
        }
    }

    public static class DefaultRoute extends Route {
        private final ExceptionalFunction<Request, NanoHTTPD.Response, ?> server;

        public DefaultRoute(NanoHTTPD.Method method, Pattern pathPattern, ExceptionalFunction<Request, NanoHTTPD.Response, ?> server) {
            super(method, pathPattern);
            this.server = server;
        }

        @Override // com.brixcore.util.io.HttpServer.Route
        public NanoHTTPD.Response serve(Request request) {
            try {
                return this.server.apply(request);
            } catch (JsonParseException e) {
                return HttpServer.badRequest();
            } catch (Exception e2) {
                Logging.LOG.log(Level.SEVERE, "Error handling " + request.getSession().getUri(), (Throwable) e2);
                return HttpServer.internalError();
            }
        }
    }

    public static class Request {
        Matcher pathVariables;
        Map<String, String> query;
        NanoHTTPD.IHTTPSession session;

        public Request(Matcher pathVariables, Map<String, String> query, NanoHTTPD.IHTTPSession session) {
            this.pathVariables = pathVariables;
            this.query = query;
            this.session = session;
        }

        public Matcher getPathVariables() {
            return this.pathVariables;
        }

        public Map<String, String> getQuery() {
            return this.query;
        }

        public NanoHTTPD.IHTTPSession getSession() {
            return this.session;
        }
    }
}
