package com.android.landmarks.presentation.notesmanager

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.android.landmarks.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotesManagerActivityTest {

    @get:Rule
    var activityRule = ActivityTestRule(NotesManagerActivity::class.java)

    @Test
    fun isFabDisplayed() {
        onView(withId(R.id.notes_fab_add_note)).check(matches(isDisplayed()))
    }

    @Test
    fun isSwitchButtonDisplayed() {
        onView(withId(R.id.notes_switch_my_notes)).check(matches(isDisplayed()))
    }

    @Test
    fun isToolbarImageDisplayed() {
        onView(withId(R.id.menu_main_search)).check(matches(isDisplayed()))
    }
}
