package com.xunfos.graphqldemo.client

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.xunfos.generated.ArtistIdQuery
import com.xunfos.generated.artistidquery.Artist
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class ClientApplication

fun main(args: Array<String>) {
    runApplication<ClientApplication>(*args)
}

@RestController
class MyDummyController(
    private val graphQLWebClient: GraphQLWebClient
) {
    @GetMapping("/artists")
    suspend fun artists(): List<Artist> {
        val query = ArtistIdQuery()
        val result = graphQLWebClient.execute(query)
        return result.data?.getArtists ?: error("something went funky")
    }
}

@Configuration
class GraphQLConfiguration {
    @Bean
    fun webclient() = GraphQLWebClient(url = "http://localhost:8080/graphql")
}
