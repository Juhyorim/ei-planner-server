package com.kihyaa.Eiplanner.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.cloud.aws")
public class S3Properties {
    private String accessKey;
    private String secretKey;
    private String region;
    private String endpoint;
    private String bucket;
}
