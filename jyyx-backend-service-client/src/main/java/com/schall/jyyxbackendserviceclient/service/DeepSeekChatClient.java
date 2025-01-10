////package com.schall.jyyxbackendserviceclient.service;
////
////import com.fasterxml.jackson.databind.ObjectMapper;
////import com.fasterxml.jackson.databind.node.ObjectNode;
////import com.fasterxml.jackson.databind.node.ArrayNode;
////import com.schall.jyyxbackendserviceclient.service.config.ConfigLoader;
////import org.apache.hc.client5.http.classic.methods.HttpPost;
////import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
////import org.apache.hc.client5.http.impl.classic.HttpClients;
////import org.apache.hc.core5.http.io.entity.StringEntity;
////import org.apache.hc.core5.http.io.entity.EntityUtils;
////import org.apache.hc.core5.http.ContentType;
////
////import java.nio.charset.StandardCharsets;
////import java.io.IOException;
////import java.util.Map;
////
////public class DeepSeekChatClient {
////
////    public static void main(String[] args) {
////        try {
////            // 加载配置文件
////            Map<String, Object> config = ConfigLoader.loadConfig();
////            String apiUrl = (String) ((Map<String, Object>) config.get("deepseek")).get("api_url");
////            String apiKey = (String) ((Map<String, Object>) config.get("deepseek")).get("api_key");
////
////            // 发送请求并获取响应
////            String response = sendChatRequest(apiUrl, apiKey);
////            System.out.println("API Response: " + response);
////
////        } catch (IOException e) {
////            System.err.println("配置文件加载失败: " + e.getMessage());
////            e.printStackTrace();
////        }
////    }
////
////    /**
////     * 发送聊天请求到DeepSeek API并获取响应
////     *
////     * @param apiUrl API的URL
////     * @param apiKey API密钥
////     * @return API的响应内容
////     * @throws IOException 网络请求异常
////     */
////    public static String sendChatRequest(String apiUrl, String apiKey) throws IOException {
////        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
////            HttpPost httpPost = new HttpPost(apiUrl);
////
////            // 设置请求头
////            httpPost.setHeader("Authorization", "Bearer " + apiKey);
////            httpPost.setHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
////
////            // 创建JSON请求体
////            ObjectMapper objectMapper = new ObjectMapper();
////            ObjectNode root = objectMapper.createObjectNode();
////            ArrayNode messages = root.putArray("messages");
////
////            ObjectNode systemMessage = messages.addObject();
////            systemMessage.put("role", "system");
////            systemMessage.put("content", "You are a helpful assistant.");
////
////            ObjectNode userMessage = messages.addObject();
////            userMessage.put("role", "user");
////            userMessage.put("content", "华南师范大学在哪里请你回答我");
////
////            root.put("model", "deepseek-chat");
////
////            String jsonPayload = root.toString();
////            System.out.println("Request payload: " + jsonPayload);
////
////            // 设置实体并指定字符编码为UTF-8
////            httpPost.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));
////
////            // 执行请求并处理响应
////            String responseString = httpClient.execute(httpPost, response -> {
////                int statusCode = response.getCode();
////                if (statusCode >= 200 && statusCode < 300) {
////                    return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
////                } else {
////                    throw new IOException("Unexpected response status: " + statusCode);
////                }
////            });
////
////            // 解析JSON响应
////            ObjectMapper responseMapper = new ObjectMapper();
////            ObjectNode jsonResponse = responseMapper.readValue(responseString, ObjectNode.class);
////            String content = jsonResponse.path("choices").path(0).path("message").path("content").asText();
////            return content;
////        }
////    }
////}
////package com.schall.jyyxbackendserviceclient.service;
////
////import com.fasterxml.jackson.databind.ObjectMapper;
////import com.fasterxml.jackson.databind.node.ObjectNode;
////import com.fasterxml.jackson.databind.node.ArrayNode;
////import com.schall.jyyxbackendserviceclient.service.config.ConfigLoader;
////import org.apache.hc.client5.http.classic.methods.HttpPost;
////import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
////import org.apache.hc.client5.http.impl.classic.HttpClients;
////import org.apache.hc.core5.http.io.entity.StringEntity;
////import org.apache.hc.core5.http.io.entity.EntityUtils;
////import org.apache.hc.core5.http.ContentType;
////
////import java.nio.charset.StandardCharsets;
////import java.io.BufferedReader;
////import java.io.IOException;
////import java.io.InputStreamReader;
////import java.util.ArrayList;
////import java.util.HashMap;
////import java.util.List;
////import java.util.Map;
////
////public class DeepSeekChatClient {
////
////    private static final String SYSTEM_MESSAGE = "You are a helpful assistant.";
////
////    private List<Map<String, String>> messages;
////
////    public DeepSeekChatClient() {
////        messages = new ArrayList<>();
////        // 添加系统消息
////        messages.add(new HashMap<String, String>() {{
////            put("role", "system");
////            put("content", SYSTEM_MESSAGE);
////        }});
////    }
////
////    public static void main(String[] args) {
////        try {
////            // 加载配置文件
////            Map<String, Object> config = ConfigLoader.loadConfig();
////            String apiUrl = (String) ((Map<String, Object>) config.get("deepseek")).get("api_url");
////            String apiKey = (String) ((Map<String, Object>) config.get("deepseek")).get("api_key");
////
////            // 创建聊天客户端实例
////            DeepSeekChatClient chatClient = new DeepSeekChatClient();
////
////            // 维持对话框
////            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
////            while (true) {
////                System.out.print("User: ");
////                String userMessage = reader.readLine();
////                if ("exit".equalsIgnoreCase(userMessage)) {
////                    break;
////                }
////
////                String response = chatClient.sendChatRequest(apiUrl, apiKey, userMessage);
////                System.out.println("Assistant: " + response);
////            }
////
////        } catch (IOException e) {
////            System.err.println("配置文件加载失败: " + e.getMessage());
////            e.printStackTrace();
////        }
////    }
////
////    /**
////     * 发送聊天请求到DeepSeek API并获取响应
////     *
////     * @param apiUrl     API的URL
////     * @param apiKey     API密钥
////     * @param userMessage 用户消息
////     * @return API的响应内容
////     * @throws IOException 网络请求异常
////     */
////    public String sendChatRequest(String apiUrl, String apiKey, String userMessage) throws IOException {
////        // 添加用户消息到对话历史
////        messages.add(new HashMap<String, String>() {{
////            put("role", "user");
////            put("content", userMessage);
////        }});
////
////        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
////            HttpPost httpPost = new HttpPost(apiUrl);
////
////            // 设置请求头
////            httpPost.setHeader("Authorization", "Bearer " + apiKey);
////            httpPost.setHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
////
////            // 创建JSON请求体
////            ObjectMapper objectMapper = new ObjectMapper();
////            ObjectNode root = objectMapper.createObjectNode();
////            ArrayNode messagesNode = root.putArray("messages");
////
////            // 将对话历史添加到请求体中
////            for (Map<String, String> message : messages) {
////                ObjectNode msg = messagesNode.addObject();
////                msg.put("role", message.get("role"));
////                msg.put("content", message.get("content"));
////            }
////
////            root.put("model", "deepseek-chat");
////
////            String jsonPayload = root.toString();
////            System.out.println("Request payload: " + jsonPayload);
////
////            // 设置实体并指定字符编码为UTF-8
////            httpPost.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));
////
////            // 执行请求并处理响应
////            String responseString = httpClient.execute(httpPost, response -> {
////                int statusCode = response.getCode();
////                if (statusCode >= 200 && statusCode < 300) {
////                    return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
////                } else {
////                    throw new IOException("Unexpected response status: " + statusCode);
////                }
////            });
////
////            // 解析JSON响应
////            ObjectMapper responseMapper = new ObjectMapper();
////            ObjectNode jsonResponse = responseMapper.readValue(responseString, ObjectNode.class);
////            String content = jsonResponse.path("choices").path(0).path("message").path("content").asText();
////
////            // 添加助理的回答到对话历史
////            messages.add(new HashMap<String, String>() {{
////                put("role", "assistant");
////                put("content", content);
////            }});
////
////            return content;
////        }
////    }
////}
//
////----------------------------------------------这个是智能分析代码客户端完全可以完成业务----------------------------------------------
////package com.schall.jyyxbackendserviceclient.service;
////
////import com.fasterxml.jackson.databind.ObjectMapper;
////import com.fasterxml.jackson.databind.node.ObjectNode;
////import com.fasterxml.jackson.databind.node.ArrayNode;
////import com.schall.jyyxbackendserviceclient.service.config.ConfigLoader;
////import org.apache.hc.client5.http.classic.methods.HttpPost;
////import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
////import org.apache.hc.client5.http.impl.classic.HttpClients;
////import org.apache.hc.core5.http.io.entity.StringEntity;
////import org.apache.hc.core5.http.io.entity.EntityUtils;
////import org.apache.hc.core5.http.ContentType;
////
////import java.io.IOException;
////import java.nio.charset.StandardCharsets;
////import java.util.ArrayList;
////import java.util.HashMap;
////import java.util.List;
////import java.util.Map;
////
/////**
//// * DeepSeekChatClient 类用于与 DeepSeek API 进行交互，发送代码分析请求并获取响应。
//// */
////public class DeepSeekChatClient {
////
////    private static final String SYSTEM_MESSAGE = "You are a helpful assistant.";
////
////    private List<Map<String, String>> messages;
////
////    private String apiUrl;
////    private String apiKey;
////
////    /**
////     * 构造函数，初始化消息列表并添加系统消息，同时加载配置文件。
////     */
////    public DeepSeekChatClient() {
////        messages = new ArrayList<>();
////        // 添加系统消息
////        messages.add(new HashMap<String, String>() {{
////            put("role", "system");
////            put("content", SYSTEM_MESSAGE);
////        }});
////
////        // 加载配置文件
////        loadConfig();
////    }
////
////    /**
////     * 加载配置文件。
////     */
////    private void loadConfig() {
////        try {
////            Map<String, Object> config = ConfigLoader.loadConfig();
////            Map<String, Object> deepSeekConfig = (Map<String, Object>) config.get("deepseek");
////            apiUrl = (String) deepSeekConfig.get("api_url");
////            apiKey = (String) deepSeekConfig.get("api_key");
////        } catch (IOException e) {
////            throw new RuntimeException("Failed to load configuration", e);
////        }
////    }
////
////    /**
////     * 发送代码分析请求到 DeepSeek API 并获取响应。
////     *
////     * @param code 需要分析的代码
////     * @return API 的响应内容
////     * @throws IOException 网络请求异常
////     */
////    public String sendCodeAnalysisRequest(String code) throws IOException {
////        // 添加用户消息到对话历史
////        messages.add(new HashMap<String, String>() {{
////            put("role", "user");
////            put("content", "Analyze the following code for correctness and readability:\n" + code);
////        }});
////
////        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
////            HttpPost httpPost = new HttpPost(apiUrl);
////
////            // 设置请求头
////            httpPost.setHeader("Authorization", "Bearer " + apiKey);
////            httpPost.setHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
////
////            // 创建 JSON 请求体
////            ObjectMapper objectMapper = new ObjectMapper();
////            ObjectNode root = objectMapper.createObjectNode();
////            ArrayNode messagesNode = root.putArray("messages");
////
////            // 将对话历史添加到请求体中
////            for (Map<String, String> message : messages) {
////                ObjectNode msg = messagesNode.addObject();
////                msg.put("role", message.get("role"));
////                msg.put("content", message.get("content"));
////            }
////
////            root.put("model", "deepseek-chat");
////
////            String jsonPayload = root.toString();
////            System.out.println("Request payload: " + jsonPayload);
////
////            // 设置实体并指定字符编码为 UTF-8
////            httpPost.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));
////
////            // 执行请求并处理响应
////            String responseString = httpClient.execute(httpPost, response -> {
////                int statusCode = response.getCode();
////                if (statusCode >= 200 && statusCode < 300) {
////                    return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
////                } else {
////                    throw new IOException("Unexpected response status: " + statusCode);
////                }
////            });
////
////            // 解析 JSON 响应
////            ObjectMapper responseMapper = new ObjectMapper();
////            ObjectNode jsonResponse = responseMapper.readValue(responseString, ObjectNode.class);
////            String content = jsonResponse.path("choices").path(0).path("message").path("content").asText();
////
////            // 添加助理的回答到对话历史
////            messages.add(new HashMap<String, String>() {{
////                put("role", "assistant");
////                put("content", content);
////            }});
////
////            return content;
////        }
////    }
////}
////----------------------------------------------这个是智能分析代码客户端完全可以完成业务----------------------------------------------
//
//
//
//
//
//
//
//
//
//
//
//
//
//
////
////
////package com.schall.jyyxbackendserviceclient.service;
////
////import com.fasterxml.jackson.databind.ObjectMapper;
////import com.fasterxml.jackson.databind.node.ObjectNode;
////import com.fasterxml.jackson.databind.node.ArrayNode;
////import com.schall.jyyxbackendserviceclient.service.config.ConfigLoader;
////import org.apache.hc.client5.http.classic.methods.HttpPost;
////import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
////import org.apache.hc.client5.http.impl.classic.HttpClients;
////import org.apache.hc.core5.http.io.entity.StringEntity;
////import org.apache.hc.core5.http.io.entity.EntityUtils;
////import org.apache.hc.core5.http.ContentType;
////
////import java.io.IOException;
////import java.nio.charset.StandardCharsets;
////import java.util.ArrayList;
////import java.util.HashMap;
////import java.util.List;
////import java.util.Map;
////
/////**
//// * DeepSeekChatClient 类用于与 DeepSeek API 进行交互，发送代码分析请求并获取响应。
//// */
////public class DeepSeekChatClient {
////
////    private static final String SYSTEM_MESSAGE = "You are a helpful assistant.";
////
////    private List<Map<String, String>> messages;
////
////    private String apiUrl;
////    private String apiKey;
////
////    /**
////     * 构造函数，初始化消息列表并添加系统消息，同时加载配置文件。
////     */
////    public DeepSeekChatClient() {
////        messages = new ArrayList<>();
////        // 添加系统消息
////        messages.add(new HashMap<String, String>() {{
////            put("role", "system");
////            put("content", SYSTEM_MESSAGE);
////        }});
////
////        // 加载配置文件
////        loadConfig();
////    }
////
////    /**
////     * 加载配置文件。
////     */
////    private void loadConfig() {
////        try {
////            Map<String, Object> config = ConfigLoader.loadConfig();
////            Map<String, Object> deepSeekConfig = (Map<String, Object>) config.get("deepseek");
////            apiUrl = (String) deepSeekConfig.get("api_url");
////            apiKey = (String) deepSeekConfig.get("api_key");
////        } catch (IOException e) {
////            throw new RuntimeException("Failed to load configuration", e);
////        }
////    }
////
////    /**
////     * 发送代码分析请求到 DeepSeek API 并获取响应。
////     *
////     * @param code 需要分析的代码
////     * @return API 的响应内容
////     * @throws IOException 网络请求异常
////     */
////    public String sendCodeAnalysisRequest(String code) throws IOException {
////        // 添加用户消息到对话历史
////        messages.add(new HashMap<String, String>() {{
////            put("role", "user");
////            put("content", "Analyze the following code for correctness and readability:\n" + code + "\nPlease provide the response in Chinese.");
////        }});
////
////        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
////            HttpPost httpPost = new HttpPost(apiUrl);
////
////            // 设置请求头
////            httpPost.setHeader("Authorization", "Bearer " + apiKey);
////            httpPost.setHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
////
////            // 创建 JSON 请求体
////            ObjectMapper objectMapper = new ObjectMapper();
////            ObjectNode root = objectMapper.createObjectNode();
////            ArrayNode messagesNode = root.putArray("messages");
////
////            // 将对话历史添加到请求体中
////            for (Map<String, String> message : messages) {
////                ObjectNode msg = messagesNode.addObject();
////                msg.put("role", message.get("role"));
////                msg.put("content", message.get("content"));
////            }
////
////            root.put("model", "deepseek-chat");
////
////            String jsonPayload = root.toString();
////            System.out.println("Request payload: " + jsonPayload);
////
////            // 设置实体并指定字符编码为 UTF-8
////            httpPost.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));
////
////            // 执行请求并处理响应
////            String responseString = httpClient.execute(httpPost, response -> {
////                int statusCode = response.getCode();
////                if (statusCode >= 200 && statusCode < 300) {
////                    return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
////                } else {
////                    throw new IOException("Unexpected response status: " + statusCode);
////                }
////            });
////
////            // 解析 JSON 响应
////            ObjectMapper responseMapper = new ObjectMapper();
////            ObjectNode jsonResponse = responseMapper.readValue(responseString, ObjectNode.class);
////            String content = jsonResponse.path("choices").path(0).path("message").path("content").asText();
////
////            // 添加助理的回答到对话历史
////            messages.add(new HashMap<String, String>() {{
////                put("role", "assistant");
////                put("content", content);
////            }});
////
////            return content;
////        }
////    }
////}//完整代码中文回复，缺点不能简短回复代码
//
//package com.schall.jyyxbackendserviceclient.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.schall.jyyxbackendserviceclient.service.config.ConfigLoader;
//import org.apache.hc.client5.http.classic.methods.HttpPost;
//import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
//import org.apache.hc.client5.http.impl.classic.HttpClients;
//import org.apache.hc.core5.http.io.entity.StringEntity;
//import org.apache.hc.core5.http.io.entity.EntityUtils;
//import org.apache.hc.core5.http.ContentType;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * DeepSeekChatClient 类用于与 DeepSeek API 进行交互，发送代码分析请求并获取响应。
// */
//public class DeepSeekChatClient {
//
//    private static final String SYSTEM_MESSAGE = "You are a helpful assistant.";
//
//    private List<Map<String, String>> messages;
//
//    private String apiUrl;
//    private String apiKey;
//
//    /**
//     * 构造函数，初始化消息列表并添加系统消息，同时加载配置文件。
//     */
//    public DeepSeekChatClient() {
//        messages = new ArrayList<>();
//        // 添加系统消息
//        messages.add(new HashMap<String, String>() {{
//            put("role", "system");
//            put("content", SYSTEM_MESSAGE);
//        }});
//
//        // 加载配置文件
//        loadConfig();
//    }
//
//    /**
//     * 加载配置文件。
//     */
//    private void loadConfig() {
//        try {
//            Map<String, Object> config = ConfigLoader.loadConfig();
//            Map<String, Object> deepSeekConfig = (Map<String, Object>) config.get("deepseek");
//            apiUrl = (String) deepSeekConfig.get("api_url");
//            apiKey = (String) deepSeekConfig.get("api_key");
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to load configuration", e);
//        }
//    }
//
//    /**
//     * 发送代码分析请求到 DeepSeek API 并获取响应。
//     *
//     * @param code 需要分析的代码
//     * @return API 的响应内容
//     * @throws IOException 网络请求异常
//     */
//    public String sendCodeAnalysisRequest(String code) throws IOException {
//        // 添加用户消息到对话历史
//        messages.add(new HashMap<String, String>() {{
//            put("role", "user");
//            put("content", "Analyze the following code for correctness and readability:\n" + code + "\nPlease provide a concise response in Chinese.");
//        }});
//
//        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//            HttpPost httpPost = new HttpPost(apiUrl);
//
//            // 设置请求头
//            httpPost.setHeader("Authorization", "Bearer " + apiKey);
//            httpPost.setHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
//
//            // 创建 JSON 请求体
//            ObjectMapper objectMapper = new ObjectMapper();
//            ObjectNode root = objectMapper.createObjectNode();
//            ArrayNode messagesNode = root.putArray("messages");
//
//            // 将对话历史添加到请求体中
//            for (Map<String, String> message : messages) {
//                ObjectNode msg = messagesNode.addObject();
//                msg.put("role", message.get("role"));
//                msg.put("content", message.get("content"));
//            }
//
//            //切换大模型
//            //deepseek-chat deepseek-coder
//            root.put("model", "deepseek-coder");
//
//            String jsonPayload = root.toString();
//            System.out.println("Request payload: " + jsonPayload);
//
//            // 设置实体并指定字符编码为 UTF-8
//            httpPost.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));
//
//            // 执行请求并处理响应
//            String responseString = httpClient.execute(httpPost, response -> {
//                int statusCode = response.getCode();
//                if (statusCode >= 200 && statusCode < 300) {
//                    return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
//                } else {
//                    throw new IOException("Unexpected response status: " + statusCode);
//                }
//            });
//
//            // 解析 JSON 响应
//            ObjectMapper responseMapper = new ObjectMapper();
//            ObjectNode jsonResponse = responseMapper.readValue(responseString, ObjectNode.class);
//            String content = jsonResponse.path("choices").path(0).path("message").path("content").asText();
//
//            // 添加助理的回答到对话历史
//            messages.add(new HashMap<String, String>() {{
//                put("role", "assistant");
//                put("content", content);
//            }});
//
//            return content;
//        }
//    }
//}
package com.schall.jyyxbackendserviceclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.schall.jyyxbackendserviceclient.service.config.ConfigLoader;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.ContentType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DeepSeekChatClient 类用于与 DeepSeek API 进行交互，发送代码分析请求并获取响应。
 */
