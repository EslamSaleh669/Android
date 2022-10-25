package intalio.cts.mobile.android.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.cts.mobile.android.R
import intalio.cts.mobile.android.data.network.response.NodeResponseItem
import intalio.cts.mobile.android.ui.fragment.advancedsearch.AdvancedSearchFragment
import intalio.cts.mobile.android.ui.fragment.correspondence.CorrespondenceFragment
import intalio.cts.mobile.android.ui.fragment.main.MainViewModel
import intalio.cts.mobile.android.util.AutoDispose
import intalio.cts.mobile.android.util.Constants
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber


class NodesAdapter(
    private val Nodes: List<NodeResponseItem>,
    val activity: Activity,
    val viewModel: MainViewModel,
    val autoDispose: AutoDispose,
    val delegationId: Int
) : RecyclerView.Adapter<NodesAdapter.AllNewsVHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllNewsVHolder =
        AllNewsVHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.categories_viewshape, parent, false)
        )


    override fun onBindViewHolder(holder: AllNewsVHolder, position: Int) {


        when (Nodes[position].inherit) {
            "Inbox" -> {
                holder.nodeImage.setImageResource(R.drawable.ic_inboxicon)

                autoDispose.add(
                    viewModel.inboxCount(Nodes[position].id!!,delegationId)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe({
                            holder.nodeCounter.text = it.total.toString()
                            holder.totalNodeCounter.text = it.total.toString()

                            holder.nodeCounter.visibility = View.VISIBLE
                            holder.totalNodeCounter.visibility = View.VISIBLE


                            holder.countProgress.visibility = View.GONE
                            holder.totalCountProgress.visibility = View.GONE
                        }, { Timber.e(it) })
                )

            }
            "Sent" -> {
                holder.nodeImage.setImageResource(R.drawable.ic_senticon)
                autoDispose.add(
                    viewModel.sentCount(Nodes[position].id!!,delegationId)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe({
                            holder.nodeCounter.text = it.total.toString()
                            holder.totalNodeCounter.text = it.total.toString()

                            holder.nodeCounter.visibility = View.VISIBLE
                            holder.totalNodeCounter.visibility = View.VISIBLE


                            holder.countProgress.visibility = View.GONE
                            holder.totalCountProgress.visibility = View.GONE
                        }, { Timber.e(it) })
                )
            }
            "Completed" -> {
                holder.nodeImage.setImageResource(R.drawable.ic_completedicon)
                autoDispose.add(
                    viewModel.completedCount(Nodes[position].id!!,delegationId)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe({
                            holder.nodeCounter.text = it.total.toString()
                            holder.totalNodeCounter.text = it.total.toString()

                            holder.nodeCounter.visibility = View.VISIBLE
                            holder.totalNodeCounter.visibility = View.VISIBLE


                            holder.countProgress.visibility = View.GONE
                            holder.totalCountProgress.visibility = View.GONE
                        }, { Timber.e(it) })
                )
            }

            "Closed" -> {
                holder.nodeImage.setImageResource(R.drawable.ic_completedicon)
                autoDispose.add(
                    viewModel.closedCount(Nodes[position].id!!,delegationId)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe({
                            holder.nodeCounter.text = it.total.toString()
                            holder.totalNodeCounter.text = it.total.toString()

                            holder.nodeCounter.visibility = View.VISIBLE
                            holder.totalNodeCounter.visibility = View.VISIBLE


                            holder.countProgress.visibility = View.GONE
                            holder.totalCountProgress.visibility = View.GONE
                        }, { Timber.e(it) })
                )
            }
            "MyRequests" -> {
                holder.nodeImage.setImageResource(R.drawable.ic_requestsicon)
                autoDispose.add(
                    viewModel.requestedCount(Nodes[position].id!!,delegationId)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe({
                            holder.nodeCounter.text = it.total.toString()
                            holder.totalNodeCounter.text = it.total.toString()

                            holder.nodeCounter.visibility = View.VISIBLE
                            holder.totalNodeCounter.visibility = View.VISIBLE


                            holder.countProgress.visibility = View.GONE
                            holder.totalCountProgress.visibility = View.GONE
                        }, { Timber.e(it) })
                )
            }
            "BAM" -> {
                holder.nodeImage.setImageResource(R.drawable.ic_bamicon)
                holder.counterFrame.visibility = View.INVISIBLE
                holder.totalCounterFrame.visibility = View.INVISIBLE
            }
            "Search" -> {
                holder.nodeImage.setImageResource(R.drawable.ic_advancedsearch)
                holder.counterFrame.visibility = View.INVISIBLE
                holder.totalCounterFrame.visibility = View.INVISIBLE
            }
        }


        holder.nodeTitle.text = Nodes[position].name
        holder.nodeLin.setOnClickListener {

            if (Nodes[position].id != 96 && Nodes[position].id != 97) {
                (activity as AppCompatActivity).supportFragmentManager.commit {
                    replace(R.id.fragmentContainer,
                        CorrespondenceFragment().apply {
                            arguments = bundleOf(
                                Pair(Constants.NODE_INHERIT, Nodes[position].inherit),
                            )
                        }
                    )
                    addToBackStack("")

                }

            } else if (Nodes[position].id == 97) {

                (activity as AppCompatActivity).supportFragmentManager.commit {
                    replace(R.id.fragmentContainer,
                        AdvancedSearchFragment().apply {
                            arguments = bundleOf(
                                Pair(Constants.SEARCH_TYPE, 1)
                            )

                        }
                    )
                    addToBackStack("")

                }

            }
        }
    }

    override fun getItemCount() = Nodes.size

    class AllNewsVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nodeTitle: TextView = itemView.findViewById(R.id.nodetitle)
        val nodeLin: RelativeLayout = itemView.findViewById(R.id.nodelin)
        val nodeCounter: TextView = itemView.findViewById(R.id.nodeCount)
        val nodeImage: ImageView = itemView.findViewById(R.id.nodeImage)
        val countProgress: ProgressBar = itemView.findViewById(R.id.countProgress)
        val counterFrame: FrameLayout = itemView.findViewById(R.id.counterFrame)


        val totalNodeCounter: TextView = itemView.findViewById(R.id.nodetotalCount)
        val totalCountProgress: ProgressBar = itemView.findViewById(R.id.totalcountProgress)
        val totalCounterFrame: FrameLayout = itemView.findViewById(R.id.totalcounterFrame)

    }

}