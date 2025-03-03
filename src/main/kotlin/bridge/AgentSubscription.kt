package ai.ancf.lmos.arc.bridge


import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Subscription
import io.opentelemetry.instrumentation.annotations.WithSpan
import org.eclipse.lmos.sdk.agents.ConversationalAgent
import org.eclipse.lmos.sdk.agents.toAgentResult
import org.eclipse.lmos.sdk.model.AgentRequest
import org.eclipse.lmos.sdk.model.AgentResult
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class AgentSubscription(private val conversationalAgent: ConversationalAgent): Subscription {

    @GraphQLDescription("Executes an Agent and returns the results. If no agent is specified, the first agent is used.")
    @WithSpan
    suspend fun agent(agentName: String? = null, request: AgentRequest) : Flux<AgentResult> {
        try{
            val response = conversationalAgent.chat(request)
            return Flux.just(response)
        }catch (e: Exception){
            return Flux.just(e.message!!.toAgentResult())
        }

    }
}