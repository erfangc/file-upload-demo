package com.example.demo

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.web.multipart.MultipartFile

import software.amazon.awssdk.core.ResponseInputStream
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectResponse
import software.amazon.awssdk.services.s3.model.PutObjectRequest

internal class S3ProxyTest {

    @Test
    fun read() {
        val s3Client = mockk<S3Client>()

        val fakeBytes = ByteArray(10)
        val responseInputStream = mockk<ResponseInputStream<GetObjectResponse>>()
        every { responseInputStream.readAllBytes() } returns fakeBytes
        every { s3Client.getObject(any<GetObjectRequest>()) } returns responseInputStream

        val s3 = S3Proxy(s3Client)
        val result = s3.read("123")

        assertEquals(10, result.size)

        verify {
            s3Client.getObject(match<GetObjectRequest> { request ->
                request.bucket() == "erfangchen.com" && request.key() == "uploads/documents/123"
            })
        }

    }

    @Test
    fun write() {
        val s3Client = mockk<S3Client>()

        every {
            s3Client.putObject(any<PutObjectRequest>(), any<RequestBody>())
        } returns mockk()

        val s3 = S3Proxy(s3Client)

        val file = mockk<MultipartFile>()
        every { file.bytes } returns ByteArray(10)
        s3.write("123", file)

        verify {
            s3Client.putObject(
                match<PutObjectRequest> { it.bucket() == "erfangchen.com" && it.key() == "uploads/documents/123" },
                any<RequestBody>()
            )
        }
    }
}