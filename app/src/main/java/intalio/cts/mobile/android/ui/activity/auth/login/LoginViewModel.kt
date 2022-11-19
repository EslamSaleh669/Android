package intalio.cts.mobile.android.ui.activity.auth.login

import androidx.lifecycle.ViewModel
import intalio.cts.mobile.android.data.model.UserCredentials
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.data.repo.UserRepo

import io.reactivex.Observable

class LoginViewModel(private val userRepo: UserRepo) : ViewModel() {

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

    fun userBasicInfo (token:String):Observable<UserInfoResponse> {
        return userRepo.userBasicInfo(token)
    }

    fun saveUserBasicInfo(userInfo: UserInfoResponse) {
        userRepo.saveUserBasicInfo(userInfo)
    }


    fun getDictionary (token:String,baseUrl : String):Observable<DictionaryResponse> {
        return userRepo.getDictionary(token,baseUrl)
    }

    fun saveDictionary(dictionary: DictionaryResponse) {
        userRepo.saveDictionary(dictionary)
    }


    fun readCurrentNode():String = userRepo.readCurrentNode()!!

    fun readLanguage() : String = userRepo.currentLang()
    fun readDictionary(): DictionaryResponse? = userRepo.readDictionary()
    fun readSavedDelegator(): DelegationRequestsResponseItem? = userRepo.readDelegatorData()
    fun readUserinfo (): UserFullDataResponseItem = userRepo.readFullUserData()!!


    fun logout() {
        userRepo.clearUserData()
    }

}