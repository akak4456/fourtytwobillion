package com.adele.fourtytwobillion

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.adele.fourtytwobillion.databinding.ActivityGameOverBinding
import com.adele.fourtytwobillion.databinding.ActivityMainBinding

class GameOverActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding: ActivityGameOverBinding = DataBindingUtil.setContentView(this, R.layout.activity_game_over)
        binding.lifecycleOwner = this
        binding.tvScore.text = String.format(getString(R.string.game_over_score), intent.getLongExtra("score", 0L))
        binding.tvRestartGame.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
            finish()
        }
    }
}