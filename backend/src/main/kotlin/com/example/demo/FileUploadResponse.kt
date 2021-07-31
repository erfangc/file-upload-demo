package com.example.demo

import java.time.Instant
import java.util.*

data class FileUploadResponse(
    val id: String = UUID.randomUUID().toString(),
    val createdOn: Instant = Instant.now(),
    val updatedOn: Instant = Instant.now(),
    val size: Long,
    val originalFilename: String,
    val link: String,
)