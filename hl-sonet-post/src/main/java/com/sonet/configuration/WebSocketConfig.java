package com.sonet.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonet.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;
import java.util.UUID;

@Configuration
@Slf4j
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig
        implements WebSocketMessageBrokerConfigurer {

    public static final String DESTINATION_PREFIX = "/user";
    public static final String DESTINATION_POSTFIX = "/queue/messages";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(DESTINATION_PREFIX);
        registry.setUserDestinationPrefix(DESTINATION_PREFIX);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String jwtToken = accessor.getFirstNativeHeader(jwtTokenProvider.getAuthorizationHeader());
                    // just check that JWT toked is valid
                    jwtTokenProvider.extractUsernameAndUuid(jwtToken);
                }
                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String jwtToken = accessor.getFirstNativeHeader(jwtTokenProvider.getAuthorizationHeader());
                    // check that JWT token is valid
                    Pair<String, UUID> userNameAndUuid = jwtTokenProvider.extractUsernameAndUuid(jwtToken);
                    // check that feed UUID and user UUID are the same
                    checkFeedOwnership(accessor, userNameAndUuid);
                }
                return message;
            }
        });
    }

    private static void checkFeedOwnership(StompHeaderAccessor accessor, Pair<String, UUID> userNameAndUuid) {
        if (!(DESTINATION_PREFIX + "/" + userNameAndUuid.getRight() + DESTINATION_POSTFIX)
                .equals(accessor.getDestination())) {
            throw new SecurityException("Forbidden to see foreign feed");
        }
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);
        return false;
    }
}
