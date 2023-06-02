package com.cognizant.openweather

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.cognizant.openweather.ui.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MainActivityTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    var permissionRule = GrantPermissionRule.grant(
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION"
    )


    @Before
    fun setup() {
        hiltRule.inject()
    }


    @Test
    fun testHappyUserFlow() {

        composeTestRule.onNodeWithTag("search_screen_container")
            .assertIsDisplayed()

        // wait until the weather screen after weather data is loaded using the location or last known search location
        waitUntilExists("weather_screen_container")

        // Check if the weather screen is displayed correctly
        composeTestRule.onNodeWithTag("weather_screen_container")
            .assertIsDisplayed()

        // Attempt to change location
        composeTestRule.onNodeWithTag("change_location_button")
            .assertExists()
            .performClick()

        // Check if the search screen is displayed correctly
        composeTestRule.onNodeWithTag("search_screen_container")
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("search_field")
            .assertIsDisplayed()
            .assertIsEnabled()
            .performClick()
            .performTextClearance()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("search_field")
            .performTextInput("New York")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("search_button")
            .assertIsDisplayed()
            .assertIsEnabled()
            .performClick()


        // wait until the weather screen after weather data is loaded using the location or last known search location

        waitUntilExists("weather_screen_container")

        // Check if the weather screen is displayed correctly
        composeTestRule.onNodeWithTag("weather_screen_container")
            .assertIsDisplayed()


    }

    fun waitUntilExists(tag: String, timeout: Long = 10000) {
        val startTime = System.currentTimeMillis()
        while (true) {
            try {
                composeTestRule.onNodeWithTag(tag)
                    .assertExists()
                break
            } catch (e: AssertionError) {
                if (System.currentTimeMillis() - startTime >= timeout) {
                    throw e
                }
                Thread.sleep(100) // Sleep for a short time before trying again
            }
        }
    }

}
