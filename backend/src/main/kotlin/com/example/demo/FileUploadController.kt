package com.example.demo

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@RestController
@CrossOrigin
class FileUploadController(private val s3: S3Client) {
    private val log = LoggerFactory.getLogger(FileUploadController::class.java)

    @PostMapping("upload")
    fun uploadSingleFile(@RequestParam file: MultipartFile): FileUploadResponse {
        val prefix = "uploads/documents"

        return if (file.isEmpty) {
            error("what are you doing?")
        } else {

            val bytes = file.bytes
            val originalFilename = file.originalFilename ?: error("originalFilename not found")
            val bucketName = "erfangchen.com"
            val objectKey = "$prefix/$originalFilename"

            s3.putObject(
                PutObjectRequest.builder().bucket(bucketName).key(objectKey).build(),
                RequestBody.fromBytes(bytes),
            )

            log.info(
                "Uploaded file " +
                    "contentType=${file.contentType}, " +
                    "originalFilename=${file.originalFilename}, " +
                    "size=${file.size} name=${file.name} "
            )

            FileUploadResponse(
                size = file.size,
                originalFilename = file.originalFilename ?: "unknown",
                link = "s3://$bucketName/$objectKey",
            )
        }

    }

}