package intalio.cts.mobile.android.di

import dagger.Component
import intalio.cts.mobile.android.ui.HomeActivity
import intalio.cts.mobile.android.ui.activity.auth.login.LoginActivity
import intalio.cts.mobile.android.ui.activity.splash.ScanningActivity
import intalio.cts.mobile.android.ui.activity.splash.SplashActivity
import intalio.cts.mobile.android.ui.fragment.advancedsearch.AdvancedSearchFragment
import intalio.cts.mobile.android.ui.fragment.advancedsearch.AdvancedSearchResultFragment
import intalio.cts.mobile.android.ui.fragment.attachments.AttachmentsFragment
import intalio.cts.mobile.android.ui.fragment.correspondencedetails.CorrespondenceDetailsFragment
import intalio.cts.mobile.android.ui.fragment.correspondence.CorrespondenceFragment
import intalio.cts.mobile.android.ui.fragment.correspondencedetails.MyTransferFragment
import intalio.cts.mobile.android.ui.fragment.correspondencedetails.TransferHistoryFragment
import intalio.cts.mobile.android.ui.fragment.note.AllNotesFragment
import intalio.cts.mobile.android.ui.fragment.delegations.AddDelegationFragment
import intalio.cts.mobile.android.ui.fragment.delegations.DelegationsFragment
import intalio.cts.mobile.android.ui.fragment.linkedcorrespondence.LinkedCorrespondenceFragment
import intalio.cts.mobile.android.ui.fragment.linkedcorrespondence.LinkedCorrespondenceResultFragment
import intalio.cts.mobile.android.ui.fragment.main.MainFragment
import intalio.cts.mobile.android.ui.fragment.metadata.MetaDataFragment
import intalio.cts.mobile.android.ui.fragment.nonarchattachments.AddNonArchsFragment
import intalio.cts.mobile.android.ui.fragment.nonarchattachments.NonArchAttachmentsFragment
import intalio.cts.mobile.android.ui.fragment.note.AddNotesFragment
import intalio.cts.mobile.android.ui.fragment.profile.ChangeLanguageFragment
import intalio.cts.mobile.android.ui.fragment.reply.ReplyToStructureFragment
import intalio.cts.mobile.android.ui.fragment.reply.ReplyToUserFragment
import intalio.cts.mobile.android.ui.fragment.transfer.AddTransferFragment
import intalio.cts.mobile.android.ui.fragment.transfer.TransferListFragment
import intalio.cts.mobile.android.ui.fragment.uploadattachment.UploadAttachmentFragment
import intalio.cts.mobile.android.ui.fragment.visualtracking.VisualTrackingFragment


@Component(modules = [
    NetworkModule::class,
    StorageModule::class,
    ViewModelFactoryModule::class
])
interface AppComponent {
    fun inject(splashActivity: SplashActivity)
    fun inject(loginActivity: LoginActivity)
    fun inject(homeActivity: HomeActivity)
    fun inject(mainFragment: MainFragment)
    fun inject(correspondenceFragment: CorrespondenceFragment)
    fun inject(delegationsFragment: DelegationsFragment)
    fun inject(addDelegationFragment: AddDelegationFragment)
    fun inject(changeLanguageFragment: ChangeLanguageFragment)
    fun inject(correspondenceDetailsFragment: CorrespondenceDetailsFragment)
    fun inject(allNotesFragment: AllNotesFragment)
    fun inject(addNotesFragment: AddNotesFragment)
    fun inject(transferHistoryFragment: TransferHistoryFragment)
    fun inject(myTransferFragment: MyTransferFragment)
    fun inject(nonArchAttachmentsFragment: NonArchAttachmentsFragment)
    fun inject(addNonArchsFragment: AddNonArchsFragment)
    fun inject(linkedCorrespondenceFragment: LinkedCorrespondenceFragment)
    fun inject(visualTrackingFragment: VisualTrackingFragment)
    fun inject(addTransferFragment: AddTransferFragment)
    fun inject(replyToUserFragment: ReplyToUserFragment)
    fun inject(metaDataFragment: MetaDataFragment)
    fun inject(advancedSearchFragment: AdvancedSearchFragment)
    fun inject(advancedSearchResultFragment: AdvancedSearchResultFragment)
    fun inject(attachmentsFragment: AttachmentsFragment)
    fun inject(uploadAttachmentFragment: UploadAttachmentFragment)
    fun inject(linkedCorrespondenceResultFragment: LinkedCorrespondenceResultFragment)
    fun inject(replyToStructureFragment: ReplyToStructureFragment)
    fun inject(transferListFragment: TransferListFragment)
    fun inject(scanningActivity: ScanningActivity)


}