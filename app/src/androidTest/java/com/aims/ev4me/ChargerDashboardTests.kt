package com.aims.ev4me

import android.util.Log
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Debug this file, it will pause after each page is loaded
 * for you to see what it looks like and how to change it
 */
@RunWith(AndroidJUnit4::class)
class ChargerDashboardTests {

    @Test
    fun testLenderDashboard() {
        basicSetup("test1@test.com", "testing", R.id.navigation_dashboard_lender)

        Log.v("ChargerDashboardTests.kt", "We made it")
    }

    @Test
    fun testUserDashboard() {
        basicSetup("test2@test.com", "testing", R.id.navigation_dashboard_user)

        Log.v("ChargerDashboardTests.kt", "We made it")
    }

    fun basicSetup(username: String, password: String, viewToClick: Int) {
        logOut()
        launchActivity<LoginActivity>().use {
            onView(withId(R.id.email_input_field))
                .perform(typeText(username))
            onView(withId(R.id.password_input_field))
                .perform(typeText(password))
            onView(withId(R.id.login_button))
                .perform(scrollTo())
                .perform(click())

            ConditionWatcher.waitForCondition(existsViewWithID("Waiting for main page", viewToClick))
            onView(withId(viewToClick)).perform(click())
        }
    }

    private fun logOut() {
        Firebase.auth.signOut()
    }

}

class existsViewWithID(override val description: String, override val id: Int) :
    Instruction() {
    override fun checkCondition(): Boolean {
        //Nah cuz what is Kotlin
        val viewFound = try {
            onView(withId(id)).perform(click())
            true
        } catch (e: NoMatchingViewException) {
            false
        } catch (e: Exception) {
            false
        }
        return viewFound
    }

}