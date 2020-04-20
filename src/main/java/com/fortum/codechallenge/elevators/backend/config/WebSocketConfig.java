//package com.fortum.codechallenge.elevators.backend.config;
//
//import com.fortum.codechallenge.elevators.backend.api.ElevatorController;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.web.reactive.socket.WebSocketMessage;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.util.List;
//import java.util.concurrent.CopyOnWriteArrayList;
//
//@Configuration
//@EnableWebSocket
//public class WebSocketConfig implements WebSocketConfigurer {
//
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
//        webSocketHandlerRegistry.addHandler(new ElevatorHandler(),"/elevators").setAllowedOrigins("*");
//    }
//
//    class ElevatorHandler extends TextWebSocketHandler {
//
//        @Autowired
//        private ElevatorController elevatorController;
//
//        private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
//
//        @Override
//        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//           sessions.add(session);
//           while(session.isOpen()){
//               session.sendMessage(new TextMessage("babaab"));
//               System.out.println(elevatorController.getElevatorsPositions());
//               session.sendMessage(new TextMessage(elevatorController.getElevatorsPositions().toString()));
//
//           }
////           for(int i =0; i < 10000; i ++){
////               session.sendMessage(new TextMessage("bla bla"));
////            }
//
//            super.afterConnectionEstablished(session);
//        }
//
//        @Override
//        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//           for(WebSocketSession s: sessions){
//                session.sendMessage(message);
//            }
//
//            super.handleTextMessage(session, message);
//        }
//    }
//}
