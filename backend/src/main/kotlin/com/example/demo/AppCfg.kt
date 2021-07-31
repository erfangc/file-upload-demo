package com.example.demo

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class AppCfg {
    @Bean
    fun s3(): S3Client {
        return S3Client.builder().build()
    }
}