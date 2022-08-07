package intalio.cts.mobile.android.ui.fragment.profile

import androidx.lifecycle.ViewModel
import intalio.cts.mobile.android.data.repo.AdminRepo
import intalio.cts.mobile.android.data.repo.UserRepo

class ProfleViewModel(private val userRepo: UserRepo, private val adminRepo: AdminRepo) : ViewModel() {

    fun currentLanguage(): String = userRepo.currentLang()


}