/*
 * Injest - https://injest.io
 *
 * Copyright (c) 2020.
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * Last Modified: 7/22/20, 9:22 AM
 */

package io.injest.core.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.injest.core.Exceptions;
import io.injest.core.boot.ConfigKeys;
import io.injest.core.boot.StaticConfig;
import io.injest.core.structs.Bundle;
import io.injest.core.util.JsonMappers;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.PathTemplateMatch;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Abstraction layer around the default undertow HttpServerExchange
 * request facet.
 */
final public class HttpRequest implements HttpExchangeFacet {

    private final HttpExchange exchange;
    private final HeaderMap headers;
    private final RequestMethod requestMethod;
    private final String requestUri;
    private final BodyParser bodyParser;
    private final HashSet<String> requiredParams;
    private final HashSet<String> missingParams = new HashSet<>();
    private final HashMap<ParameterSource, HttpParameters> params = new HashMap<>();

    private InetAddress remoteAddress;
    private String body = "";
    private String requestError;
    private boolean canIntercept = true;
    private boolean isValid = true;
    private boolean parametersInspected = false;

    private final long creationTime = System.currentTimeMillis();

    /**
     * @param exchange Undertow HttpServerExchange object
     */
    HttpRequest(HttpExchange exchange) {
        HttpServerExchange nativeExchange = exchange.getNativeExchange();
        StaticConfig config = StaticConfig.getInstance();

        this.exchange = exchange;
        this.headers = nativeExchange.getRequestHeaders();
        this.requestMethod = RequestMethod.find(nativeExchange.getRequestMethod().toString());
        this.requestUri = nativeExchange.getRequestURI();
        this.bodyParser = new BodyParser(nativeExchange);

        if (config.has(ConfigKeys.Net.FORWARDED_IP_HEADER)) {
            try {
                String forwardedIpHeader = config.getString(ConfigKeys.Net.FORWARDED_IP_HEADER).orElse("x-forwarded-for");
                this.remoteAddress = InetAddress.getByName(getHeader(forwardedIpHeader));
            } catch (UnknownHostException e) {
                this.remoteAddress = nativeExchange.getSourceAddress().getAddress();
            }
        } else {
            this.remoteAddress = nativeExchange.getSourceAddress().getAddress();
        }

        TreeMap<String, Deque<String>> pathParams = new TreeMap<>();
        PathTemplateMatch pathTemplateMatch = nativeExchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);

        // PathTemplateMatch will only match on a valid route. Check for nullity
        if (pathTemplateMatch != null) {
            for (String key : pathTemplateMatch.getParameters().keySet()) {
                Deque<String> values = new ArrayDeque<>(1);
                values.add(pathTemplateMatch.getParameters().get(key));
                pathParams.put(key, values);
            }
        }

        // Are we dealing with a body?
        final ParameterWrapper parameterWrapper;
        if (headerExists(Headers.CONTENT_LENGTH)) {

            // Get content-type
            String contentType = getHeader(Headers.CONTENT_TYPE.toString());

            // If content-type is JSON, create params without body, and set raw body string,
            // otherwise, use the body parser to parse the body as form-data
            if (ContentType.JSON.equals(contentType)) {
                this.body = bodyParser.parseRaw();
                parameterWrapper = new ParameterWrapper(
                        pathParams,
                        nativeExchange.getQueryParameters());
            } else {
                parameterWrapper = new ParameterWrapper(
                        pathParams,
                        nativeExchange.getQueryParameters(),
                        bodyParser.parseFormData());
            }
        } else {
            parameterWrapper = new ParameterWrapper(
                    pathParams,
                    nativeExchange.getQueryParameters());
        }

        this.params.put(ParameterSource.ANY, new HttpParameters(parameterWrapper, ParameterSource.ANY));
        this.params.put(ParameterSource.PATH, new HttpParameters(parameterWrapper, ParameterSource.PATH));
        this.params.put(ParameterSource.QUERY, new HttpParameters(parameterWrapper, ParameterSource.QUERY));
        this.params.put(ParameterSource.BODY, new HttpParameters(parameterWrapper, ParameterSource.BODY));

