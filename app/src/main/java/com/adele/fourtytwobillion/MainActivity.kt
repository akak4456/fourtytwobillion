package com.adele.fourtytwobillion

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.room.Room
import com.adele.fourtytwobillion.databinding.ActivityMainBinding
import com.adele.fourtytwobillion.model.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "game-database"
        ).build()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        CoroutineScope(Dispatchers.IO).launch {
            val scores = db.scoreDao().getScores()
            if(scores.size == 1) {
                runOnUiThread {
                    binding.tvHighscore.text = String.format(getString(R.string.main_high_score), Util.getDecimalFormattedString(scores[0].highScore.toString()))
                }
            }
        }
        binding.tvNewGame.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.gameDao().deleteGame(db.gameDao().getGames())
            }
            startActivity(Intent(this, GameActivity::class.java).apply {
                putExtra("continue", false)
            })
        }
        binding.tvContinueGame.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java).apply {
                putExtra("continue", true)
            })
        }
    }
}