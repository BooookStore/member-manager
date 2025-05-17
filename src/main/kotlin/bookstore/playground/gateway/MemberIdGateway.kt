package bookstore.playground.gateway

import bookstore.playground.domain.MemberId
import bookstore.playground.driver.JdkUUIDDriver
import bookstore.playground.port.MemberIdPort

object MemberIdGateway : MemberIdPort {

    override fun generateId(): MemberId = MemberId(JdkUUIDDriver.generateUUID())

}