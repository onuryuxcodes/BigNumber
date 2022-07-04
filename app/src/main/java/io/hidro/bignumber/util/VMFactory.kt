package io.hidro.bignumber.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.hidro.bignumber.vm.BigNumberGameVM

class VMFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BigNumberGameVM() as T
    }
}