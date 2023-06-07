package web.api.config;

import static io.lettuce.core.ReadFrom.REPLICA_PREFERRED;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import web.api.config.properties.CacheProperties;

@EnableTransactionManagement
@Slf4j
@RequiredArgsConstructor
@Configuration
public class RedisConfig {
    @Value("spring.redis.cluster.mode")
    private Boolean clusterMode;

    private final CacheProperties cacheProperties;

    @Bean(name = "redisCacheConnectionFactory")
    public LettuceConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {

        if (clusterMode){
            // 클러스터 호스트 세팅
            RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration(redisProperties.getCluster().getNodes());
            clusterConfig.setMaxRedirects(redisProperties.getCluster().getMaxRedirects());

            // topology 자동 업데이트 옵션 추가
            // enablePeriodicRefresh(tolpology 정보 감시 텀) default vaule : 60s
            ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                    .enableAllAdaptiveRefreshTriggers()  // MOVED, ASK, PERSISTENT_RECONNECTS, UNCOVERED_SLOT, UNKOWN_NODE trigger시 refresh 진행
                    .build();

            ClientOptions clientOptions = ClusterClientOptions.builder()
                    .topologyRefreshOptions(clusterTopologyRefreshOptions)
                    .build();

            // topology 옵션 및 timeout 세팅
            LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                    .clientOptions(clientOptions)
                    .commandTimeout(Duration.ofSeconds(2000L)) // timeout Duration 값
                    .readFrom(REPLICA_PREFERRED) // slave 에서 읽도록 설정
                    .build();
            return new LettuceConnectionFactory(clusterConfig,clientConfig);
        } else {
            // REDIS StandAlone 설정
            RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration(redisProperties.getHost(),redisProperties.getPort());
            return new LettuceConnectionFactory(standaloneConfig);
        }

    }

    private ObjectMapper objectMapper() {
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator
                .builder()
                .allowIfSubType(Object.class)
                .build();
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
        mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
        return mapper;
    }

    private RedisCacheConfiguration redisCacheDefaultConfiguration() {
        RedisCacheConfiguration redisCacheConfiguration =
                RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper())));
        return redisCacheConfiguration;
    }

    /*
     * properties에서 가져온 캐시명과 ttl 값으로 RedisCacheConfiguration을 만들고 Map에 넣어 반환한다.
     */
    private Map<String, RedisCacheConfiguration> redisCacheConfigurationMap() {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        for (Entry<String, Long> cacheNameAndTimeout : cacheProperties.getTtl().entrySet()) {
            cacheConfigurations
                    .put(cacheNameAndTimeout.getKey(),
                            redisCacheDefaultConfiguration().entryTtl(Duration.ofSeconds(cacheNameAndTimeout.getValue())));
        }
        return cacheConfigurations;
    }

    @Bean
    public CacheManager redisCacheManager(@Qualifier("redisCacheConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        RedisCacheManager redisCacheManager = RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheDefaultConfiguration())
                .withInitialCacheConfigurations(redisCacheConfigurationMap()).build();
        return redisCacheManager;
    }
}
