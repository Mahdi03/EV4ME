package com.aims.ev4me

import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.SmallTest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @Test
    fun testSuccessfulLogin() {
        logOut()
        launchActivity<LoginActivity>().use {
            onView(withId(R.id.email_input_field)).perform(typeText("administrator@admin.com"))
            onView(withId(R.id.password_input_field)).perform(typeText("administrator"))
            onView(withId(R.id.login_button)).perform(click())
            //Check that MainActivity was opened - whatever I give up just don't run this lmao
            intended(hasComponent(MainActivity::class.java.name))
        }
    }

    @SmallTest
    fun testSuccessfulRegistration() {
        logOut()
        launchActivity<LoginActivity>().use {
            onView(withId(R.id.register_button)).perform(click())
            intended(hasComponent(RegisterActivity::class.java.name))
        }
    }

    /*
    @Test
    fun testUnsuccessfulLogin() {
        logOut()
        launchActivity<LoginActivity>().use {
            onView(withId(R.id.email_input_field)).perform(typeText("wrongemail"))
            onView(withId(R.id.password_input_field)).perform(typeText("wrongpass"))
            onView(withId(R.id.login_button)).perform(click())
            //TODO: We need to have error handling before we test
               the error handling
        }
    }
    */


    private fun logOut() {
        Firebase.auth.signOut()
    }
}