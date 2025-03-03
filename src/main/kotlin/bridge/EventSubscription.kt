package ai.ancf.lmos.arc.bridge


import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Subscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import org.eclipse.lmos.sdk.agents.ConsumedConversationalAgent
import org.eclipse.lmos.sdk.model.AgentEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class EventSubscription(private val conversationalAgent: ConsumedConversationalAgent): Subscription {

    private val log = LoggerFactory.getLogger(EventSubscription::class.java)

    @GraphQLDescription("Subscribes to events.")
    suspend fun events(): Flow<AgentEvent> {
        log.info("Subscribed to agentEvents")
        return conversationalAgent.consumeEvent("agentEvent", AgentEvent::class)
            .onCompletion {
                log.info("Unsubscribed from agentEvents")
            }
    }

}
