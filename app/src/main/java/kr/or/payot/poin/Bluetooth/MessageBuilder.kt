package kr.or.payot.poin.Bluetooth

/**
 * Created by yongheekim on 2018. 2. 13..
 */
interface MessageBuilder {

    // Poin device message formatter
    fun generateCommend(commend: String): ByteArray

    // device encryption message
    fun encryption(byteArray: ByteArray): ByteArray

    // device decryption message
    fun decryption(byteArray: ByteArray): Array<String>

    fun checksum(byteArray: ByteArray): ByteArray
}