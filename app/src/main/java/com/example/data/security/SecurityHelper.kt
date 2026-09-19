package com.example.data.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object SecurityHelper {
    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    fun hashPassword(password: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(Base64.getDecoder().decode(salt))
        val hashedBytes = md.digest(password.toByteArray(Charsets.UTF_8))
        val sb = StringBuilder()
        for (b in hashedBytes) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }

    fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
        val computedHash = hashPassword(password, salt)
        return MessageDigest.isEqual(computedHash.toByteArray(), expectedHash.toByteArray())
    }
}
