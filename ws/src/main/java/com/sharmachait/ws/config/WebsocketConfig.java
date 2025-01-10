package com.sharmachait.ws.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharmachait.ws.config.jwt.JwtConstants;
import com.sharmachait.ws.config.jwt.JwtProvider;
import com.sharmachait.ws.models.messages.MessageType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import javax.crypto.SecretKey;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic", "/user");
        registry.setUserDestinationPrefix("/user");// convertSendToUser will send to /user/{username}/queue/{destination}
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // Allow all origins
                .setAllowedOrigins("*")         // Additional CORS configuration
                .withSockJS()
                .setClientLibraryUrl("https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js")
                .setWebSocketEnabled(true)
                .setSessionCookieNeeded(false);
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);

        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(new ObjectMapper());
        converter.setContentTypeResolver(resolver);

        messageConverters.add(converter);

        return false;
    }
    @Primary
    @Bean(name="chat")
    public DataSource dbContextChat(){
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl("jdbc:postgresql://ep-soft-recipe-a189x52v.ap-southeast-1.aws.neon.tech/chat?sslmode=require");
        ds.setUsername("learning_postgres_owner");
        ds.setPassword("t8qELP1OjCFp");
        return ds;
    }

    @Primary
    @Bean(name = "chatEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean chatEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("chat") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.sharmachait.ws.models.entity")
                .persistenceUnit("chat")
                .properties(Collections.singletonMap("hibernate.hbm2ddl.auto", "create-drop"))
                .build();
    }

    @Primary
    @Bean(name = "chatTransactionManager")
    public PlatformTransactionManager chatTransactionManager(
            @Qualifier("chatEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name="metaverse")
    public DataSource dbContext(){
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl("jdbc:postgresql://ep-soft-recipe-a189x52v.ap-southeast-1.aws.neon.tech/metaverse?sslmode=require");
        ds.setUsername("learning_postgres_owner");
        ds.setPassword("t8qELP1OjCFp");
        return ds;
    }
    @Bean(name = "metaverseJdbcTemplate")
    public JdbcTemplate metaverseJdbcTemplate(@Qualifier("metaverse") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                try{
                    if (accessor == null) {
                        throw new MessagingException("Invalid message headers");
                    }
                    // Skip CONNECT frame processing
                    if (StompCommand.CONNECT.equals(accessor.getCommand())
                            || StompCommand.DISCONNECT.equals(accessor.getCommand())
                    ) {
                        return message;
                    }
                    if(StompCommand.SUBSCRIBE.equals(accessor.getCommand())){
                        String destination = accessor.getDestination();
                        assert destination != null;
                        String[] pathParts = destination.split("/");
                        if(pathParts[1].equals("user")){
                            String targetUsername = pathParts[2];
                            String bearerToken = accessor.getNativeHeader("Authorization")
                                    .stream()
                                    .findFirst()
                                    .orElse(null);

                            String username = JwtProvider.getEmailFromToken(bearerToken);
                            if(!targetUsername.equals(username)){
                                throw new MessageDeliveryException("Not authorized to subscribe to this queue");
                            }
                        }
                        return message;
                    }
                    Object payloadObj = message.getPayload();
                    String token = getTokenFromMessage(payloadObj);
                    String destination = accessor.getDestination();

                    if(!validateToken(token)){
                        throw new RuntimeException("Invalid token");
                    }
                } catch (Exception e) {
                    throw new MessagingException(e.getMessage());
                }
                return message;
            }
        });
    }

    public MessageType getMessageTypeFromMessage(Object payload) throws JsonProcessingException {
        if (!(payload instanceof byte[])) {
            throw new MessagingException("Invalid payload type, expected byte[]");
        }
        String jsonPayload = new String((byte[]) payload, StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonPayload);
        String token = jsonNode.get("type").asText();
        if(token != null && token.equals(MessageType.CHAT.toString()) ){
            return MessageType.CHAT;
        }
        throw new RuntimeException("Invalid token, can not be empty");
    }

    public String getTokenFromMessage(Object payload) throws JsonProcessingException {
        if (!(payload instanceof byte[])) {
            throw new MessagingException("Invalid payload type, expected byte[]");
        }
        String jsonPayload = new String((byte[]) payload, StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonPayload);
        String token = jsonNode.get("payload").get("token").asText();
        if(token == null || token.equals("null") ){
            throw new RuntimeException("Invalid token, can not be empty");
        }
        return token;
    }

    private boolean validateToken(String jwt) {
        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
            try{
                SecretKey key = Keys.hmacShaKeyFor(JwtConstants.JWT_SECRET.getBytes());
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();
                return true;
            } catch (JwtException | IllegalArgumentException e) {
                return false;
            }
        }
        return false;
    }
}
