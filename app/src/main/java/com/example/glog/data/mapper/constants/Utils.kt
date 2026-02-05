package com.example.glog.data.mapper.constants

object Utils {

    internal fun String?.formatEmpty(default: String = "Desconocido"): String {
        return if (this.isNullOrBlank()) default else this
    }

    internal fun Int?.formatGenre(): String {
        return if (this != null) GenreConstants.getGenreNameById(this)
        else "Género desconocido"
    }

    internal fun Int?.formatPlatform(): String {
        return if (this != null) PlatformConstants.getPlatformNameById(this)
        else "Plataforma desconocida"
    }

    // Para listas de IDs
    internal fun Int?.formatGenres(): String {
        return this?.formatGenre() ?: "Géneros desconocidos"
    }

    internal fun Int?.formatPlatforms(): String {
        return this?.formatPlatform() ?: "Plataformas desconocidas"
    }


}