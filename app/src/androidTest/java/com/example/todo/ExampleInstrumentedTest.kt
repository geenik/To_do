package com.example.todo

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todo.AddTask
import com.example.todo.MainActivity
import com.example.todo.NetworkIdlingResource
import com.example.todo.tasks
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    private lateinit var idlingResource: IdlingResource

    @Before
    fun setUp() {
        Intents.init()
        idlingResource = NetworkIdlingResource.getIdlingResource()
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    fun tearDown() {
        // Release Espresso Intents
        Intents.release()
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun testFabButtonClick() {
        // Launch the MainActivity
        ActivityScenario.launch(MainActivity::class.java)

        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click())

        // Verify that the AddTask activity is launched
        Intents.intended(IntentMatchers.hasComponent(AddTask::class.java.name))

        // Enter text and save
        val title = "Test Title"
        val description = "Test Description"
        Espresso.onView(ViewMatchers.withId(R.id.taskheading)).perform(ViewActions.typeText(title))
        Espresso.onView(ViewMatchers.withId(R.id.taskdescription)).perform(ViewActions.typeText(description))
        Espresso.onView(ViewMatchers.withId(R.id.savebtn))
            .perform(ViewActions.pressBack(), ViewActions.click())

        // Wait for the network call to complete using the IdlingResource
        IdlingRegistry.getInstance().register(idlingResource)

        // Verify that the tasks activity is launched
        Intents.intended(IntentMatchers.hasComponent(tasks::class.java.name))
    }
}
