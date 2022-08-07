package intalio.cts.mobile.android.ui.fragment.visualtracking

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cts.mobile.android.R
import intalio.cts.mobile.android.ui.adapter.TransferHistory_Adapter
import intalio.cts.mobile.android.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.allnotes_fragment.*
import kotlinx.android.synthetic.main.allnotes_fragment.noDataFounded
import kotlinx.android.synthetic.main.fragment_mytransfers.*
import kotlinx.android.synthetic.main.fragment_transferhistory.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class VisualTrackingFragment : Fragment() {
    private var DocumentId: Int = 0


    @Inject
    @field:Named("visualtracking")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: VisualTrackingViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(VisualTrackingViewModel::class.java)
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
        dialog = requireContext().launchLoadingDialog()

        return inflater.inflate(R.layout.fragment_visualtracking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        centered_txt.text = requireActivity().getText(R.string.visual_tracking)

        back_icon.setOnClickListener {
            activity?.onBackPressed()
        }



        requireArguments().getInt(Constants.DOCUMENT_ID).let {
            DocumentId = it
            getVisualTracking(DocumentId)
        }


    }


    private fun getVisualTracking(TransferId: Int) {

        autoDispose.add(viewModel.getVisualTracking(TransferId)
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
            {
                dialog!!.dismiss()


            }, {
                dialog!!.dismiss()
                Timber.e(it)


            })
        )


    }


}