        RequiredParameters requiredParameters = getHandler().getAttachment(RequiredParameters.ATTACHMENT_KEY);
        this.requiredParams = requiredParameters != null ? requiredParameters.getValues() : new HashSet<>();
    }

    /**
     * Gets the request method for the request as
     * a RequestMethod enum
     *
     * @return RequestMethod enum
     */
    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    /**
     * Gets the HttpResponse associated with this request
     *
     * @return HttpResponse
     */
    public HttpResponse getResponse() {
        return exchange.getResponse();
    }

    /**
     * Gets the IP information for the request
     *
     * @return remote IP information
     */
    public InetAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    /**
     * Gets the request's content-type
     *
     * @return content-type as String
     */
    public String getContentType() {
        return exchange.getRequest().getContentType();
    }

    /**
     * Ascertain the presence of a request header with string value of headerName
     *
     * @param headerName String instance of the key's value
     * @return if it exists
     */
    public boolean headerExists(String headerName) {
        return headers.contains(headerName);
    }

    /**
     * Ascertain the presence of a request header with HttpString value of headerName
     *
     * @param headerName HttpString instance of the key's string value
     * @return if it exists
     */
    public boolean headerExists(HttpString headerName) {
        return headers.contains(headerName);
    }

    /**
     * Gets the specified request header value
     *
     * @param headerName name of the header
     * @return string value of header
     */
    public String getHeader(String headerName) {
        if (headerExists(headerName)) {
            return headers.get(headerName).getFirst();
        }
        return null;
    }

    /**
     * Gets the current request URI path
     *
     * @return request URI path
     */
    public String getRequestUri() {
        return requestUri;
    }

    /**
     * Get the user agent string
     *
     * @return user agent string
     */
    public String getUserAgent() {
        return getHeader("User-Agent");
    }

    /**
     * Determine if a cookie exists
     *
     * @param cookieName cookie name
     * @return if cookie exists
     */
    public boolean cookieExists(String cookieName) {
        return exchange.getNativeExchange().getRequestCookies().containsKey(cookieName);
    }

    /**
     * Gets a specified cookie
     *
     * @param cookieName cookie name
     * @return HTTP cookie
     */
    public Cookie getCookie(String cookieName) {
        return exchange.getNativeExchange().getRequestCookies().get(cookieName);
    }

    /**
     * Gets the HttpParameters bundle
     *
     * @return HttpParameters bundle
     */
    public HttpParameters params() {
        return params.get(ParameterSource.ANY);
    }

    /**
     * Gets query string parameters
     *
     * @return HttpParameters
     */
    public HttpParameters query() {
        return params.get(ParameterSource.QUERY);
    }

    /**
     * Gets body (form-data) parameters
     *
     * @return HttpParameters
     */
    public HttpParameters body() {
        return params.get(ParameterSource.BODY);
    }

    /**
     * Gets path (url) parameters
     *
     * @return HttpParameters
     */
    public HttpParameters path() {
        return params.get(ParameterSource.PATH);
    }

    /**
     * Gets the raw body of the request. Returns an Optional of type string
     * and will not be present if the request did not supply a content-length
     * header
     *
     * @return Raw body as String
     */
    public Optional<String> raw() {
        if (this.body == null)
            this.body = this.bodyParser.parseRaw();
        return Optional.ofNullable(this.body);
    }

    /**
     * Gets JSON body as Object
     *
     * @param jsonType Object type JSON will be deserialized to
     * @param <T>      expectant type
     * @return Object with JSON data or <code>null</code> if serialization failed
     */
    public <T> Optional<T> json(Class<T> jsonType) {
        return json(jsonType, JsonMappers.serializationDefault());
    }

    /**
     * Gets JSON body as Object with custom mapper
     *
     * @param jsonType Object type JSON will be deserialized to
     * @param mapper   a user-provided ObjectMapper instance
     * @param <T>      expectant type
     * @return Object with JSON data or <code>null</code> if serialization failed
     */
    public <T> Optional<T> json(Class<T> jsonType, ObjectMapper mapper) {
        Optional<String> body = this.raw();
        if (!body.isPresent())
            return Optional.empty();
        try {
            return Optional.ofNullable(mapper.readValue(body.get(), jsonType));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets JSON body as an array of objects
     *
     * @param elementType target element type
     * @param <T>         target list type
     * @return List containing objects of type T
     */
    public <T> Optional<List<T>> jsonList(Class<T> elementType) {
        return jsonList(elementType, JsonMappers.serializationDefault());
    }

    /**
     * Gets JSON body as an array of objects using defined mapper
     *
     * @param elementType target element type
     * @param mapper      a user-provided objectMapper instance
     * @param <T>         target list type
     * @return List containing objects of type T
     */
    public <T> Optional<List<T>> jsonList(Class<T> elementType, ObjectMapper mapper) {
        Optional<String> body = this.raw();
        if (!body.isPresent())
            return Optional.empty();
        try {
            List<T> result = mapper.readValue(body.get(), mapper.getTypeFactory().constructCollectionType(List.class, elementType));
            return Optional.ofNullable(result);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Require path/store/body parameters for this request
     *
     * @param args one or more argument keys to enforce
     */
    public void requireParameters(String... args) {
        if (parametersInspected) {
            throw Exceptions.parametersInspectedAlready();
        }
        requiredParams.addAll(Arrays.asList(args));
    }

    /**
     * Inspect and see which required parameters (if any)
     * are missing from the HttpParameters class
     *
     * @return if all required are present
     */
    boolean hasAllRequiredParameters() {
        if (requiredParams.size() == 0)
            return true;
        for (String requiredParam : requiredParams) {
            if (!params().has(requiredParam)) {
                missingParams.add(requiredParam);
            }
        }
        parametersInspected = true;
        return missingParams.size() == 0;
    }

    /**
     * Get the string set of missing parameter keys
     *
     * @return missing parameter set
     */
    Set<String> getMissingParams() {
        return missingParams;
    }

    /**
     * Determine of the request is valid or if it was invalidated
     * by a request interceptor
     *
     * @return request validity
     */
    public boolean isValid() {
        return this.isValid;
    }


    /**
     * Invalidate current request. Once invalidated, the
     * HTTP handler will not be invoked. Response will be
     * delegated to any response security set
     */
    public void invalidate() {
        this.isValid = false;
    }

    /**
     * Invalidate current request. Once invalidated, the
     * HTTP handler will not be invoked. Response will be
     * delegated to any response security set
     *
     * @param errorMessage error description
     */
    public void invalidate(String errorMessage) {
        this.invalidate();
        this.assignError(errorMessage);
    }

    /**
     * Invalidate current request. Once invalidated, the
     * HTTP handler will not be invoked. Response will be
     * delegated to any response security set
     *
     * @param errorMessage error description
     * @param args         object arguments, to be joined with spaces
     */
    public void invalidate(String errorMessage, Object... args) {
        String argString = Stream.of(args)
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining(" "));
        this.invalidate(errorMessage + " " + argString);
    }

    /**
     * Add an error message to the request
     *
     * @param errorMessage name or description of the error
     */
    public void assignError(String errorMessage) {
        this.requestError = errorMessage;
    }

    /**
     * Get the error message from this request
     *
     * @return error description
     */
    public String getRequestError() {
        return this.requestError;
    }

    /**
     * Gets the timestamp of when the request was created
     *
     * @return timestamp in milliseconds
     */
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public Handler<?> getHandler() {
        return exchange.getCurrentHandler();
    }

    @Override
    public HttpServerExchange getExchange() {
        return exchange.getNativeExchange();
    }

    @Override
    public Bundle data() {
        return exchange.getExchangeData();
    }

    /**
     * Stop request intercept pipeline
     */
    @Override
    public void endIntercepts() {
        this.canIntercept = false;
    }

    /**
     * Determine whether the request can still
     * be intercepted
     *
     * @return boolean
     */
    @Override
    public boolean canIntercept() {
        return canIntercept;
    }
}
