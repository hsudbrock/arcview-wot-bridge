package ai.ancf.lmos.arc.bridge

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component

@Component
class ChatQuery: Query {

    @GraphQLDescription("agents query")
    fun agent(): Agents {
        return Agents(listOf("scraper"))
    }

}

data class Agents(val names: List<String>)