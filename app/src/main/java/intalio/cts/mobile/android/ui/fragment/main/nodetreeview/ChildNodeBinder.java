package intalio.cts.mobile.android.ui.fragment.main.nodetreeview;

import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.cts.mobile.android.R;

import javax.xml.parsers.FactoryConfigurationError;

import intalio.cts.mobile.android.data.network.response.InboxCountResponse;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewBinder;
import timber.log.Timber;

/**
 * Created by tlh on 2016/10/1 :)
 */

public class ChildNodeBinder extends TreeViewBinder<ChildNodeBinder.ChildViewHolder> {
    @Override
    public ChildViewHolder provideViewHolder(View itemView) {
        return new ChildViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void bindView(ChildViewHolder holder, int position, TreeNode node) {
        holder.setIsRecyclable(true);

        NodeChild fileNode = (NodeChild) node.getContent();


        LinearLayout.LayoutParams outsideParentLayout =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        outsideParentLayout.setMargins(0, 0, 0, 25);

        LinearLayout.LayoutParams insideParentLayout =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        insideParentLayout.setMargins(0, 5, 0, 5);




        holder.tvName.setText(fileNode.fileName);
        holder.nodeInherit.setText(fileNode.inherit);


        if (fileNode.location.equals("outsideParent")) {
            RelativeLayout.LayoutParams childIconLayout =
                    new RelativeLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            childIconLayout.setMarginStart(0);
            childIconLayout.setMarginEnd(20);
            childIconLayout.width = 110;
            childIconLayout.height = 100;
            childIconLayout.addRule(RelativeLayout.CENTER_VERTICAL);
            holder.cardView.setLayoutParams(outsideParentLayout);
            holder.childIcon.setLayoutParams(childIconLayout);

        } else if (fileNode.location.equals("insideParent")){
            RelativeLayout.LayoutParams childIconLayout =
                    new RelativeLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            childIconLayout.setMarginStart(fileNode.padding);
            childIconLayout.setMarginEnd(20);
            childIconLayout.width = 110;
            childIconLayout.height = 100;
            childIconLayout.addRule(RelativeLayout.CENTER_VERTICAL);
            holder.cardView.setLayoutParams(insideParentLayout);
            holder.childIcon.setLayoutParams(childIconLayout);
        }else {
            RelativeLayout.LayoutParams childIconLayout =
                    new RelativeLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            childIconLayout.setMarginStart(0);
            childIconLayout.setMarginEnd(20);
            childIconLayout.width = 110;
            childIconLayout.height = 100;
            childIconLayout.addRule(RelativeLayout.CENTER_VERTICAL);
            holder.cardView.setLayoutParams(outsideParentLayout);
            holder.childIcon.setLayoutParams(childIconLayout);

        }


        switch (fileNode.inherit) {
            case "Inbox":
                  holder.childIcon.setImageResource(R.drawable.ic_inboxicon);

                fileNode.autoDispose.add(
                        fileNode.viewModel.inboxCount(fileNode.id,0)
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<InboxCountResponse>() {
                            @Override
                            public void accept(InboxCountResponse inboxCountResponse) throws Exception {
                                holder.nodeCounter.setText(inboxCountResponse.getToday().toString());
                                holder.totalNodeCounter.setText(inboxCountResponse.getTotal().toString());

                                holder.nodeCounter.setVisibility(View.VISIBLE);
                                holder.totalNodeCounter.setVisibility(View.VISIBLE);


                                holder.countProgress.setVisibility(View.GONE);
                                holder.totalCountProgress.setVisibility(View.GONE);
                            }

                        }));
                break;
            case "Sent":
                  holder.childIcon.setImageResource(R.drawable.ic_senticon);

                fileNode.autoDispose.add(
                        fileNode.viewModel.sentCount(fileNode.id,0)
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<InboxCountResponse>() {
                            @Override
                            public void accept(InboxCountResponse inboxCountResponse) throws Exception {
                                holder.nodeCounter.setText(inboxCountResponse.getToday().toString());
                                holder.totalNodeCounter.setText(inboxCountResponse.getTotal().toString());

                                holder.nodeCounter.setVisibility(View.VISIBLE);
                                holder.totalNodeCounter.setVisibility(View.VISIBLE);


                                holder.countProgress.setVisibility(View.GONE);
                                holder.totalCountProgress.setVisibility(View.GONE);
                            }

                        }));
                break;
            case "Completed":
                  holder.childIcon.setImageResource(R.drawable.ic_completedicon);

                fileNode.autoDispose.add(
                        fileNode.viewModel.completedCount(fileNode.id,0)
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<InboxCountResponse>() {
                            @Override
                            public void accept(InboxCountResponse inboxCountResponse) throws Exception {
                                holder.nodeCounter.setText(inboxCountResponse.getToday().toString());
                                holder.totalNodeCounter.setText(inboxCountResponse.getTotal().toString());

                                holder.nodeCounter.setVisibility(View.VISIBLE);
                                holder.totalNodeCounter.setVisibility(View.VISIBLE);


                                holder.countProgress.setVisibility(View.GONE);
                                holder.totalCountProgress.setVisibility(View.GONE);
                            }

                        }));
                break;
            case "Closed":
               holder.childIcon.setImageResource(R.drawable.ic_completedicon);

