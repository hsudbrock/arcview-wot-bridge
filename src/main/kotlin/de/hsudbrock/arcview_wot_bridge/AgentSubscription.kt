package de.hsudbrock.arcview_wot_bridge

import ai.ancf.lmos.wot.Wot
import ai.ancf.lmos.wot.thing.ConsumedThing
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Subscription
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class AgentSubscription(private val wot: Wot): Subscription {

    @GraphQLDescription("Executes an Agent and returns the results. If no agent is specified, the first agent is used.")
    suspend fun agent(agentName: String? = null, request: AgentRequest) : Flux<AgentResult> {

        val tenantId = request.systemContext.first { it.key == "tenantId" }.value
        val channelId = request.systemContext.first { it.key == "channelId" }.value
        val conversationId = request.conversationContext.conversationId

        val runtimeAgentDesc = wot.requestThingDescription("http://localhost:8080/runtime-agent")
        val runtimeAgent = wot.consume(runtimeAgentDesc) as ConsumedThing

        val result = runtimeAgent.invokeAction<ChatInput, AssistantMessage>(
            actionName = "chat", input = ChatInput(
                conversation = Conversation(
                    inputContext = InputContext(
                        messages = request.messages,
                        anonymizationEntities = request.conversationContext.anonymizationEntities
                    ),
                    systemContext = SystemContext(
                        channelId = channelId,
                        contextParams = mapOf()
                    ),
                    userContext = request.userContext
                ),
                turnId = request.conversationContext.turnId ?: "unknown turn",
                conversationId = conversationId,
                tenantId = tenantId
            )
        )

        return Flux.just(
            AgentResult(
                status = "all good",
                responseTime = 2.0,
                messages = listOf(
                    Message(
                        role = "assistant",
                        content = result.content,
                        turnId = request.conversationContext.turnId
                    )
                ),
                anonymizationEntities = listOf()
            )
        )

    }
}

data class ChatInput(
    val conversation: Conversation,
    val turnId: String,
    val conversationId: String,
    val tenantId: String
)

data class AgentRequest(
    val messages: List<Message>,
    val conversationContext: ConversationContext,
    val systemContext: List<SystemContextEntry>,
    val userContext: UserContext,
)

data class Message(
    val role: String,
    val content: String,
    val format: String = "text",
    val turnId: String? = null,
)

data class ConversationContext(
    val conversationId: String,
    val turnId: String? = null,
    val anonymizationEntities: List<AnonymizationEntity>? = null,
)

data class UserContext(
    val userId: String? = null,
    val userToken: String? = null,
    val profile: List<ProfileEntry>,
)

data class ProfileEntry(
    val key: String,
    val value: String,
)

data class AnonymizationEntity(val type: String, val value: String, val replacement: String)

data class SystemContextEntry(
    val key: String,
    val value: String,
)

data class AgentResult(
    val status: String? = null,
    val responseTime: Double = -1.0,
    val messages: List<Message>,
    val anonymizationEntities: List<AnonymizationEntity>? = null,
)

data class Conversation(
    val inputContext: InputContext,
    val systemContext: SystemContext,
    val userContext: UserContext,
)

data class InputContext(
    val messages: List<Message>,
    val anonymizationEntities: List<AnonymizationEntity>? = null,
)

data class SystemContext(
    val channelId: String,
    val contextParams: Map<String, String> = mapOf(),
)


sealed class ChatMessage {
    abstract val content: String
}

data class AssistantMessage(
    override val content: String,
    val anonymizationEntities: List<AnonymizationEntity>? = emptyList(),
) : ChatMessage()