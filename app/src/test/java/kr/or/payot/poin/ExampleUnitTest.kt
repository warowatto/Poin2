package kr.or.payot.poin

import io.reactivex.schedulers.Schedulers
import kr.or.payot.poin.DI.Component.Tests.DaggerNetworkTestComponent
import kr.or.payot.poin.DI.Modules.NetworkModule
import kr.or.payot.poin.RESTFul.MachineAPI
import kr.or.payot.poin.RESTFul.UserAPI
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import retrofit2.HttpException
import java.util.regex.Pattern

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    lateinit var machine: MachineAPI
    lateinit var user: UserAPI

    @Before
    fun init() {
        val component = DaggerNetworkTestComponent.builder()
                .networkModule(NetworkModule())
                .build()
        machine = component.machineAPI()
        user = component.userAPI()
    }

    @Test
    fun cardAdd() {
        val cardNumber = "9420-6110-9453-3079"
        val expiry = "2022-02"
        val birth = "910711"
        val password = "73"
        val name = "NPay Card"
        user.addCard(7, name, cardNumber, expiry, birth, password)
                .subscribe(
                        {
                            println(it)
                        },
                        {
                            it.printStackTrace()
                            when (it) {
                                is HttpException -> {
                                    val message = it.response().message()
                                    println(message)
                                }
                            }
                        }
                )
    }

    @Test
    fun getMachines() {
        val macAddress = "D4:36:39:D8:35:72"
        machine.getMachine(macAddress)
                .subscribeOn(Schedulers.trampoline())
                .subscribe(
                        {
                            println(it.company)
                            println(it.isRunning)
                            println(it.products)
                        },
                        {
                            it.printStackTrace()
                        })
    }
}
