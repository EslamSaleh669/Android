package intalio.cts.mobile.android.ui.fragment.main

import android.view.textclassifier.TextLanguage
import androidx.lifecycle.ViewModel
import intalio.cts.mobile.android.data.network.response.*
import intalio.cts.mobile.android.data.repo.AdminRepo
import intalio.cts.mobile.android.data.repo.UserRepo
import io.reactivex.Observable

class MainViewModel(private val userRepo: UserRepo, private val adminRepo: AdminRepo) : ViewModel() {


     //fun userData(): UserData? = userRepo.readUserData()


    fun readLanguage (): String = userRepo.currentLang()
    fun readDictionary (): DictionaryResponse = userRepo.readDictionary()!!

    fun nodesData(): Observable<List<NodeResponseItem>> = adminRepo.getNodesData()

   ////////
    fun inboxCount(nodeId:Int): Observable<InboxCountResponse> = adminRepo.getInboxCount(nodeId)
    fun sentCount(nodeId:Int): Observable<InboxCountResponse> = adminRepo.getSentCount(nodeId)
    fun completedCount(nodeId:Int): Observable<InboxCountResponse> = adminRepo.getCompletedCount(nodeId)
    fun requestedCount(nodeId:Int): Observable<InboxCountResponse> = adminRepo.getRequestedCount(nodeId)


    ///////
    fun categoriesData(): Observable<List<CategoryResponseItem>> = adminRepo.getCategoriesData()

    fun saveCategoriesData(categories: List<CategoryResponseItem>) {
        userRepo.saveCategoriesData(categories)
    }



    //////

    fun usersStructureData(ids:Array<Int>): Observable<ArrayList<UsersStructureItem>> = adminRepo.getStructureData(ids)

    fun saveUsersStructureData(users: ArrayList<UsersStructureItem>) {
        userRepo.saveUsersStructureData(users)
    }


    fun getAllStructures(): Observable<AllStructuresResponse> = adminRepo.getAllStructures("")

    fun saveAllStructures(structure: AllStructuresResponse) {
        userRepo.saveAllStructureData(structure)
    }






    //////
    fun fullUserData():Observable<UserFullDataResponseItem> = adminRepo.getUserFullData()

    fun saveFullUserData(userFullData: UserFullDataResponseItem) {
        userRepo.saveFullUserData(userFullData)
    }


    fun getStatuses():Observable<ArrayList<StatusesResponseItem>> = adminRepo.getStatuses()
    fun saveStatuses(statuses: ArrayList<StatusesResponseItem>) {
        userRepo.saveStatuses(statuses)
    }

    fun getPurposes():Observable<ArrayList<PurposesResponseItem>> = adminRepo.getPurposes()
    fun savePurposes(purposes: ArrayList<PurposesResponseItem>) {
        userRepo.savePurposes(purposes)
    }


    fun getPriorities():Observable<ArrayList<PrioritiesResponseItem>> = adminRepo.getPriorities()
    fun savePriorities(priorities: ArrayList<PrioritiesResponseItem>) {
        userRepo.savePriorities(priorities)
    }


    fun getPrivacies():Observable<ArrayList<PrivaciesResponseItem>> = adminRepo.getPrivacies()
    fun savePrivacies(privacies: ArrayList<PrivaciesResponseItem>) {
        userRepo.savePrivacies(privacies)
    }

    fun getImportances():Observable<ArrayList<ImportancesResponseItem>> = adminRepo.getImportances()
    fun saveImportnaces(importances: ArrayList<ImportancesResponseItem>) {
        userRepo.saveImportnaces(importances)
    }

    fun getTypes():Observable<ArrayList<TypesResponseItem>> = adminRepo.getTypes()
    fun saveTypes(types: ArrayList<TypesResponseItem>) {
        userRepo.saveTypes(types)
    }


    fun getFullCategories():Observable<ArrayList<FullCategoriesResponseItem>> = adminRepo.getFullCategories()
    fun saveFullCategories(importances: ArrayList<FullCategoriesResponseItem>) {
        userRepo.saveFullCategories(importances)
    }



    fun getFullStructures(language: Int):Observable<FullStructuresResponse> = adminRepo.getFullStructures(language)
    fun saveFullStructures(structures: ArrayList<FullStructuresResponseItem>) {
        userRepo.saveFullStructures(structures)
    }


    fun getSettings():Observable<ArrayList<ParamSettingsResponseItem>> = adminRepo.getSettings()
    fun saveSettings(settings: ArrayList<ParamSettingsResponseItem>) {
        userRepo.saveSettings(settings)
    }



    fun getUserBasicInfo () = userRepo.readUserBasicInfo()


}