package com.example.moneyflow.features.goal.presentation.mapper

import com.example.moneyflow.data.local.entity.SavingGoalEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class GoalUiMapperTest {

    @Test
    fun `mapSavingGoal calculates progress percentage correctly`() {
        val entity = SavingGoalEntity(
            id = 1L,
            name = "MacBook",
            targetAmount = 1000L,
            currentAmount = 750L
        )
        
        val uiModel = GoalUiMapper.mapSavingGoal(entity)
        
        assertEquals(0.75f, uiModel.progress)
        assertEquals(75, uiModel.progressPercent)
    }

    @Test
    fun `mapSavingGoal handles zero target without crashing`() {
        val entity = SavingGoalEntity(name = "Empty", targetAmount = 0L, currentAmount = 0L)
        val uiModel = GoalUiMapper.mapSavingGoal(entity)
        assertEquals(0f, uiModel.progress)
    }
}
