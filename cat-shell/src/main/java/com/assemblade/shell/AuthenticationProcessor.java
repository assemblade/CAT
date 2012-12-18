/*
 * Copyright 2012 Mike Adamson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.assemblade.shell;

import com.assemblade.client.CallFailedException;
import com.assemblade.client.ChangePasswordException;
import com.assemblade.client.ClientException;
import com.assemblade.client.Login;
import com.assemblade.client.Users;
import com.assemblade.client.model.Authentication;
import com.assemblade.client.model.User;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Scanner;

public class AuthenticationProcessor {
    private File assembladeDirectory = new File(System.getProperty("user.home") + "/.assemblade");
    private String url;
    private Login login;

    public AuthenticationProcessor(String url) {
        this.url = url;
        this.login = new Login(url);
    }


    public Authentication authenticate() {
        Authentication authentication = null;

        while (authentication == null) {
            authentication = retrieveAuthentication();
            if (authentication == null) {
                authentication = readAuthenticationFromUser();
            }
            if (authentication != null) {
                Users users = new Users(authentication);
                try {
                    User authenticatedUser = users.getAuthenticatedUser();
                    if (authenticatedUser != null) {
                        System.out.println("Welcome " + authenticatedUser.getFullName());
                        return authentication;
                    } else {
                        authentication = null;
                        clearStoredAuthentication();
                    }
                } catch (ClientException e) {
                    authentication = null;
                    clearStoredAuthentication();
                }
            }
        }
        return null;
    }

    private Authentication readAuthenticationFromUser() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = null;

        if (System.console() != null) {
            password = String.valueOf(System.console().readPassword());
        } else {
            password = scanner.nextLine();
        }

        try {
            Authentication authentication = login.login(username, password);
            if (authentication != null) {
                storeAuthentication(authentication);
                return authentication;
            } else {
                System.err.println("");
                System.err.println("Failed to authenticate");
                System.err.println("");
            }
        } catch (ChangePasswordException e) {
        } catch (CallFailedException e) {
            System.err.println("");
            System.err.println("Failed to authenticate");
            System.err.println("");
        }
        return null;
    }

    private void storeAuthentication(Authentication authentication) {
        if (!assembladeDirectory.exists()) {
            assembladeDirectory.mkdirs();
        }

        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            Element root = document.createElement("authentication");
            document.appendChild(root);

            Element url = document.createElement("url");
            url.appendChild(document.createTextNode(authentication.getBaseUrl()));
            root.appendChild(url);
            Element token = document.createElement("token");
            token.appendChild(document.createTextNode(authentication.getToken()));
            root.appendChild(token);
            Element secret = document.createElement("secret");
            secret.appendChild(document.createTextNode(authentication.getSecret()));
            root.appendChild(secret);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(assembladeDirectory.getAbsolutePath() + "/authentication"));
            transformer.transform(source, result);
        } catch (ParserConfigurationException e) {
            System.err.println("Failed to store the authentication");
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            System.err.println("Failed to store the authentication");
            e.printStackTrace();
        } catch (TransformerException e) {
            System.err.println("Failed to store the authentication");
            e.printStackTrace();
        }
    }

    private Authentication retrieveAuthentication() {
        if (assembladeDirectory.exists()) {
            try {
                File authenticationFile = new File(assembladeDirectory.getAbsolutePath() + "/authentication");
                if (authenticationFile.exists()) {
                    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(authenticationFile);
                    document.getDocumentElement().normalize();
                    NodeList nodeList = document.getElementsByTagName("authentication");
                    for (int index = 0; index < nodeList.getLength(); index++) {
                        Node node = nodeList.item(index);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;
                            String url = element.getElementsByTagName("url").item(0).getChildNodes().item(0).getNodeValue();
                            String token = element.getElementsByTagName("token").item(0).getChildNodes().item(0).getNodeValue();
                            String secret = element.getElementsByTagName("secret").item(0).getChildNodes().item(0).getNodeValue();
                            Authentication authentication = new Authentication();
                            authentication.setBaseUrl(url);
                            authentication.setToken(token);
                            authentication.setSecret(secret);
                            return authentication;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to read authentication file");
                e.printStackTrace();
            }
        }
        return null;
    }

    private void clearStoredAuthentication() {
        File authenticationFile = new File(assembladeDirectory.getAbsolutePath() + "/authentication");
        if (authenticationFile.exists()) {
            authenticationFile.delete();
        }
    }
}
