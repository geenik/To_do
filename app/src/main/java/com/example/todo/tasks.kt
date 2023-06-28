package com.example.todo
import android.app.Fragment
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.example.todo.databinding.ActivityTasksBinding
import com.example.todo.model.Task
import com.google.android.material.tabs.TabLayout


class tasks : AppCompatActivity() {
    lateinit var bind:ActivityTasksBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO;
        bind=ActivityTasksBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(bind.root)
        bind.tabLayout.addTab(bind.tabLayout.newTab().setText("Pending"))
        bind.tabLayout.addTab(bind.tabLayout.newTab().setText("Completed"))
        bind.backbtn.setOnClickListener {
            finish()
        }
        bind.taskscount.text= "You have total ${MainActivity.count} tasks"
        val pending=pending()
        val completed=completed()

        replaceFragment(pending)
        bind.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> replaceFragment(pending)
                    1 -> replaceFragment(completed)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // Do nothing
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Do nothing
            }
        })
    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.framelayout, fragment)
        fragmentTransaction?.commit()
    }
}