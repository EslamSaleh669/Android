package intalio.cts.mobile.android.data.repo

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import intalio.cts.mobile.android.data.model.ScanResponse
import intalio.cts.mobile.android.data.model.UserCredentials
import intalio.cts.mobile.android.data.network.ApiClient
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.util.Constants
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

import javax.inject.Inject

class UserRepo @Inject constructor(
    private val apiClient: ApiClient,
    private val shared: SharedPreferences

) {


    fun userLogin(
        clientId: String, GrantType: String,
        email: String, password: String
    ): Observable<TokenResponse> {
        return apiClient.userLogin(
            "${Constants.BASE_URL2}/connect/token",
            clientId,
            GrantType,
            email,
            password,
            "IdentityServerApi openid profile"
        ).subscribeOn(Schedulers.io())
    }

    fun userBasicInfo(token: String): Observable<UserInfoResponse> {
        return apiClient.getUserBasicInfo(
            "Bearer $token",
            "${Constants.BASE_URL2}/connect/userinfo"
        ).subscribeOn(Schedulers.io())
    }


    fun getDictionary(token: String,baseUrl : String): Observable<DictionaryResponse> {
        return apiClient.getDictionary(
            "Bearer $token",
            "${Constants.BASE_URL}/TranslatorDictionary/List",
            1,
            0,
            1000
        ).subscribeOn(Schedulers.io())
    }

    fun saveTokenData(tokenData: TokenResponse) {
        shared.edit().putString(Constants.USER_TOKEN_KEY, Gson().toJson(tokenData)).apply()
    }


    fun readTokenData(): TokenResponse? {
        return try {
            Gson().fromJson(
                shared.getString(Constants.USER_TOKEN_KEY, ""),
                TokenResponse::class.java
            )
        } catch (e: Exception) {
            null
        }
    }


    fun saveCredentials(tokenData: UserCredentials) {
        shared.edit().putString(Constants.USER_CREDENTIALS, Gson().toJson(tokenData)).apply()
    }


    fun readCredentials(): UserCredentials? {
        return try {
            Gson().fromJson(
                shared.getString(Constants.USER_CREDENTIALS, ""),
                UserCredentials::class.java
            )
        } catch (e: Exception) {
            null
        }
    }


    fun saveDictionary(dictionary: DictionaryResponse) {
        shared.edit().putString(Constants.DICTIONARY, Gson().toJson(dictionary)).apply()
    }


    fun readDictionary(): DictionaryResponse? {
        return try {
            Gson().fromJson(
                shared.getString(Constants.DICTIONARY, ""),
                DictionaryResponse::class.java
            )
        } catch (e: Exception) {
            null
        }
    }


    fun readUserBasicInfo(): UserInfoResponse? {
        return try {
            Gson().fromJson(
                shared.getString(Constants.USER_BASIC_INFO, ""),
                UserInfoResponse::class.java
            )
        } catch (e: Exception) {
            null
        }
    }


    fun saveUserBasicInfo(userInfo: UserInfoResponse) {
        shared.edit().putString(Constants.USER_BASIC_INFO, Gson().toJson(userInfo)).apply()
    }


    fun currentLang(): String {
        return shared.getString(Constants.LANG_KEY, "en") ?: "en"
        //ar-fr-en
    }


    fun saveCurrentNode(inherit: String) {
        shared.edit().putString(Constants.NODE_INHERIT, inherit).apply()
    }

    fun readCurrentNode(): String? {
        return try {
            shared.getString(Constants.NODE_INHERIT, "")
        } catch (e: Exception) {
            null
        }
    }

    fun savePath(path: String) {
        shared.edit().putString(Constants.PATH, path).apply()
    }

    fun readPath(): String? {
        return try {
            shared.getString(Constants.PATH, "")
        } catch (e: Exception) {
            null
        }
    }

    fun saveCategoriesData(categories: List<CategoryResponseItem>) {
        shared.edit().putString(Constants.CATEGORY_KEY, Gson().toJson(categories)).apply()
    }


    fun readCategoriesData(): ArrayList<CategoryResponseItem>? {
        return try {

            val gson = Gson()
            val json = shared.getString(Constants.CATEGORY_KEY, "")
            val type = object :
                TypeToken<ArrayList<CategoryResponseItem>>() {}.type//converting the json to list
            return gson.fromJson(json, type)
        } catch (e: Exception) {

            null
        }
    }


    fun saveUsersStructureData(users: ArrayList<UsersStructureItem>) {
        shared.edit().putString(Constants.USER_STRUCTURE_KEY, Gson().toJson(users)).apply()
    }

    fun readUsersStructureData(): ArrayList<UsersStructureItem>? {
        return try {

            val gson = Gson()
            val json = shared.getString(Constants.USER_STRUCTURE_KEY, "")
            val type = object :
                TypeToken<ArrayList<UsersStructureItem>>() {}.type//converting the json to list
            return gson.fromJson(json, type)
        } catch (e: Exception) {

            null
        }
    }


    fun saveAllStructureData(users: AllStructuresResponse) {
        shared.edit().putString(Constants.ALL_STRUCTURES, Gson().toJson(users)).apply()
    }

    fun readAllStructureData(): AllStructuresResponse? {
        return try {
            Gson().fromJson(
                shared.getString(Constants.ALL_STRUCTURES, ""),
                AllStructuresResponse::class.java
            )
        } catch (e: Exception) {
            null
        }
    }


    fun saveFullUserData(userFullData: UserFullDataResponseItem) {
        shared.edit().putString(Constants.USER_FULL_DATA, Gson().toJson(userFullData)).apply()
    }

    fun readFullUserData(): UserFullDataResponseItem? {
        return try {
            Gson().fromJson(
                shared.getString(Constants.USER_FULL_DATA, ""),
                UserFullDataResponseItem::class.java
            )
        } catch (e: Exception) {
            null
        }
    }



    fun saveNodes(users: ArrayList<NodeResponseItem>) {
        shared.edit().putString(Constants.NODES_DATA, Gson().toJson(users)).apply()
    }


    fun readNodes(): ArrayList<NodeResponseItem>? {
        return try {

            val gson = Gson()
            val json = shared.getString(Constants.NODES_DATA, "")
            val type = object :
                TypeToken<ArrayList<NodeResponseItem>>() {}.type//converting the json to list
            return gson.fromJson(json, type)
        } catch (e: Exception) {

            null
        }
    }



    fun saveStatuses(users: ArrayList<StatusesResponseItem>) {
        shared.edit().putString(Constants.STATUSES_DATA, Gson().toJson(users)).apply()
    }


    fun readStatuses(): ArrayList<StatusesResponseItem>? {
        return try {

            val gson = Gson()
            val json = shared.getString(Constants.STATUSES_DATA, "")
            val type = object :
                TypeToken<ArrayList<StatusesResponseItem>>() {}.type//converting the json to list
            return gson.fromJson(json, type)
        } catch (e: Exception) {

            null
        }
    }


    fun savePurposes(users: ArrayList<PurposesResponseItem>) {
        shared.edit().putString(Constants.PURPOSES_DATA, Gson().toJson(users)).apply()
    }


    fun readPurposes(): ArrayList<PurposesResponseItem>? {
        return try {

            val gson = Gson()
            val json = shared.getString(Constants.PURPOSES_DATA, "")
            val type = object :
                TypeToken<ArrayList<PurposesResponseItem>>() {}.type//converting the json to list
            return gson.fromJson(json, type)
        } catch (e: Exception) {

            null
        }
    }


    fun savePriorities(users: ArrayList<PrioritiesResponseItem>) {
        shared.edit().putString(Constants.PRIORITIES_DATA, Gson().toJson(users)).apply()
    }


    fun readPriorities(): ArrayList<PrioritiesResponseItem>? {
        return try {

            val gson = Gson()
            val json = shared.getString(Constants.PRIORITIES_DATA, "")
            val type = object :
                TypeToken<ArrayList<PrioritiesResponseItem>>() {}.type//converting the json to list
            return gson.fromJson(json, type)
        } catch (e: Exception) {

            null
        }
    }

    fun savePrivacies(users: ArrayList<PrivaciesResponseItem>) {
        shared.edit().putString(Constants.PRIVACIES_DATA, Gson().toJson(users)).apply()
    }


    fun readPrivacies(): ArrayList<PrivaciesResponseItem>? {
        return try {

            val gson = Gson()
            val json = shared.getString(Constants.PRIVACIES_DATA, "")
            val type = object :
                TypeToken<ArrayList<PrivaciesResponseItem>>() {}.type//converting the json to list
            return gson.fromJson(json, type)
        } catch (e: Exception) {

            null
        }
    }


    fun saveImportnaces(users: ArrayList<ImportancesResponseItem>) {
        shared.edit().putString(Constants.IMPORTANCES_DATA, Gson().toJson(users)).apply()
    }


    fun readImportnaces(): ArrayList<ImportancesResponseItem>? {
        return try {

            val gson = Gson()
            val json = shared.getString(Constants.IMPORTANCES_DATA, "")
            val type = object :
                TypeToken<ArrayList<ImportancesResponseItem>>() {}.type//converting the json to list
            return gson.fromJson(json, type)
        } catch (e: Exception) {

            null
        }
    }

    fun saveTypes(types: ArrayList<TypesResponseItem>) {
        shared.edit().putString(Constants.TYPES_DATA, Gson().toJson(types)).apply()
    }

    fun readTypes(): ArrayList<TypesResponseItem>? {
        return try {

            val gson = Gson()
            val json = shared.getString(Constants.TYPES_DATA, "")
            val type = object :
                TypeToken<ArrayList<TypesResponseItem>>() {}.type//converting the json to list
            return gson.fromJson(json, type)
        } catch (e: Exception) {

            null
        }
    }


    fun saveFullCategories(types: ArrayList<FullCategoriesResponseItem>) {
        shared.edit().putString(Constants.FULLCATEGORIES_DATA, Gson().toJson(types)).apply()
    }

    fun readFullCategories(): ArrayList<FullCategoriesResponseItem>? {
        return try {

            val gson = Gson()
            val json = shared.getString(Constants.FULLCATEGORIES_DATA, "")
            val type = object :
                TypeToken<ArrayList<FullCategoriesResponseItem>>() {}.type//converting the json to list
            return gson.fromJson(json, type)
        } catch (e: Exception) {

            null
        }
    }




    fun saveFullStructures(types: ArrayList<FullStructuresResponseItem>) {
        shared.edit().putString(Constants.FULLSTRUCTURES_DATA, Gson().toJson(types)).apply()
    }

    fun readFullStructures(): ArrayList<FullStructuresResponseItem>? {
        return try {

            val gson = Gson()
            val json = shared.getString(Constants.FULLSTRUCTURES_DATA, "")
            val type = object :
                TypeToken<ArrayList<FullStructuresResponseItem>>() {}.type//converting the json to list
            return gson.fromJson(json, type)
        } catch (e: Exception) {

            null
        }
    }


    fun saveSettings(categories: ArrayList<ParamSettingsResponseItem>) {
        shared.edit().putString(Constants.SETTINGS_KEY, Gson().toJson(categories)).apply()
    }

    fun readSettings(): ArrayList<ParamSettingsResponseItem>? {
        return try {

            val gson = Gson()
            val json = shared.getString(Constants.SETTINGS_KEY, "")
            val type = object :
                TypeToken<ArrayList<ParamSettingsResponseItem>>() {}.type//converting the json to list
            return gson.fromJson(json, type)
        } catch (e: Exception) {

            null
        }
    }

    fun saveDelegatorData(delegator: DelegationRequestsResponseItem) {
        shared.edit().putString(Constants.DELEGATOR_DATA, Gson().toJson(delegator)).apply()
    }

    fun readDelegatorData(): DelegationRequestsResponseItem? {
        return try {
            Gson().fromJson(
                shared.getString(Constants.DELEGATOR_DATA, ""),
                DelegationRequestsResponseItem::class.java
            )
        } catch (e: Exception) {
            null
        }
    }


    fun clearUserData() {
        shared.edit().clear().apply()
    }
}
