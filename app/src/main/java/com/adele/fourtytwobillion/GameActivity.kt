package com.adele.fourtytwobillion

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.adele.fourtytwobillion.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private val gameViewModel: GameViewModel by viewModels()
    private val divideNumber = 5L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game)
        binding.lifecycleOwner = this
        binding.tvGameGuide2.text = String.format(getString(R.string.game_guide_2), divideNumber)
        gameViewModel.board.observe(this) {
            val rows = arrayOf(
                binding.llMainContentRow1,
                binding.llMainContentRow2,
                binding.llMainContentRow3,
                binding.llMainContentRow4,
                binding.llMainContentRow5
            )
            it.forEachIndexed { rowIdx, values ->
                with(rows[rowIdx]) {
                    post {
                        removeAllViews()
                        val parentWidth = width
                        values.forEachIndexed { colIdx ,value ->
                            val textView = TextView(this@GameActivity).apply {
                                text = value.toString()
                                gravity = Gravity.CENTER
                                if(value == 0L) {
                                    setBackgroundResource(R.drawable.bg_no_exist_block)
                                    setTextColor(Color.BLACK)
                                } else if(value < divideNumber) {
                                    setBackgroundResource(R.drawable.bg_not_able_block)
                                    setTextColor(Color.WHITE)
                                } else {
                                    setBackgroundResource(R.drawable.bg_exist_block)
                                    setTextColor(Color.WHITE)
                                }
                                layoutParams = LinearLayout.LayoutParams(
                                    (parentWidth * 0.2).toInt(), // 부모의 20% 너비
                                    (parentWidth * 0.2).toInt()  // 높이도 동일하게 설정
                                )
                                setOnClickListener {
                                    clickBlock(rowIdx, colIdx)
                                }
                            }
                            addView(textView)
                        }
                    }
                }
            }
        }

        gameViewModel.score.observe(this) {
            binding.tvGameScore.text = String.format(getString(R.string.game_score), it)
        }
    }

    private fun clickBlock(rowIdx: Int, colIdx: Int) {
        binding.tvWarning.text = ""
        gameViewModel.board.value?.let {
            if(it[rowIdx][colIdx] < divideNumber) {
                binding.tvWarning.text = getString(R.string.game_guide)
            } else {
                val possiblePositions: MutableList<PossiblePosition> = mutableListOf()
                PossiblePosition.entries.forEach { pos ->
                    val nrow = rowIdx + pos.drow
                    val ncol = colIdx + pos.dcol
                    if(nrow in 0..4 && ncol in 0..4 && (it[nrow][ncol] == 0L || it[nrow][ncol] >= divideNumber)) {
                        possiblePositions.add(pos)
                    }
                }
                if(possiblePositions.size >= 2) {
                    val randomPositions = possiblePositions.shuffled().take(2)
                    gameViewModel.score.value = (gameViewModel.score.value ?: 0L) + (it[rowIdx][colIdx]) % divideNumber
                    randomPositions.forEach { randomPos ->
                        val nRandomRow = rowIdx + randomPos.drow
                        val nRandomCol = colIdx + randomPos.dcol
                        it[nRandomRow][nRandomCol] = it[nRandomRow][nRandomCol] + (it[rowIdx][colIdx] / divideNumber)
                    }
                    it[rowIdx][colIdx] = 0
                    gameViewModel.board.value = it
                } else {
                    binding.tvWarning.text = getString(R.string.game_at_least_block)
                }
            }
        }
        if(testGameOver()) {
            startActivity(Intent(this, GameOverActivity::class.java).apply {
                putExtra("score", gameViewModel.score.value)
            })
            finish()
        }
    }

    private fun testGameOver(): Boolean {
        for(rowIdx in 0..4) {
            for(colIdx in 0..4) {
                gameViewModel.board.value?.let {
                    if(it[rowIdx][colIdx] >= divideNumber) {
                        val possiblePositions: MutableList<PossiblePosition> = mutableListOf()
                        PossiblePosition.entries.forEach { pos ->
                            val nrow = rowIdx + pos.drow
                            val ncol = colIdx + pos.dcol
                            if(nrow in 0..4 && ncol in 0..4 && (it[nrow][ncol] == 0L || it[nrow][ncol] >= divideNumber)) {
                                possiblePositions.add(pos)
                            }
                        }
                        if(possiblePositions.size >= 2) return false
                    }
                }
            }
        }
        return true
    }
}