package com.tirex.marvelsuperheros

import com.google.gson.annotations.SerializedName

data class SuperHeroDetailsResponse(
    @SerializedName("code") val code: Int?,
    @SerializedName("status") val status: String?,
    @SerializedName("copyright") val copyright: String?,
    @SerializedName("attributionText") val attributionText: String?,
    @SerializedName("attributionHTML") val attributionHTML: String?,
    @SerializedName("data") val data: CharacterData?,
    @SerializedName("etag") val etag: String?
)

// La clase que contiene la lista de personajes.
data class CharacterData(
    @SerializedName("offset") val offset: Int?,
    @SerializedName("limit") val limit: Int?,
    @SerializedName("total") val total: Int?,
    @SerializedName("count") val count: Int?,
    @SerializedName("results") val results: MutableList<Character>?
)

// La clase que representa un personaje individual.
data class Character(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("modified") val modified: String?,  // Puede ser una fecha en formato String
    @SerializedName("resourceURI") val resourceURI: String?,
    @SerializedName("urls") val urls: List<Url>?,
    @SerializedName("thumbnail") val image: Image?,
    @SerializedName("comics") val comics: ComicList?,
    @SerializedName("stories") val stories: StoryList?,
    @SerializedName("events") val events: EventList?,
    @SerializedName("series") val series: SeriesList?
)

// La clase que representa una URL asociada al personaje.
data class Url(
    @SerializedName("type") val type: String?,
    @SerializedName("url") val url: String?
)

// La clase que representa una imagen del personaje.
data class Image(
    @SerializedName("path") val path: String?,
    @SerializedName("extension") val extension: String?
)

// La clase para las listas de cómics, historias, eventos, y series.
data class ComicList(
    @SerializedName("available") val available: Int?,
    @SerializedName("returned") val returned: Int?,
    @SerializedName("collectionURI") val collectionURI: String?,
    @SerializedName("items") val items: List<ComicItem>?
)

data class ComicItem(
    @SerializedName("resourceURI") val resourceURI: String?,
    @SerializedName("name") val name: String?
)

data class StoryList(
    @SerializedName("available") val available: Int?,
    @SerializedName("returned") val returned: Int?,
    @SerializedName("collectionURI") val collectionURI: String?,
    @SerializedName("items") val items: List<StoryItem>?
)

data class StoryItem(
    @SerializedName("resourceURI") val resourceURI: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("type") val type: String?
)

data class EventList(
    @SerializedName("available") val available: Int?,
    @SerializedName("returned") val returned: Int?,
    @SerializedName("collectionURI") val collectionURI: String?,
    @SerializedName("items") val items: List<EventItem>?
)

data class EventItem(
    @SerializedName("resourceURI") val resourceURI: String?,
    @SerializedName("name") val name: String?
)

data class SeriesList(
    @SerializedName("available") val available: Int?,
    @SerializedName("returned") val returned: Int?,
    @SerializedName("collectionURI") val collectionURI: String?,
    @SerializedName("items") val items: List<SeriesItem>?
)

data class SeriesItem(
    @SerializedName("resourceURI") val resourceURI: String?,
    @SerializedName("name") val name: String?
)
