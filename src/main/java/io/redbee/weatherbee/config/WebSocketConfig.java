package io.redbee.weatherbee.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
 
//	@Override
//	public void registerStompEndpoints(StompEndpointRegistry registry) {
//		registry.addEndpoint("/socket").setAllowedOrigins("*").withSockJS();
//	}
//
//	@Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.setApplicationDestinationPrefixes("/weatherbee").enableSimpleBroker("/weather-updates");
//    }
// 
//	@Override
//	protected void configureInbound(
//	  MessageSecurityMetadataSourceRegistry messages) { 
//	    messages.simpTypeMatchers(
//	            SimpMessageType.CONNECT,
//	            SimpMessageType.MESSAGE,
//	            SimpMessageType.SUBSCRIBE).authenticated()
//	      .simpDestMatchers("/socket/**").authenticated()
//	      .anyMessage().authenticated(); 
//	}
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/socket");
		config.setApplicationDestinationPrefixes("/weatherbee");
	}
 
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry
		.addEndpoint("/weather-updates")
		.setAllowedOrigins("http://localhost:4200")
		.withSockJS();
	}
}
