package com.xunfos.graphqldemo.server.queries

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import kotlinx.coroutines.delay
import org.springframework.stereotype.Component

@Component
class MyFunQueries : Query {
    suspend fun artists(id: Int? = null): List<Artist> {
        return id?.let {
            listOf(artists.find { it.id == id } ?: error("no artist Found"))
        } ?: artists
    }
}

val artists = listOf(
    Artist(id = 1, name = "Iron Maiden"),
    Artist(id = 2, name = "Metallica"),
)


val albums = listOf(
    Album(id = 1, artistId = 1, name = "Senjutsu"),
    Album(id = 2, artistId = 1, name = "Powerslave"),
    Album(id = 3, artistId = 2, name = "Master of Puppets"),
    Album(id = 4, artistId = 2, name = "St. Anger"),
)


data class Artist(
    @GraphQLDescription("asdasd")
    val id: Int,
    @Deprecated("hey, please use another field")
    val name: String,
) {
    suspend fun albums(): List<Album> {
        delay(1000)
        return albums.filter { it.artistId == id }
    }
}

data class Album(val id: Int, val artistId: Int, val name: String)
