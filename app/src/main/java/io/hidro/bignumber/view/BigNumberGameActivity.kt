package io.hidro.bignumber.view

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import io.hidro.bignumber.vm.BigNumberGameVM
import io.hidro.bignumber.R
import io.hidro.bignumber.databinding.GameBinding

class BigNumberGameActivity : BaseActivity() {

    private lateinit var binding: GameBinding

    private val viewModel: BigNumberGameVM by lazy {
        ViewModelProvider(this)[BigNumberGameVM::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.game)
        initializeGame()
        observeNumberSelection()
        observeIfTimeIsUp()
        startTheGame()
    }

    private fun observeNumberSelection() {
        binding.left.setOnClickListener {
            viewModel.numberOnTheLeftIsChosen()
        }
        binding.right.setOnClickListener {
            viewModel.numberOnTheRightIsChosen()
        }
    }

    private fun initializeGame() {
        viewModel.numberPair.observe(this) {
            binding.left.text = it.first.toString()
            binding.right.text = it.second.toString()
            startCountDownAnimation()
        }
    }

    private fun observeIfTimeIsUp() {
        viewModel.timeIsUp.observe(this) {
            if (it) {
                showGameEndedUI()
            }
        }
    }

    private fun startTheGame() {
        viewModel.generateNewNumbers()
    }

    private fun startCountDownAnimation() {

    }

    private fun showGameEndedUI() {

    }

    override fun onPause() {
        super.onPause()
    }
}