package com.payzone.transaction.client

import android.util.Base64
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.zip.GZIPInputStream

internal object CompressionUtils {

    private val TAG = CompressionUtils::class.java.simpleName
    private const val GZIP_HEADER_SKIP_BYTES = 4

    fun decompressData(zipText: String?): String {
        if (zipText == null) return ""
        return decompressBytes(Base64.decode(zipText, Base64.DEFAULT))
    }

    fun decompressBytes(compressed: ByteArray?): String {
        if (compressed == null || compressed.size <= GZIP_HEADER_SKIP_BYTES) return ""
        return try {
            GZIPInputStream(
                ByteArrayInputStream(compressed, GZIP_HEADER_SKIP_BYTES, compressed.size - GZIP_HEADER_SKIP_BYTES)
            ).use { gzip ->
                ByteArrayOutputStream().use { baos ->
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (gzip.read(buffer).also { bytesRead = it } != -1) {
                        baos.write(buffer, 0, bytesRead)
                    }
                    baos.toString(StandardCharsets.UTF_8.name())
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to decompress data", e)
            ""
        }
    }
}
