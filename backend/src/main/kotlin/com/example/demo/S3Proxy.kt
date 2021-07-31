package com.example.demo

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File

/**
 * S3 proxy will save the file locally to the current directory
 * if S3 calls fail
 *
 * Similarly, will read from the file locally if S3 calls fail
 */
@Service
class S3Proxy(
    private val s3: S3Client
) {

    private val bucket = "erfangchen.com"
    private val documentsPrefix = "uploads/documents"
    private val localDir = File("uploads")
    private val log = LoggerFactory.getLogger(S3Proxy::class.java)

    init {
        // ensure directory exists locally
        localDir.mkdir()
    }

    fun read(id: String): ByteArray {
        return try {
            val getObjectRequest = GetObjectRequest.builder().bucket(bucket).key("$documentsPrefix/$id").build()
            s3
                .getObject(getObjectRequest)
                .readAllBytes()
        } catch (ex: Exception) {
            log.error("Unable to read object $id from s3://$bucket, attempting local filesystem instead, ex.message=${ex.message}")
            File(localDir, id).readBytes()
        }
    }

    fun write(id: String, file: MultipartFile) {
        val bytes = file.bytes
        try {
            s3.putObject(
                PutObjectRequest.builder().bucket(bucket).key("$documentsPrefix/$id").build(),
                RequestBody.fromBytes(bytes),
            )
        } catch (ex: Exception) {
            log.error("Unable to read object $id from s3://$bucket, attempting local filesystem instead, ex.message=${ex.message}")
            File(localDir, id).writeBytes(bytes)
        }
    }
}