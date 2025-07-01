//package com.example.easyplan.controller;
//
//import ch.qos.logback.core.net.server.Client;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.RestController;
//import com.google.genai.types.GenerateContentResponse;
//
//@RestController
//@RequiredArgsConstructor
//public class GeminiServiceController {
//    Client client = new Client();
//
//    GenerateContentResponse response =
//            client.models.generateContent(
//                    "gemini-2.5-flash",
//                    "Explain how AI works in a few words",
//                    null);
//
//    System.out.println(response.text());
//}
