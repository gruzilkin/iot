package gruzilkin.iot.repositories

import gruzilkin.iot.entities.Token
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.DirtiesContext
import kotlin.test.*

@DataJpaTest
class TokenRepositoryTest {
        @Autowired
        lateinit var tokenRepository: TokenRepository

        @Test
        @DirtiesContext
        fun `should save token`() {
            val token = Token(deviceId = 1)
            val savedToken = tokenRepository.save(token)
            assertEquals(savedToken.deviceId, token.deviceId)
            assertNotNull(savedToken.token)
        }

    @Test
    @DirtiesContext
    fun `should find tokens by device id`() {
        tokenRepository.save(Token(deviceId = 1))
        tokenRepository.save(Token(deviceId = 1))
        val tokens = tokenRepository.findByDeviceId(1)
        assertEquals(2, tokens.size)
    }
}