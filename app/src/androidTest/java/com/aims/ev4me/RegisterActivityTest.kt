package com.aims.ev4me

import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.FailureHandler
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.ktor.util.reflect.*
import org.apache.commons.lang3.RandomStringUtils
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matcher
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterActivityTest {

    @Suppress("SpellCheckingInspection")
    fun generateRandomEmail(localEmailLength: Int, host: String = "gmail.com"): String {
        val ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyz" + "1234567890" + "_-."
        val firstLetter = RandomStringUtils.random(1, 'a'.code, 'z'.code, false, false)
        val temp = if (localEmailLength == 1) "" else RandomStringUtils.random(
            localEmailLength - 1,
            ALLOWED_CHARS
        )
        return "$firstLetter$temp@$host"
    }

    @Test
    fun testLenderRegistration() {
        logOut()
        /*
        val firestore = Firebase.firestore
        firestore.useEmulator("10.0.2.2", 8080)

        firestore.firestoreSettings = firestoreSettings {
            isPersistenceEnabled = false
        }
         */

        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        launchActivity<RegisterActivity>().use { activityScenario ->
            activityScenario.onActivity { activity ->
                navController.setGraph(R.navigation.register_navigation)
                Navigation.setViewNavController(
                    activity.findViewById(R.id.nav_host_fragment_register_activity),
                    navController
                )
            }

            //We start in basic_user_info
            onView(withId(R.id.input_firstName)).perform(typeText("John"))
            onView(withId(R.id.input_lastName)).perform(typeText("Doe"))

            //All this shit just to select a value from the fucking drop-down
            onView(withId(R.id.input_accountTypeDropdown)).perform(click())
            onData(anything()).atPosition(1).perform(click())
            //onData(allOf(`is`(instanceOf(String.Companion::class.java::class)), `is`("Lender"))).perform(click())
            //onView(withId(R.id.input_accountTypeDropdown)).check(matches(withSpinnerText(containsString("lender"))))

            onView(withId(R.id.input_email))
                .perform(typeText(generateRandomEmail(5, "test.co")))
                //.perform(typeText("a@a.com"))
            onView(withId(R.id.input_password)).perform(typeText("password"))
            onView(withId(R.id.input_confirmPassword)).perform(typeText("password"))

            onView(withId(R.id.nextPageButton)).perform(click())

            //We need to implement a specific class to wait for the transitions between pages
            class FragmentTransitionInstructionToSellersPart1(override val description: String,
                                                              override val id: Int) :
                Instruction() {
                override fun checkCondition(): Boolean {
                    //Check that either the registration passed or we failed with an error, both should trigger the countdown
                    //Nah cuz what is Kotlin
                    val viewFound = try {
                        onView(withId(R.id.inputStreetAddress)).perform(click())
                        true
                    } catch (e: NoMatchingViewException) {
                        false
                    } catch (e: Exception) {
                        false
                    }
                    val errorMessageShown = try {
                        onView(withId(R.id.register_error_message)).check(
                            matches(
                                withEffectiveVisibility(ViewMatchers.Visibility.GONE)
                            )
                        )
                        false
                    } catch (e: Throwable) {
                        true
                    }

                    return (viewFound or errorMessageShown)
                }

            }
            ConditionWatcher.waitForCondition(FragmentTransitionInstructionToSellersPart1("Waiting for first step of registration", -1))

            fun hello() {
                try {
                    onView(withId(R.id.register_error_message))
                        .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
                }
                catch (e: NoMatchingViewException) {
                    //That means this view was not found, we've already moved on to the next page
                    //Do nothing
                }
                catch (e: Throwable) {
                    onView(withId(R.id.input_email)).perform(replaceText(generateRandomEmail(5, "test.co")))
                    onView(withId(R.id.nextPageButton)).perform(click())
                    ConditionWatcher.waitForCondition(FragmentTransitionInstructionToSellersPart1("Waiting for first step of registration", -1))
                    hello()
                    //Do nothinggggg
                }
            }

            hello()

            //Now we've moved on to seller_part1
            onView(withId(R.id.inputStreetAddress)).perform(typeText("525 Ucen Rd"))
            onView(withId(R.id.inputCity)).perform(typeText("Isla Vista"))
            onView(withId(R.id.inputState)).perform(typeText("CA"))
            onView(withId(R.id.inputCountry))
                .perform(scrollTo())
                .perform(typeText("USA"))
            onView(withId(R.id.inputZipCode))
                .perform(scrollTo())
                .perform(typeText("93106"))

            //We need to scroll to these views because they fall out of the layout's viewport
            onView(withId(R.id.input_numChargers))
                .perform(scrollTo())
                .perform(typeText("1"))
            onView(withId(R.id.nextPageButton))
                .perform(scrollTo())
                .perform(click())



            ConditionWatcher.waitForCondition(existsViewWithID("Waiting for second step of registration", R.id.recyclerViewForChargerInfoForms))
            //Now we're at seller_part2
            onView(withId(R.id.input_chargerName)).perform(typeText("My Charger"))
            //Select the second element in the list of chargers
            onView(withId(R.id.input_chargerTypeDropdown)).perform(click())
            onData(anything()).atPosition(1).perform(click())
            onView(withId(R.id.finishRegistrationButton)).perform(click())

            ConditionWatcher.waitForCondition(existsViewWithID("Waiting for third step of registration", R.id.numberOfCarsInput))
            //Now we're at allusers_part1
            onView(withId(R.id.numberOfCarsInput)).perform(typeText("1"))
            onView(withId(R.id.nextPageButton)).perform(click())

            ConditionWatcher.waitForCondition(existsViewWithID("Waiting for fourth step of registration", R.id.recyclerViewForVehicleInfoForms))
            //Now we're at allusers_part2
            onView(withId(R.id.input_carMake)).perform(typeText("Tesla"))
            onView(withId(R.id.input_carModel)).perform(typeText("Model X"))
            onView(withId(R.id.input_carColor)).perform(typeText("white"))
            onView(withId(R.id.input_licensePlateNumber)).perform(typeText("EV4ME"))

            onView(withId(R.id.finishRegistrationButton)).perform(click())

            ConditionWatcher.waitForCondition(existsViewWithID("Waiting for end of registration", R.id.nav_view))
            //And voila!!! we are at MainActivity
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
/*
    @Test
    fun testTooShortPasswordRegister() {
        logOut()
        launchActivity<RegisterActivity>().use {
            onView(withId(R.id.input_firstName)).perform(typeText("John"))
            onView(withId(R.id.input_lastName)).perform(typeText("Doe"))
            onView(withId(R.id.input_email)).perform(typeText("administraktor@admin.com"))
            onView(withId(R.id.input_password)).perform(typeText("password"))
            onView(withId(R.id.input_confirmPassword)).perform(typeText("password"))

            onView(withId(R.id.nextPageButton)).perform(click())

        }
    }
*/

    private fun logOut() {
        Firebase.auth.signOut()
        //Firebase.auth.useEmulator("10.0.2.2", 9099)
    }

}


/**
 * ConditionWatcher library stolen from
 * https://github.com/AzimoLabs/ConditionWatcher
 * Created by F1sherKK on 08/10/15.
 */
class ConditionWatcher private constructor() {
    private var timeoutLimit = DEFAULT_TIMEOUT_LIMIT
    private var watchInterval = DEFAULT_INTERVAL

    companion object {
        const val CONDITION_NOT_MET = 0
        const val CONDITION_MET = 1
        const val TIMEOUT = 2
        const val DEFAULT_TIMEOUT_LIMIT = 1000 * 5 //Set this to 5 seconds max
        const val DEFAULT_INTERVAL = 250 //Refresh every 250ms
        private var conditionWatcher: ConditionWatcher? = null
        val instance: ConditionWatcher?
            get() {
                if (conditionWatcher == null) {
                    conditionWatcher = ConditionWatcher()
                }
                return conditionWatcher
            }

        @Throws(Exception::class)
        fun waitForCondition(instruction: Instruction) {
            waitForCondition(instruction, instance!!.timeoutLimit, instance!!.watchInterval)
        }

        @Throws(Exception::class)
        fun waitForCondition(instruction: Instruction, timeoutLimit: Int) {
            waitForCondition(instruction, timeoutLimit, instance!!.watchInterval)
        }

        @Throws(Exception::class)
        fun waitForCondition(instruction: Instruction, timeoutLimit: Int, watchInterval: Int) {
            var status = CONDITION_NOT_MET
            var elapsedTime = 0
            do {
                if (instruction.checkCondition()) {
                    status = CONDITION_MET
                } else {
                    elapsedTime += watchInterval
                    Thread.sleep(watchInterval.toLong())
                }
                if (elapsedTime >= timeoutLimit) {
                    status = TIMEOUT
                    break
                }
            } while (status != CONDITION_MET)
            if (status == TIMEOUT) throw Exception(instruction.description + " - took more than " + timeoutLimit / 1000 + " seconds. Test stopped.")
        }

        fun setWatchInterval(watchInterval: Int) {
            instance!!.watchInterval = watchInterval
        }

        fun setTimeoutLimit(ms: Int) {
            instance!!.timeoutLimit = ms
        }
    }
}


/**
 * Created by F1sherKK on 16/12/15.
 */
abstract class Instruction {
    abstract val id: Int
    var dataContainer = Bundle()
        private set

    fun setData(dataContainer: Bundle) {
        this.dataContainer = dataContainer
    }

    abstract val description: String

    abstract fun checkCondition(): Boolean
}