public class DeepSeekChatClient {

    private static final String SYSTEM_MESSAGE = "You are a helpful assistant.";

    private List<Map<String, String>> messages;

    private String apiUrl;
    private String apiKey;

    /**
     * 构造函数，初始化消息列表并添加系统消息，同时加载配置文件。
     */
    public DeepSeekChatClient() {
        messages = new ArrayList<>();
        // 添加系统消息
        messages.add(new HashMap<String, String>() {{
            put("role", "system");
            put("content", SYSTEM_MESSAGE);
        }});

        // 加载配置文件
        loadConfig();
    }

    /**
     * 加载配置文件。
     */
    private void loadConfig() {
        try {
            Map<String, Object> config = ConfigLoader.loadConfig();
            Map<String, Object> deepSeekConfig = (Map<String, Object>) config.get("deepseek");
            apiUrl = (String) deepSeekConfig.get("api_url");
            apiKey = (String) deepSeekConfig.get("api_key");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    /**
     * 发送代码分析请求到 DeepSeek API 并获取响应。
     *
     * @param code 需要分析的代码
     * @return API 的响应内容
     * @throws IOException 网络请求异常
     */
    public String sendCodeAnalysisRequest(String code) throws IOException {
        // 添加用户消息到对话历史
        messages.add(new HashMap<String, String>() {{
            put("role", "user");
            put("content", "Analyze the following code for correctness and readability:\n" + code + "\nPlease provide a concise response in Chinese.");
        }});

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);

            // 设置请求头
            httpPost.setHeader("Authorization", "Bearer " + apiKey);
            httpPost.setHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());

            // 创建 JSON 请求体
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode root = objectMapper.createObjectNode();
            ArrayNode messagesNode = root.putArray("messages");

            // 将对话历史添加到请求体中
            for (Map<String, String> message : messages) {
                ObjectNode msg = messagesNode.addObject();
                msg.put("role", message.get("role"));
                msg.put("content", message.get("content"));
            }

            // 切换大模型
            root.put("model", "deepseek-coder");

            String jsonPayload = root.toString();
            System.out.println("Request payload: " + jsonPayload);

            // 设置实体并指定字符编码为 UTF-8
            httpPost.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));

            // 执行请求并处理响应
            String responseString = httpClient.execute(httpPost, response -> {
                int statusCode = response.getCode();
                if (statusCode >= 200 && statusCode < 300) {
                    return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                } else {
                    throw new IOException("Unexpected response status: " + statusCode);
                }
            });

            // 解析 JSON 响应
            ObjectMapper responseMapper = new ObjectMapper();
            ObjectNode jsonResponse = responseMapper.readValue(responseString, ObjectNode.class);
            String content = jsonResponse.path("choices").path(0).path("message").path("content").asText();

            // 添加助理的回答到对话历史
            messages.add(new HashMap<String, String>() {{
                put("role", "assistant");
                put("content", content);
            }});

            return content;
        }
    }

    /**
     * 提供个性化的学习反馈
     *
     * @param code 学生提交的代码
     * @return 个性化的学习反馈
     * @throws IOException 网络请求异常
     */
    public String providePersonalizedFeedback(String code) throws IOException {
        // 添加用户消息到对话历史
        messages.add(new HashMap<String, String>() {{
            put("role", "user");
            put("content", "Provide personalized feedback for the following code to help the student improve:\n" + code + "\nPlease provide the response in Chinese.");
        }});

        return sendChatRequest();
    }

    /**
     * 提供教学指导建议
     *
     * @param code 学生提交的代码
     * @return 教学指导建议
     * @throws IOException 网络请求异常
     */
    public String provideTeachingGuidance(String code) throws IOException {
        // 添加用户消息到对话历史
        messages.add(new HashMap<String, String>() {{
            put("role", "user");
            put("content", "Provide teaching guidance based on the following code to help the instructor improve their teaching:\n" + code + "\nPlease provide the response in Chinese.");
        }});

        return sendChatRequest();
    }

    /**
     * 发送聊天请求到 DeepSeek API 并获取响应。
     *
     * @return API 的响应内容
     * @throws IOException 网络请求异常
     */
    private String sendChatRequest() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);

            // 设置请求头
            httpPost.setHeader("Authorization", "Bearer " + apiKey);
            httpPost.setHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());

            // 创建 JSON 请求体
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode root = objectMapper.createObjectNode();
            ArrayNode messagesNode = root.putArray("messages");

            // 将对话历史添加到请求体中
            for (Map<String, String> message : messages) {
                ObjectNode msg = messagesNode.addObject();
                msg.put("role", message.get("role"));
                msg.put("content", message.get("content"));
            }

            // 切换大模型
            root.put("model", "deepseek-coder");

            String jsonPayload = root.toString();
            System.out.println("Request payload: " + jsonPayload);

            // 设置实体并指定字符编码为 UTF-8
            httpPost.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));

            // 执行请求并处理响应
            String responseString = httpClient.execute(httpPost, response -> {
                int statusCode = response.getCode();
                if (statusCode >= 200 && statusCode < 300) {
                    return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                } else {
                    throw new IOException("Unexpected response status: " + statusCode);
                }
            });

            // 解析 JSON 响应
            ObjectMapper responseMapper = new ObjectMapper();
            ObjectNode jsonResponse = responseMapper.readValue(responseString, ObjectNode.class);
            String content = jsonResponse.path("choices").path(0).path("message").path("content").asText();

            // 添加助理的回答到对话历史
            messages.add(new HashMap<String, String>() {{
                put("role", "assistant");
                put("content", content);
            }});

            return content;
        }
    }
}