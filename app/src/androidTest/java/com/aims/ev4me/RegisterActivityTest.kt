package com.aims.ev4me

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterActivityTest {

    @Test
    fun testTooShortPasswordRegister() {
        logOut()
        launchActivity<RegisterActivity>().use {
            onView(withId(R.id.input_firstName)).perform(typeText("John"))
            onView(withId(R.id.input_lastName)).perform(typeText("Doe"))
            onView(withId(R.id.input_email)).perform(typeText("administrator@admin.com"))
            onView(withId(R.id.input_password)).perform(typeText("password"))
            onView(withId(R.id.input_confirmPassword)).perform(typeText("password"))

            onView(withId(R.id.nextPageButton)).perform(click())
        }
    }


    private fun logOut() {
        Firebase.auth.signOut()
    }

}