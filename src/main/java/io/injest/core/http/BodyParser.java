/*
 * Injest - https://injest.io
 *
 * Copyright (c) 2019.
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
 * Last Modified: 6/28/19 12:14 PM
 */

package io.injest.core.http;

import io.injest.core.InjestMessages;
import io.injest.core.util.Env;
import io.injest.core.util.Log;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

final class BodyParser {

    private final HttpServerExchange exchange;
    private final HashMap<String, Deque<String>> values = new HashMap<>();
    private static final Log LOG = Log.with(BodyParser.class);

    BodyParser(HttpServerExchange exchange) {
        this.exchange = exchange;
    }

    HashMap<String, Deque<String>> parseFormData() {
        try {
            final FormParserFactory.Builder builder = FormParserFactory.builder();
            final FormDataParser formDataParser = builder.build().createParser(exchange);
            if (formDataParser != null) {
                exchange.startBlocking();
                final FormData formData = formDataParser.parseBlocking();
                for (String data : formData) {
                    final Deque<String> dataValues = new ArrayDeque<>();
                    for (FormData.FormValue formValue : formData.get(data))
                        dataValues.add(formValue.getValue());
                    this.values.put(data, dataValues);
                }
            }
        } catch (IOException e) {
            InjestMessages.errorParsingBodyParameters(exchange, e.getMessage()).toErrorLog(LOG);
            if (Env.isDevelopment())
                e.printStackTrace();
        }
        return values;
    }

    String parseRaw() {
        try {
            exchange.startBlocking();
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getInputStream()));
            String currentLine;
            while ((currentLine = reader.readLine()) != null)
                stringBuilder.append(currentLine);
            return stringBuilder.toString();
        } catch (IOException e) {
            InjestMessages.errorParsingBodyParameters(exchange, e.getMessage()).toErrorLog(LOG);
            if (Env.isDevelopment())
                e.printStackTrace();
            return "";
        }
    }
}
