package no.nav.tilleggsstonader.libs.spring.transaction

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import java.io.IOException
import javax.sql.DataSource

@SpringBootApplication
class TestApplication

@SpringBootTest(classes = [TestApplication::class, KotlinTransactionalTest.TestConfig::class])
class KotlinTransactionalTest {
    @Autowired
    private lateinit var service: KotlinTransactionalTestService

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @BeforeEach
    fun setUp() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS TEST_TABLE")
        jdbcTemplate.execute("CREATE TABLE TEST_TABLE (id SERIAL PRIMARY KEY, data VARCHAR(255))")
    }

    @Test
    fun `happy case`() {
        service.lagre("testverdi")

        assertThat(hentAntallRader()).isEqualTo(1)
    }

    @Test
    fun `skal rulle tilbake når checked exception IOException kastes`() {
        val exception =
            try {
                service.lagreMedFeil("testverdi")
            } catch (e: Exception) {
                e
            }

        assertThat(hentAntallRader()).isEqualTo(0)
        assertThat(exception).isInstanceOf(IOException::class.java)
    }

    private fun hentAntallRader(): Int? = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM TEST_TABLE", Int::class.java)

    class TestConfig {
        @Bean
        fun dataSource(): DataSource =
            EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build()

        @Bean
        fun transactionManager(dataSource: DataSource): PlatformTransactionManager = DataSourceTransactionManager(dataSource)

        @Bean
        fun jdbcTemplate(dataSource: DataSource): JdbcTemplate = JdbcTemplate(dataSource)
    }
}

@Service
class KotlinTransactionalTestService(
    private val jdbcTemplate: JdbcTemplate,
) {
    @KotlinTransactional
    fun lagre(data: String) {
        jdbcTemplate.update("INSERT INTO TEST_TABLE (data) VALUES (?)", data)
    }

    @KotlinTransactional
    fun lagreMedFeil(data: String) {
        jdbcTemplate.update("INSERT INTO TEST_TABLE (data) VALUES (?)", data)
        throw IOException("Simulert feil")
    }
}
