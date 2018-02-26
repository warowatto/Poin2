package kr.or.payot.poin.DI.Modules

import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import dagger.Module
import dagger.Provides
import io.reactivex.Emitter
import io.reactivex.Observable
import io.reactivex.functions.BiConsumer
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kr.or.payot.poin.Bluetooth.*
import java.io.PipedReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Named
import kotlin.collections.ArrayList

/**
 * Created by yongheekim on 2018. 2. 13..
 */

@Module(includes = arrayOf(ApplicationModule::class))
class BluetoothModule(val context: Context) {

    @Provides
    fun bluetoothAdapter(): BluetoothAdapter =
            BluetoothAdapter.getDefaultAdapter()

    /**
     * Bluetooth Controller
     * connect, send, disconnect
     */
    @Provides
    fun bluetoothController(): BluetoothController = object : BluetoothGattCallback(), BluetoothController {
        private var gatt: BluetoothGatt? = null
        private var gattChar: BluetoothGattCharacteristic? = null

        override fun connect(bluetoothDevice: BluetoothDevice) {
            bluetoothDevice.connectGatt(context, false, this)
        }

        @Throws(IllegalStateException::class)
        override fun sendMessage(byteArray: ByteArray) {
            if (gatt == null) throw IllegalArgumentException("NOT FOUND BLUETOOTHGATT")

            // 전송되는 메시지의 길이는 64byte입니다.
            // BLE는 1회 전송량이 20byte로 제한되어 있습니다.
            val slice20Bytes = Observable.fromArray(byteArray.toTypedArray())
                    .buffer(20)
                    .flatMap { Observable.fromIterable(it).map { it.toByteArray() } }

            // Poin 장치의 블루투스 모듈은 메시지를 수신시 반복적으로 요청할 경우
            // 메시지간 50 m/s 정도의 딜레이를 주어야 합니다
            // 메시지의 길이는 64byte 이므로 총 4회에 걸쳐 보내야 하며
            // [20] - 5 m/s - [20] - 5 m/s - [20] - 5 m/s - [4]
            // 으로 전송되어야 합니다
            val messageDelay = Observable.interval(0, 50, TimeUnit.MILLISECONDS, Schedulers.computation())

            Observable.zip(slice20Bytes, messageDelay, BiFunction { message: ByteArray, _: Long ->
                return@BiFunction message
            })
                    .doOnNext {
                        gattChar?.setValue(it)
                        gatt?.writeCharacteristic(gattChar)
                    }
                    .takeLast(1)
                    .subscribe(
                            { sendBroadCast(BluetoothRecive.SEND, byteArray) },
                            { sendBroadCast(BluetoothRecive.ERROR, "BLUETOOTH MESSAGE SEND ERROR") }
                    )
        }

        override fun findServiceAndChar(bluetoothGatt: BluetoothGatt): Pair<BluetoothGattService, BluetoothGattCharacteristic>? {
            var result: Pair<BluetoothGattService, BluetoothGattCharacteristic>? = null
            service@ for (service in bluetoothGatt.services) {
                char@ for (char in service.characteristics) {
                    if (isWriteable(char) && isNotify(char)) {
                        result = service to char
                        break@service
                    }
                }
            }
            return result
        }

        override fun disconnect() {
            this.gatt?.close()
        }

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            sendBroadCast(BluetoothRecive.STATUS, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) gatt?.discoverServices()
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            val protocol = findServiceAndChar(gatt!!)
            this.gatt = gatt
            this.gattChar = protocol?.second
            if (protocol == null) {
                sendBroadCast(BluetoothRecive.ERROR, "NOT FOUND SERVICE")
                return
            }

            if (!gatt!!.setCharacteristicNotification(protocol.second, true)) {
                sendBroadCast(BluetoothRecive.ERROR, "")
            } else {
                sendBroadCast(BluetoothRecive.STATUS, 4)
            }

        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
        }

