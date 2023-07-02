package com.goldenratio.chuglikaro.Configuration;

import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

public class WebSocketConfigMessaging implements WebSocketMessageBrokerConfigurer {
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		
	}
	
}
