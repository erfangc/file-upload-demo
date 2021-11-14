package com.example.demo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File

@RestController
@CrossOrigin
class FileUploadController(
    private val s3: S3Proxy,
    private val objectMapper: ObjectMapper,
) {

    private val objectWriter = objectMapper.writerWithDefaultPrettyPrinter()

    @GetMapping("download/{id}")
    fun download(@PathVariable id: String): ResponseEntity<ByteArray> {
        val fileMetadata = objectMapper.readValue<FileUploadResponse>(File("$id.json"))
        val bytes = s3.read(id)
        return ResponseEntity
            .ok()
            .contentType(MediaType.valueOf(fileMetadata.contentType!!))
            .header(HttpHeaders.CONTENT_DISPOSITION, """attachment; filename="${fileMetadata.originalFilename}"""")
            .body(bytes)
    }

    @PostMapping("upload")
    fun uploadSingleFile(@RequestParam file: MultipartFile): FileUploadResponse {
        return if (file.isEmpty) {
            error("what are you doing?")
        } else {
            val fileMetadata = FileUploadResponse(
                size = file.size,
                originalFilename = file.originalFilename ?: "unknown",
                contentType = file.contentType,
            )
            s3.write(fileMetadata.id, file)
            File("${fileMetadata.id}.json").writeBytes(objectWriter.writeValueAsBytes(fileMetadata))
            fileMetadata
        }
    }

}