package com.adele.fourtytwobillion

import android.content.Intent
import android.graphics.Color
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.room.Room
import com.adele.fourtytwobillion.databinding.ActivityGameBinding
import com.adele.fourtytwobillion.model.AppDatabase
import com.adele.fourtytwobillion.model.Game
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private val gameViewModel: GameViewModel by viewModels()
    private val divideNumber = 5L
    private val possibleCnt = 3
    private val AD_UNIT_ID = "ca-app-pub-4500295629086135/3384331997"

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "game-database"
        ).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game)
        binding.lifecycleOwner = this
        binding.tvGameGuide2.text = String.format(getString(R.string.game_guide_2), divideNumber)
        if(intent.getBooleanExtra("continue", false)) {
            CoroutineScope(Dispatchers.IO).launch {
                val games = db.gameDao().getGames()
                if(games.size == 1) {
                    runOnUiThread {
                        gameViewModel.score.value = games[0].curScore
                        gameViewModel.board.value = games[0].board
                    }
                }
            }
        }
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
            binding.tvGameScore.text = String.format(getString(R.string.game_score), Util.getDecimalFormattedString(it.toString()))
        }

        // Create a new ad view.
        val adView = AdView(this)
        adView.adUnitId = AD_UNIT_ID
        adView.setAdSize(getAdSize())

// Replace ad container with new ad view.
        binding.adViewContainer.removeAllViews()
        binding.adViewContainer.addView(adView)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun getAdSize(): AdSize {
        val displayMetrics = resources.displayMetrics
        var adWidthPixels = displayMetrics.widthPixels

        if (VERSION.SDK_INT >= VERSION_CODES.R) {
            val windowMetrics = this.windowManager.currentWindowMetrics
            adWidthPixels = windowMetrics.bounds.width()
        }

        val density = displayMetrics.density
        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
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
                if(possiblePositions.size >= possibleCnt) {
                    val randomPositions = possiblePositions.shuffled().take(possibleCnt)
                    var newNumSum = 0L
                    randomPositions.forEach { randomPos ->
                        val nRandomRow = rowIdx + randomPos.drow
                        val nRandomCol = colIdx + randomPos.dcol
                        newNumSum += (it[rowIdx][colIdx] / divideNumber)
                        it[nRandomRow][nRandomCol] = it[nRandomRow][nRandomCol] + (it[rowIdx][colIdx] / divideNumber)
                    }
                    gameViewModel.score.value = (gameViewModel.score.value
                            ?: 0L) + (it[rowIdx][colIdx] - newNumSum)
                    it[rowIdx][colIdx] = 0
                    gameViewModel.board.value = it
                } else {
                    binding.tvWarning.text = String.format(getString(R.string.game_at_least_block), possibleCnt)
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            db.gameDao().deleteGame(db.gameDao().getGames())
            db.gameDao().insertGames(Game(curScore = gameViewModel.score.value ?: 0L, board = gameViewModel.board.value ?: arrayOf()))
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
                        if(possiblePositions.size >= possibleCnt) return false
                    }
                }
            }
        }
        return true
    }
}