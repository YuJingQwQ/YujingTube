package icu.yujing.product.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: Cyqurt
 * @date: 2022/3/26
 * @version: 1.0
 * @description:
 */
@Configuration
public class ElasticsearchConfig {
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        RestClientBuilder builder = RestClient.builder(new HttpHost("192.168.89.128", 9200, "http"));
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "18718857472"));
        builder.setHttpClientConfigCallback(f -> f.setDefaultCredentialsProvider(credentialsProvider));
        return new RestHighLevelClient(builder);
    }
}
