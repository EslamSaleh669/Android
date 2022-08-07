package intalio.cts.mobile.android.ui.activity.splash

import androidx.lifecycle.ViewModel
import intalio.cts.mobile.android.data.model.UserCredentials
import intalio.cts.mobile.android.data.network.response.TokenResponse
import intalio.cts.mobile.android.data.repo.UserRepo
import io.reactivex.Observable


class SplashViewModel(private val userRepo: UserRepo) : ViewModel() {

    fun userData(): TokenResponse? = userRepo.readTokenData()
    fun readLanguage() : String = userRepo.currentLang()



    fun readUserCredentials():UserCredentials = userRepo.readCredentials()!!
    fun readTokenData(): TokenResponse? = userRepo.readTokenData()

    fun userLogin(clientId: String, GrantType: String,
                  email: String, password: String): Observable<TokenResponse> {
        return userRepo.userLogin(clientId,GrantType,email,password)
    }

    fun saveUserToken(tokenData: TokenResponse) {
        userRepo.saveTokenData(tokenData)
    }


    fun saveUserCredentials(credentials : UserCredentials) {
        userRepo.saveCredentials(credentials)
    }

}