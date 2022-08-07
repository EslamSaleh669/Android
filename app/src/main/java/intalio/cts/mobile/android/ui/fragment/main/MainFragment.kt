package intalio.cts.mobile.android.ui.fragment.main

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.CorrespondenceDataItem
import intalio.cts.mobile.android.data.network.response.DictionaryResponse
import intalio.cts.mobile.android.data.network.response.NodeResponseItem
import intalio.cts.mobile.android.data.network.response.UsersStructureItem
import intalio.cts.mobile.android.ui.adapter.NodesAdapter
import intalio.cts.mobile.android.util.AutoDispose
import intalio.cts.mobile.android.util.MyApplication
import intalio.cts.mobile.android.util.launchLoadingDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.drawer_icon
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class MainFragment : Fragment() {

    private var userId: Int = 0
    // private lateinit var translator: DictionaryResponse

    @Inject
    @field:Named("main")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
    }

    private val autoDispose: AutoDispose = AutoDispose()
    var dialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        autoDispose.bindTo(this.lifecycle)

        (activity?.application as MyApplication).appComponent?.inject(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog = requireContext().launchLoadingDialog()


        //   translator = viewModel.readDictionary()


        drawer_icon.setOnClickListener {
            requireActivity().drawer_layout.openDrawer(GravityCompat.START)
        }


        viewModel.getUserBasicInfo()!!.id.also { userId = it!!.toInt() }

        getFullUserData()
        getCategoriesData()
        getNodesData()
        getStatuses()
        getPurposes()
        getPriorities()
        getPrivacies()
        getImportances()
        getTypes()
        getAllStructureData()
        getSettings()
        getFullCategories()
        getFullStructures()

    }

    private fun getNodesData() {
        val bam = NodeResponseItem(name = getString(R.string.dashboard), id = 96)
        val advancedSearch = NodeResponseItem(name = getString(R.string.advanced_search), id = 97)

        autoDispose.add(viewModel.nodesData().observeOn(AndroidSchedulers.mainThread()).subscribe({

            val nodes = ArrayList<NodeResponseItem>()
            for (item in it) {
                if (item.id!! == 2 || item.id == 7 || item.id == 4 || item.id == 6) {
                    nodes.add(item)
                    Log.d("item", item.id.toString())
                }
            }

//            nodes.add(bam)
            //          nodes.add(advancedSearch)

            noderecycler.adapter =
                NodesAdapter(nodes, requireActivity(), viewModel, autoDispose)
            noderecycler.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

            dialog!!.dismiss()

        }, {
            Timber.e(it)

        }))
    }

    private fun getCategoriesData() {
        autoDispose.add(viewModel.categoriesData().observeOn(Schedulers.io()).subscribe({

            viewModel.saveCategoriesData(it)

        }, {
            Timber.e(it)

        }))
    }

    private fun getFullUserData() {
        autoDispose.add(
            viewModel.fullUserData().observeOn(AndroidSchedulers.mainThread()).subscribe({

                viewModel.saveFullUserData(it)
                requireActivity().findViewById<TextView>(R.id.clientname).text = it.fullName
                requireActivity().findViewById<TextView>(R.id.clientposition).text =
                    it.email.toString()


                requireActivity().findViewById<TextView>(R.id.txtmenu).text =
                    "${
                        it.firstName!![0].uppercaseChar().toString()
                    }${it.lastName!![0].uppercaseChar().toString()}"


                getUserStructureData(it.structureIds!!.toTypedArray())


            }, {
                Timber.e(it)

            })
        )
    }

    private fun getStatuses() {
        autoDispose.add(viewModel.getStatuses().observeOn(Schedulers.io()).subscribe({

            viewModel.saveStatuses(it)

        }, {
            Timber.e(it)

        }))
    }

    private fun getPurposes() {
        autoDispose.add(viewModel.getPurposes().observeOn(Schedulers.io()).subscribe({

            viewModel.savePurposes(it)

        }, {
            Timber.e(it)


        }))
    }

    private fun getPriorities() {
        autoDispose.add(viewModel.getPriorities().observeOn(Schedulers.io()).subscribe({

            viewModel.savePriorities(it)

        }, {
            Timber.e(it)

        }))
    }

    private fun getPrivacies() {
        autoDispose.add(viewModel.getPrivacies().observeOn(Schedulers.io()).subscribe({

            viewModel.savePrivacies(it)

        }, {
            Timber.e(it)

        }))
    }

    private fun getImportances() {
        autoDispose.add(viewModel.getImportances().observeOn(Schedulers.io()).subscribe({

            viewModel.saveImportnaces(it)

        }, {
            Timber.e(it)

        }))
    }


    private fun getTypes() {
        autoDispose.add(viewModel.getTypes().observeOn(Schedulers.io()).subscribe(
            {

                viewModel.saveTypes(it)


            }, {
                Timber.e(it)

            })
        )
    }


    private fun getFullCategories() {
        autoDispose.add(viewModel.getFullCategories().observeOn(Schedulers.io()).subscribe(
            {

                viewModel.saveFullCategories(it)


            }, {
                Timber.e(it)

            })
        )
    }

    private fun getFullStructures() {
        var languageCode = 0
        when {
            viewModel.readLanguage() == "en" -> {
                languageCode = 1
            }
            viewModel.readLanguage() == "fr" -> {
                languageCode = 2

            }
            viewModel.readLanguage() == "ar" -> {
                languageCode = 3

            }
        }
        autoDispose.add(viewModel.getFullStructures(languageCode).observeOn(Schedulers.io()).subscribe(
            {

                Log.d("aaxffds",it.toString())
                viewModel.saveFullStructures(it.items!!)


            }, {
                Timber.e(it)
                Log.d("aaxffds",it.toString())

            })
        )
    }


    private fun getUserStructureData(structureIds: Array<Int>) {

        autoDispose.add(
            viewModel.usersStructureData(structureIds).observeOn(Schedulers.io()).subscribe({


                val usersarray = ArrayList<UsersStructureItem>()

                for (item in it) {
                    if (userId != item.id) {
                        usersarray.add(item)
                    }
                }

                viewModel.saveUsersStructureData(usersarray)


            }, {
                Timber.e(it)
            })
        )
    }

    private fun getAllStructureData() {

        autoDispose.add(viewModel.getAllStructures().observeOn(Schedulers.io()).subscribe({


            viewModel.saveAllStructures(it)


        }, {
            Timber.e(it)
        }))
    }

    private fun getSettings() {

        autoDispose.add(viewModel.getSettings().observeOn(Schedulers.io()).subscribe({


            Log.d("avvv",it.toString())
            viewModel.saveSettings(it)


        }, {
            Log.d("avvv",it.toString())

            Timber.e(it)
        }))
    }
}