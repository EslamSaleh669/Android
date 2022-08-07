package intalio.cts.mobile.android.ui.fragment.delegations

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.DelegationDataItem
import intalio.cts.mobile.android.data.network.response.DictionaryDataItem
import intalio.cts.mobile.android.ui.adapter.DelegationsAdapter
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.fragment_delegations.*
import kotlinx.android.synthetic.main.fragment_delegations.noDataFounded
import kotlinx.android.synthetic.main.toolbar_layout.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class DelegationsFragment : Fragment(),DelegationsAdapter.OnDeleteDelegationClicked{
    private lateinit var translator:  ArrayList<DictionaryDataItem>

    @Inject
    @field:Named("delegations")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: DelegationsViewModel by lazy {
            ViewModelProvider(this, viewModelFactory).get(DelegationsViewModel::class.java)
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
        return inflater.inflate(R.layout.fragment_delegations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog = requireContext().launchLoadingDialog()
        viewModel.Delegations = ReplaySubject.create()

        centered_txt.text = requireActivity().getText(R.string.delegation)
     //   setupUI()
        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }

        delegate.setOnClickListener {
           requireActivity().supportFragmentManager.commit {
               replace(R.id.fragmentContainer, AddDelegationFragment())
               addToBackStack("")
           }
        }

        getDelegations()

    }


    private fun setupUI (){
  //      translator = viewModel.readDictionary()!!.data!!


        when {
            viewModel.readLanguage() == "en" -> {
                centered_txt.text = translator.find { it.keyword == "Delegation" }!!.en
                delegationtxt.text = translator.find { it.keyword == "Delegation" }!!.en

            }
            viewModel.readLanguage() == "ar" -> {
                centered_txt.text = translator.find { it.keyword == "Delegation" }!!.ar
                delegationtxt.text = translator.find { it.keyword == "Delegation" }!!.ar

            }
            viewModel.readLanguage() == "fr" -> {
                centered_txt.text = translator.find { it.keyword == "Delegation" }!!.fr
                delegationtxt.text = translator.find { it.keyword == "Delegation" }!!.fr

            }
        }

    }
    private fun getDelegations(){
        val categoriesArray = viewModel.readCategoriesData()
        delegaterecycler.adapter =
            DelegationsAdapter(arrayListOf(), requireActivity(),this,categoriesArray)
        delegaterecycler.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)



        autoDispose.add(viewModel.Delegations.observeOn(AndroidSchedulers.mainThread()).subscribe({
            dialog!!.dismiss()
            val lastPosition: Int =
                (delegaterecycler.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()

            if (it!!.isEmpty() && viewModel.limit == 0) {
                noDataFounded.visibility = View.VISIBLE
                delegaterecycler.visibility = View.GONE

            } else {
                if (it.isNotEmpty()) {
                    Timber.d("Data Loaded")
                    (delegaterecycler.adapter as DelegationsAdapter).addDelegations(it)

                } else if (lastPosition > 10) {
                    requireContext().makeToast(getString(R.string.no_moredata))
                }
            }
        },{
            noDataFounded.visibility = View.VISIBLE
            delegaterecycler.visibility = View.GONE
            Timber.e(it)


        }))


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            delegaterecycler.setOnScrollChangeListener { view, i, i2, i3, i4 ->

                val lastPosition: Int =
                    (delegaterecycler.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                viewModel.checkForDelegationsLoading(lastPosition)

            }
        }

        viewModel.loadMoreDelegations()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.disposable?.dispose()
        viewModel.start = 0
        viewModel.limit = 0
    }

    override fun onDeleteClicked(delegationId: Int) {
        showDialog(delegationId)

    }

    private fun showDialog(delegationId: Int) {
        val ids = ArrayList<Int>()
        ids.add(delegationId)
        val width = (resources.displayMetrics.widthPixels * 0.99).toInt()
        val alertDialog = AlertDialog.Builder(activity)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(getString(R.string.delete_item_confirm))
            .setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener { dialog, i ->
                dialog.dismiss()



                viewModel.deleteDelegation(
                    ids
                ).enqueue(object : Callback<Void?> {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {

                        if (response.code() != 200) {

                            requireActivity().makeToast(getString(R.string.network_error))
                            activity!!.onBackPressed()

                        }else{
                            requireActivity().makeToast(getString(R.string.deleted))
                            (delegaterecycler.adapter as DelegationsAdapter).removeDelegation(delegationId)

                        }
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        dialog!!.dismiss()
                        requireActivity().makeToast(t.toString())
                    }

                }
                )
            })
            .setNegativeButton(getString(R.string.no), DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()


            }).show().window!!.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)


    }

    override fun onEditClicked(position: Int, model: DelegationDataItem) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.Delegation_Model, model)
        (activity as AppCompatActivity).supportFragmentManager.commit {
            replace(R.id.fragmentContainer,
                AddDelegationFragment().apply {
                    arguments = bundleOf(
                        Pair(Constants.Delegation_Model,model)
                     )


                }
            )
            addToBackStack("")

        }
    }
}