        val response: ArrayList<Byte> = arrayListOf()

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            super.onCharacteristicChanged(gatt, characteristic)
            characteristic?.value?.let {
                response.addAll(it.toTypedArray())
                if (response.size == 64) {
                    val message = response.toByteArray().toString(Charsets.UTF_8).split(" ").toTypedArray()
                    val broadCastName = "kr.or.poin.bluetooth"
                    val intent = Intent(broadCastName).apply {
                        putExtra("type", "response")
                        putExtra("message", message)
                    }
                    context.sendBroadcast(intent)
                    response.clear()
                }
            }
        }

        fun sendBroadCast(recive: BluetoothRecive, message: Any) {
            println("브로드캐스트 전송 $message")

            val messageType = when (recive) {
                BluetoothRecive.STATUS -> "status"
                BluetoothRecive.SEND -> "send"
                BluetoothRecive.RESPONSE -> "response"
                BluetoothRecive.ERROR -> "error"
            }

            val broadCastName = "kr.or.poin.bluetooth"
            val intent = Intent(broadCastName).apply {
                putExtra("type", messageType)
                when (message) {
                    is ByteArray -> putExtra("message", message)
                    is Int -> putExtra("message", message)
                    is String -> putExtra("message", message)
                }
            }

            context.sendBroadcast(intent)
        }

        fun isWriteable(characteristic: BluetoothGattCharacteristic): Boolean {
            return (characteristic.properties and (BluetoothGattCharacteristic.PROPERTY_WRITE or BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) != 0
        }

        fun isNotify(characteristic: BluetoothGattCharacteristic): Boolean {
            return (characteristic.properties and (BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0)
        }
    }

    /**
     * Bluetooth Message Encryption Key
     */
    val key = byteArrayOf(
            0x2B, 0x7E, 0x15, 0x16, 0x28,
            0xAE.toByte(), 0xD2.toByte(), 0xA6.toByte(), 0xAB.toByte(), 0xF7.toByte(),
            0x15, 0x88.toByte(), 0x09, 0xCF.toByte(), 0x4F,
            0x3C)

    /**
     * Bluetooth Message Encryption IV
     */
    val iv = byteArrayOf(
            0x00, 0x01, 0x02, 0x03, 0x04,
            0x05, 0x06, 0x07, 0x08, 0x09,
            0x0A, 0x0B, 0x0C, 0x0D, 0x0E,
            0x0F)

    /**
     * Poin Bluetooth Message Builder
     */
    @Provides
    fun messageBuilder(): MessageBuilder = object : MessageBuilder {
        val random = SecureRandom()

        // 명령 생성
        // 명령은 총 64byte
        // ex > dummy(4byte) + commend(?) + commend_crc(2) + dummy(?) = 64
        @Throws(IllegalStateException::class)
        override fun generateCommend(commend: String): ByteArray {
            val commendMessage = commend.toByteArray(Charsets.UTF_8)

            val messageTotalSize = 64
            val checksumSize = 2
            val messageSize = commendMessage.size + checksumSize
            val randomFirstByteSize = 4
            val randomLastByteSize = messageTotalSize - randomFirstByteSize - messageSize

            if (randomLastByteSize <= 0) throw IllegalArgumentException("TO LONG COMMEND MESSAGE")

            val first4RandomBytes = random.generateSeed(randomFirstByteSize)
            val lastRandomBytes = random.generateSeed(randomLastByteSize)
            val checksum = checksum(commendMessage)

            val totalMessage = byteArrayOf(*first4RandomBytes, *commendMessage, *checksum, *lastRandomBytes)

            return encryption(totalMessage)
        }

        // 명령문 암호화
        override fun encryption(byteArray: ByteArray): ByteArray {
            val cipher = Cipher.getInstance("AES/CBC/NoPadding")
            val keySpec = SecretKeySpec(key, "AES")
            val ivParameterSpec = IvParameterSpec(iv)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec)

            return cipher.doFinal(byteArray)
        }

        // 복호화 이후 명령문 파라미터 추출
        override fun decryption(byteArray: ByteArray): Array<String> {
            val cipher = Cipher.getInstance("AES/CBC/NoPadding")
            val keySpec = SecretKeySpec(key, "AES")
            val ivParameterSpec = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec)

            val plainText = cipher.doFinal(byteArray)

            val commendMessage = Observable.fromIterable(plainText.toList())
                    .skip(4)
                    .collectInto(byteArrayOf(), BiConsumer { acc: ByteArray, byte -> acc.plus(byte) })
                    .filter { checksum(it) == byteArrayOf(0, 0) }.blockingGet()

            val commendString = commendMessage.toString(Charsets.UTF_8)

            return commendString.split(" ").toTypedArray()
        }

        // 명령의 메시지 정상 수신을 알리는 checksum
        override fun checksum(byteArray: ByteArray): ByteArray {
            var crc = 0xffff
            var flag: Int

            for (b in byteArray) {
                crc = crc xor (b.toInt() and 0xff)

                for (i in 0..7) {
                    flag = crc and 1
                    crc = crc shr 1
                    if (flag != 0) {
                        crc = crc xor 0xa001
                    }
                }
            }

            val buffer = ByteBuffer.allocate(2)
            buffer.order(ByteOrder.LITTLE_ENDIAN)

            while (buffer.hasRemaining()) {
                buffer.putShort(crc.toShort())
            }

            return buffer.array()
        }
    }

    /**
     * BLE Device Scanner for RxKotlin
     *
     * scanDevice return Observable<BluetoothDevice>
     * if device not found after the scanning is complite, it returns an error
     */
    @Provides
    fun scanner(bluetoothAdapter: BluetoothAdapter): BluetoothScanner = object : BluetoothScanner {
        var callback: BluetoothAdapter.LeScanCallback? = null

        override fun scanDevice(macAddress: String, timer: Long): Observable<BluetoothDevice> =
                Observable.create { emitter: Emitter<BluetoothDevice> ->
                    var counter = 0

                    callback = object : BluetoothAdapter.LeScanCallback {
                        override fun onLeScan(device: BluetoothDevice?, p1: Int, p2: ByteArray?) {
                            val name = device?.name?.toUpperCase()?.replace(":", "")
                            if (name == macAddress) {
                                counter++
                                emitter.onNext(device)
                            }
                        }
                    }

                    bluetoothAdapter.startLeScan(callback)

                    // 지정된 시간내에 장치를 찾지못하면 에러를 반환합니다
                    // if (counter == 0) emitter.onError(NullPointerException("Not Found Device"))
                    // emitter.onComplete()
                }.timeout(timer, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())

        override fun stopScan() {
            bluetoothAdapter.stopLeScan(callback)
        }
    }

    /**
     * Poin Device Controller
     * start, insertCoin, finish
     *
     * 'Start' is Mobile to poin device send message for connected to ready service
     * 'Finish' is Allways send to poin device for user is payment finish
     */
    @Provides
    fun vendingMachineController(bluetoothController: BluetoothController, messageBuilder: MessageBuilder)
            : VendingMachineController = object : VendingMachineController {
        override fun connect(device: BluetoothDevice) {
            bluetoothController.connect(device)
        }

        override fun start(userId: String) {
            val datetime = SimpleDateFormat("yyMMddHHmmss").format(Date())
            val message = messageBuilder.generateCommend("CMD S 1 $userId $datetime")
            bluetoothController.sendMessage(message)
        }

        override fun insertCoin(coin: Int) {
            val message = messageBuilder.generateCommend("CMD S 2 $coin")

            println(message.size)
            bluetoothController.sendMessage(message)
        }

        override fun finish() {
            val message = messageBuilder.generateCommend("CMD S 3")
            bluetoothController.sendMessage(message)
        }

    }
}