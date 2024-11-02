package com.adele.fourtytwobillion

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.room.Room
import com.adele.fourtytwobillion.databinding.ActivityGameOverBinding
import com.adele.fourtytwobillion.databinding.ActivityMainBinding
import com.adele.fourtytwobillion.model.AppDatabase
import com.adele.fourtytwobillion.model.Score
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameOverActivity: AppCompatActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "game-database"
        ).build()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding: ActivityGameOverBinding = DataBindingUtil.setContentView(this, R.layout.activity_game_over)
        binding.lifecycleOwner = this
        val curScore = intent.getLongExtra("score", 0L)
        binding.tvScore.text = String.format(getString(R.string.game_over_score), Util.getDecimalFormattedString(curScore.toString()))
        binding.tvRestartGame.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
            finish()
        }
        CoroutineScope(Dispatchers.IO).launch {
            val scores = db.scoreDao().getScores()
            if(scores.isEmpty() || (scores.size == 1 && curScore > scores[0].highScore)) {
                db.scoreDao().deleteScores(scores)
                db.scoreDao().insertScores(Score(highScore = curScore))
                runOnUiThread {
                    binding.tvHighScore.visibility = View.VISIBLE
                }
            }
        }
    }
}