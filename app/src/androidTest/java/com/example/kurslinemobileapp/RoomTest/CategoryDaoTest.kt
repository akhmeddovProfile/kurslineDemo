package com.example.kurslinemobileapp.RoomTest

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.kurslinemobileapp.service.Room.AppDatabase
import com.example.kurslinemobileapp.service.Room.category.CategoryDao
import com.example.kurslinemobileapp.service.Room.category.CategoryEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SmallTest
class CategoryDaoTest {
    private lateinit var  kurslinedatabase:AppDatabase
    private lateinit var categoryDao:CategoryDao

    @Before
    fun setup(){
        kurslinedatabase=Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),AppDatabase::class.java
        ).allowMainThreadQueries().build()
        categoryDao=kurslinedatabase.categoryDao()
    }

    @After
    fun closeDb(){
        kurslinedatabase.close()
    }
    @Test
    fun insertCategoryItem()= runBlocking {
        val categoryEntity=CategoryEntity(5,"It")
        categoryDao.insertAll(listOf(categoryEntity))

        val allCategory=categoryDao.getAllCategories()
        assertThat(allCategory)
    }
}