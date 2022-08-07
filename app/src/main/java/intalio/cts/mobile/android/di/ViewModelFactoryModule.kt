package intalio.cts.mobile.android.di

import androidx.lifecycle.ViewModel
import dagger.Module
import javax.inject.Named
import androidx.lifecycle.ViewModelProvider.Factory
import dagger.Provides
import intalio.cts.mobile.android.data.repo.AdminRepo
import intalio.cts.mobile.android.data.repo.UserRepo
import intalio.cts.mobile.android.ui.activity.auth.login.LoginViewModel
import intalio.cts.mobile.android.ui.activity.splash.SplashViewModel
import intalio.cts.mobile.android.ui.fragment.advancedsearch.AdvanceSearchViewModel
import intalio.cts.mobile.android.ui.fragment.attachments.AttachmentsViewModel
import intalio.cts.mobile.android.ui.fragment.correspondence.CorrespondenceViewModel
import intalio.cts.mobile.android.ui.fragment.correspondencedetails.CorrespondenceDetailsViewModel
import intalio.cts.mobile.android.ui.fragment.note.NotesViewModel
import intalio.cts.mobile.android.ui.fragment.delegations.DelegationsViewModel
import intalio.cts.mobile.android.ui.fragment.linkedcorrespondence.LinkedCorrespondenceViewModel
import intalio.cts.mobile.android.ui.fragment.main.MainViewModel
import intalio.cts.mobile.android.ui.fragment.metadata.MetaDataViewModel
import intalio.cts.mobile.android.ui.fragment.nonarchattachments.NonArchAttachmentsViewModel
import intalio.cts.mobile.android.ui.fragment.profile.ProfleViewModel
import intalio.cts.mobile.android.ui.fragment.reply.ReplyViewModel
import intalio.cts.mobile.android.ui.fragment.transfer.TransfersViewModel
import intalio.cts.mobile.android.ui.fragment.uploadattachment.UploadAttachmentViewModel
import intalio.cts.mobile.android.ui.fragment.visualtracking.VisualTrackingViewModel


@Module
class ViewModelFactoryModule  {


    @Provides
    @Named("login")
    fun provideLoginViewModel(userRepo: UserRepo): Factory {
        return object : Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return LoginViewModel(userRepo) as T
            }
        }
    }

    @Provides
    @Named("splash")
    fun provideSplashViewModel(userRepo: UserRepo): Factory {
        return object : Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SplashViewModel(userRepo) as T
            }
        }
    }


    @Provides
    @Named("main")
    fun provideMainViewModel(userRepo: UserRepo, adminRepo: AdminRepo): Factory {
        return object : Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainViewModel(userRepo, adminRepo) as T
            }
        }
    }

    @Provides
    @Named("correspondence")
    fun provideCorrespondenceViewModel(userRepo: UserRepo, adminRepo: AdminRepo): Factory {
        return object : Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return CorrespondenceViewModel(userRepo, adminRepo) as T
            }
        }
    }

    @Provides
    @Named("correspondenceDetails")
    fun provideCorrespondenceDetailsViewModel(userRepo: UserRepo, adminRepo: AdminRepo): Factory {
        return object : Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return CorrespondenceDetailsViewModel(userRepo, adminRepo) as T
            }
        }
    }

    @Provides
    @Named("delegations")
    fun provideDelegationsViewModel(userRepo: UserRepo, adminRepo: AdminRepo): Factory {
        return object : Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return DelegationsViewModel(userRepo, adminRepo) as T
            }
        }
    }

    @Provides
    @Named("profile")
    fun provideProfileViewModel(userRepo: UserRepo, adminRepo: AdminRepo): Factory {
        return object : Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ProfleViewModel(userRepo, adminRepo) as T
            }
        }
    }

    @Provides
    @Named("notes")
    fun provideNotesViewModel(userRepo: UserRepo, adminRepo: AdminRepo): Factory {
        return object : Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return NotesViewModel(userRepo, adminRepo) as T
            }
        }
    }

    @Provides
    @Named("nonarchattachments")
    fun provideNonArchAttachmentsViewModel(userRepo: UserRepo, adminRepo: AdminRepo): Factory {
        return object : Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return NonArchAttachmentsViewModel(userRepo, adminRepo) as T
            }
        }
    }


    @Provides
    @Named("linkedcorrespondence")
    fun provideLinkedCorrespondenceViewModel(userRepo: UserRepo, adminRepo: AdminRepo): Factory {
        return object : Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return LinkedCorrespondenceViewModel(userRepo, adminRepo) as T
            }
        }
    }

    @Provides
    @Named("visualtracking")
    fun provideVisualTrackingViewModel(userRepo: UserRepo, adminRepo: AdminRepo): Factory {
        return object : Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return VisualTrackingViewModel(userRepo, adminRepo) as T
            }
        }
    }

    @Provides
    @Named("addtransfer")
    fun provideTransfersViewModel(userRepo: UserRepo, adminRepo: AdminRepo): Factory {
        return object : Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return TransfersViewModel(userRepo, adminRepo) as T
            }
        }
    }

    @Provides
    @Named("replytransfer")
    fun provideReplyViewModel(userRepo: UserRepo, adminRepo: AdminRepo): Factory {
        return object : Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ReplyViewModel(userRepo, adminRepo) as T
            }
        }
    }
    @Provides
    @Named("metadata")
    fun provideMetaDataViewModel(userRepo: UserRepo, adminRepo: AdminRepo): Factory {
        return object : Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MetaDataViewModel(userRepo, adminRepo) as T
            }
        }
    }

    @Provides
    @Named("advancedsearch")
    fun provideAdvanceSearchViewModel(userRepo: UserRepo, adminRepo: AdminRepo): Factory {
        return object : Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return AdvanceSearchViewModel(userRepo, adminRepo) as T
            }
        }
    }

    @Provides
    @Named("attachments")
    fun provideAttachmentsViewModel(userRepo: UserRepo, adminRepo: AdminRepo): Factory {
        return object : Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return AttachmentsViewModel(userRepo, adminRepo) as T
            }
        }
    }

    @Provides
    @Named("uploadattachment")
    fun provideUploadAttachmentViewModel(userRepo: UserRepo, adminRepo: AdminRepo): Factory {
        return object : Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return UploadAttachmentViewModel(userRepo, adminRepo) as T
            }
        }
    }
}