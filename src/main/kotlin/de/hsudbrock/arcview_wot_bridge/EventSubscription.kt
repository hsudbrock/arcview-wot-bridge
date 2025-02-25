package de.hsudbrock.arcview_wot_bridge

import ai.ancf.lmos.wot.Wot
import ai.ancf.lmos.wot.thing.ConsumedThing
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Subscription
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Component

@Component
class EventSubscription(private val wot: Wot,  private val objectMapper: ObjectMapper): Subscription {
    @GraphQLDescription("Subscribes to events.")
    suspend fun events(): Flow<AgentEvent> {
        val runtimeAgentDesc = wot.requestThingDescription("http://localhost:8182/runtime-agent")
        val runtimeAgent = wot.consume(runtimeAgentDesc) as ConsumedThing
        return runtimeAgent.consumeEvent("event").map { wotInteractionOutput ->
            objectMapper.treeToValue(wotInteractionOutput.value(), AgentEvent::class.java)
        }
    }
}

data class AgentEvent(
    val type: String,
    val payload: String,
    val conversationId: String?,
    val turnId: String?,
)