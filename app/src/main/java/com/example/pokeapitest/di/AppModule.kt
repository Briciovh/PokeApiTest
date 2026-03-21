package com.example.pokeapitest.di

import android.content.Context
import androidx.room.Room
import com.example.pokeapitest.data.local.PokemonDao
import com.example.pokeapitest.data.local.PokemonDatabase
import com.example.pokeapitest.data.remote.PokeApi
import com.example.pokeapitest.data.repository.PokemonRepository
import com.example.pokeapitest.data.repository.PokemonRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePokeApi(): PokeApi {
        return Retrofit.Builder()
            .baseUrl(PokeApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(PokeApi::class.java)
    }

    @Provides
    @Singleton
    fun providePokemonDatabase(@ApplicationContext context: Context): PokemonDatabase {
        return Room.databaseBuilder(
            context,
            PokemonDatabase::class.java,
            "pokemon_db"
        ).build()
    }

    @Provides
    @Singleton
    fun providePokemonDao(db: PokemonDatabase): PokemonDao {
        return db.dao
    }

    @Provides
    @Singleton
    fun providePokemonRepository(
        api: PokeApi,
        dao: PokemonDao
    ): PokemonRepository {
        return PokemonRepositoryImpl(api, dao)
    }
}
