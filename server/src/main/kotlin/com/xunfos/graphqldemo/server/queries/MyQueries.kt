package com.xunfos.graphqldemo.server.queries

import com.expediagroup.graphql.dataloader.KotlinDataLoader
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.extensions.getValueFromDataLoader
import com.expediagroup.graphql.server.operations.Query
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.future
import kotlinx.coroutines.runBlocking
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class MyQueries(
    private val artistService: ArtistService,
    private val albumService: AlbumService,
) : Query {
    suspend fun artists(): List<Artist> = artistService.getAll()
    suspend fun albums(): List<Album> = albumService.getAll()
}

data class Artist(val id: Int, @GraphQLDescription("badassName") val name: String) {
    suspend fun albums(): List<Album> = AlbumService().getByArtistId(id)
}

data class Album(val id: Int, val name: String, val artistId: Int) {
    suspend fun artists(): Artist {
        return ArtistService().getById(id = artistId) ?: error("not found")
    }
}
data class Song(val id: Int, val name: String, val artistId: Int) {
    fun artist(dataFetchingEnvironment: DataFetchingEnvironment): CompletableFuture<Artist> {
        return dataFetchingEnvironment.getValueFromDataLoader(ArtistDataLoader.NAME, artistId)
    }
}

@Component
class MyDataFetcherQueries() : Query {
    fun songs(): List<Song> = listOf(
        Song(id = 1, name = "Powerslave", artistId = 1),
        Song(id = 2, name = "Running Free", artistId = 1),
        Song(id = 3, name = "Ride the Lightning", artistId = 2),
        Song(id = 4, name = "Master of Puppets", artistId = 2),

    )
}

@Component
class ArtistDataLoader(
    private val artistService: ArtistService,
) : KotlinDataLoader<Int, Artist> {
    override val dataLoaderName: String = NAME

    override fun getDataLoader(): DataLoader<Int, Artist> = DataLoaderFactory.newDataLoader { ids ->
        runBlocking {
            future {
               artistService.getByIds(ids)
            }
        }
    }

    companion object {
        const val NAME = "artistDataLoader"
    }
}




@Component
class ArtistService {
    private val artists = listOf(
        Artist(id = 1, name = "Iron Maiden"),
        Artist(id = 2, name = "Metallica"),
    )

    suspend fun getAll(): List<Artist> {
        return artists
    }

    suspend fun getById(id: Int): Artist? {
        delay(2_000)
        return artists.find { it.id == id }
    }
    suspend fun getByIds(ids: List<Int>): List<Artist> {
        delay(2_000)
        return artists.filter{ it.id in ids }
    }
}

@Component
class AlbumService {
    val albums = listOf(
        Album(id = 1, artistId = 1, name = "Senjutsu"),
        Album(id = 2, artistId = 1, name = "Powerslave"),
        Album(id = 3, artistId = 2, name = "Master of Puppets"),
        Album(id = 4, artistId = 2, name = "St. Anger"),
    )

    suspend fun getAll(): List<Album> {
        return albums
    }
    suspend fun getByArtistId(artistId: Int): List<Album> {
        return albums.filter { it.artistId == artistId }
    }
}