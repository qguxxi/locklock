package com.locklock.applock.core.crypto

import java.security.MessageDigest

object PasscodeHasher {
    private const val SALT = "SereneVault_LockLock_Salt_2026"

    fun sha256(input: String): String {
        val salted = "$SALT:$input"
        val digest = MessageDigest.getInstance("SHA-256").digest(salted.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }
}
