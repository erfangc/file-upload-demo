package com.example.demo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File

@RestController
@CrossOrigin
class FileUploadController(
    private val s3: S3Client,
    private val objectMapper: ObjectMapper,
) {

    private val log = LoggerFactory.getLogger(FileUploadController::class.java)
    private val objectWriter = objectMapper.writerWithDefaultPrettyPrinter()
    private val bucket = "erfangchen.com"

    @GetMapping("download/{filename}")
    fun download(@PathVariable filename: String): ResponseEntity<ByteArray> {
        val fileMetadata = objectMapper.readValue<FileUploadResponse>(File("$filename.json"))
        val bytes =
            s3.getObject(GetObjectRequest.builder().bucket(bucket).key(fileMetadata.objectKey).build())
                .readAllBytes()
        return ResponseEntity
            .ok()
            .contentType(MediaType.valueOf(fileMetadata.contentType!!))
            .body(bytes)
    }

    @PostMapping("upload")
    fun uploadSingleFile(@RequestParam file: MultipartFile): FileUploadResponse {
        val prefix = "uploads/documents"

        return if (file.isEmpty) {
            error("what are you doing?")
        } else {
            val bytes = file.bytes
            val originalFilename = file.originalFilename ?: error("originalFilename not found")
            val bucketName = bucket
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

            val fileMetadata = FileUploadResponse(
                size = file.size,
                originalFilename = file.originalFilename ?: "unknown",
                objectKey = objectKey,
                contentType = file.contentType,
                link = "s3://$bucketName/$objectKey",
            )
            File("$originalFilename.json").writeBytes(objectWriter.writeValueAsBytes(fileMetadata))
            fileMetadata
        }
    }

}