                fileNode.autoDispose.add(
                        fileNode.viewModel.closedCount(fileNode.id,0)
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<InboxCountResponse>() {
                            @Override
                            public void accept(InboxCountResponse inboxCountResponse) throws Exception {
                                holder.nodeCounter.setText(inboxCountResponse.getToday().toString());
                                holder.totalNodeCounter.setText(inboxCountResponse.getTotal().toString());

                                holder.nodeCounter.setVisibility(View.VISIBLE);
                                holder.totalNodeCounter.setVisibility(View.VISIBLE);


                                holder.countProgress.setVisibility(View.GONE);
                                holder.totalCountProgress.setVisibility(View.GONE);
                            }

                        }));
                break;
            case "MyRequests":
                holder.childIcon.setImageResource(R.drawable.ic_requestsicon);

                fileNode.autoDispose.add(
                        fileNode.viewModel.requestedCount(fileNode.id,0)
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<InboxCountResponse>() {
                            @Override
                            public void accept(InboxCountResponse inboxCountResponse) throws Exception {
                                holder.nodeCounter.setText(inboxCountResponse.getToday().toString());
                                holder.totalNodeCounter.setText(inboxCountResponse.getTotal().toString());

                                holder.nodeCounter.setVisibility(View.VISIBLE);
                                holder.totalNodeCounter.setVisibility(View.VISIBLE);


                                holder.countProgress.setVisibility(View.GONE);
                                holder.totalCountProgress.setVisibility(View.GONE);
                            }

                        }));
                break;
        }

        // holder.notifyAll();
        synchronized(holder) {
            holder.notifyAll();
            holder.notify();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.child_dir;
    }

    public static class ChildViewHolder extends TreeViewBinder.ViewHolder {
        public TextView tvName;
        public TextView nodeInherit;
        public CardView cardView;
        public ImageView childIcon;
        public TextView nodeCounter, totalNodeCounter;
        public ProgressBar countProgress, totalCountProgress;

        public ChildViewHolder(View rootView) {
            super(rootView);
            this.tvName = (TextView) rootView.findViewById(R.id.nodetitle);
            this.nodeInherit = (TextView) rootView.findViewById(R.id.nodeinherit);
            this.cardView = (CardView) rootView.findViewById(R.id.childcard);
            this.childIcon = (ImageView) rootView.findViewById(R.id.childnodeImage);
            nodeCounter = (TextView) rootView.findViewById(R.id.nodeCount);
            totalNodeCounter = (TextView) rootView.findViewById(R.id.nodetotalCount);
            countProgress = (ProgressBar) rootView.findViewById(R.id.countProgress);
            totalCountProgress = (ProgressBar) rootView.findViewById(R.id.totalcountProgress);

        }

        public String getInherit() {
            return nodeInherit.getText().toString();
        }
    }


}
