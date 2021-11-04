package com.xunfos.graphqldemo.client

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.xunfos.generated.ArtistQuery
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class ClientApplication

fun main(args: Array<String>) {
    runApplication<ClientApplication>(*args)
}

@RestController
class MyBananaController(
    private val graphQLWebClient: GraphQLWebClient,
) {
    @GetMapping("/artists")
    suspend fun getArtists() {
        val artistQuery = ArtistQuery()
        val result = graphQLWebClient.execute(artistQuery)
        println(result.data)
    }
}

@Configuration
class Configs {
    @Bean
    fun graphqlClient(): GraphQLWebClient = GraphQLWebClient(url = "http://localhost:8080/graphql")
}
