package ai.ancf.lmos.arc.bridge


import kotlinx.coroutines.runBlocking
import org.eclipse.lmos.sdk.agents.WotConversationalAgent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfiguration {

    @Bean
    fun conversationalAgent() = runBlocking {
        WotConversationalAgent.create("http://localhost:8181/scraper")
    }
}