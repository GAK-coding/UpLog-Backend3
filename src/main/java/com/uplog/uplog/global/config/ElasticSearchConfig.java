//package com.uplog.uplog.global.config;
//
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.client.RestClients;
//import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
//import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
//
//@Configuration
//public class ElasticSearchConfig {
//    private static final String host = "june-deployment.es.ap-northeast-2.aws.elastic-cloud.com";
//
//    private static final String username = "tun111@gachon.ac.kr ";
//    private static final String password = "";
//
//    @Bean
//    public RestHighLevelClient client() {
//        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
//                .connectedTo(host)
//                .usingSsl()
//                .withBasicAuth(username, password)
//                .build();
//        return RestClients.create(clientConfiguration)
//                .rest();
//    }
//
//    @Bean
//    public ElasticsearchOperations elasticsearchTemplate() {
//        ElasticsearchRestTemplate elasticsearchRestTemplate = new ElasticsearchRestTemplate(client());
//        return elasticsearchRestTemplate;
//    }
//}
