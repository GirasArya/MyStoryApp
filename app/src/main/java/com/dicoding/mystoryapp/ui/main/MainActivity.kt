package com.dicoding.mystoryapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.databinding.ActivityMainBinding
import com.dicoding.mystoryapp.factory.ViewModelFactory
import com.dicoding.mystoryapp.ui.addstory.AddStoryActivity
import com.dicoding.mystoryapp.ui.map.MapActivity
import com.dicoding.mystoryapp.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: MainListAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MyStoryApp)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBarLogin.visibility = View.GONE

        mainViewModel = obtainViewModel(this)

        // Observe the session
        mainViewModel.getSession().observe(this@MainActivity) { user ->
            binding.progressBarLogin.visibility = View.VISIBLE
            user?.let {
                if (it.isLoggedIn) {
                    token = it.token
                    Log.d("MainActivity", "Token: $token")
                    binding.progressBarLogin.visibility = View.GONE
                    mainViewModel.getListStories
                } else {
                    val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } ?: run {
                val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        // Observe the list of stories
        mainViewModel.getListStories.observe(this@MainActivity) { stories ->
            stories?.let {
                adapter.submitData(lifecycle, it)
            }
        }


        setupRecyclerView()
        binding.fabAddstory.setOnClickListener {
            Intent(this@MainActivity, AddStoryActivity::class.java).also { intent ->
                startActivity(intent)
            }
        }

        binding.fabMapStories.setOnClickListener{
            startActivity(Intent(this, MapActivity::class.java))
        }
    }


    // Back button handler to delete previous activity
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    // RecyclerView setup
    private fun setupRecyclerView() {
        adapter = MainListAdapter()
        binding.rvStory.layoutManager = LinearLayoutManager(this)
        binding.rvStory.adapter = adapter
    }

    // ViewModel setup
    private fun obtainViewModel(activity: AppCompatActivity): MainViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(MainViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_logout ->{
                mainViewModel.clearSession()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}