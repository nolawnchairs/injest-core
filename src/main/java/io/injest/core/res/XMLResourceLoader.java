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
 * Last Modified: 5/30/19 10:28 AM
 */

package io.injest.core.res;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

abstract public class XMLResourceLoader implements Runnable {

    private final String resourcePath;
    private final HashMap<String, String> values = new HashMap<>();

    XMLResourceLoader(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    protected abstract void onDocumentParsed(Map<String, String> values);

    @Override
    public void run() {

        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(is);
            Element root = document.getDocumentElement();
            NodeList nodes = root.getElementsByTagName("string");

            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                String key = node.getAttributes().getNamedItem("key").getNodeValue();
                String value = node.getAttributes().getNamedItem("value").getNodeValue();
                values.put(key, value);
            }

            onDocumentParsed(values);
        } catch (IOException
                | ParserConfigurationException
                | SAXException e) {
            e.printStackTrace();
        }
    }
}
