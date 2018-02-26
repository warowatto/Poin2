package kr.or.payot.poin.DI.Component.Tests

import dagger.Component
import kr.or.payot.poin.DI.Modules.NetworkModule
import kr.or.payot.poin.RESTFul.MachineAPI
import kr.or.payot.poin.RESTFul.UserAPI
import javax.inject.Singleton

/**
 * Created by yongheekim on 2018. 2. 21..
 */

@Singleton
@Component(modules = arrayOf(NetworkModule::class))
interface NetworkTestComponent {

    fun userAPI(): UserAPI

    fun machineAPI(): MachineAPI
}