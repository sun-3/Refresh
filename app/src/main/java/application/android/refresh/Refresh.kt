package application.android.refresh

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import application.android.refresh.data.db.RefreshDatabase
import application.android.refresh.data.repository.RefreshRepository
import application.android.refresh.data.repository.RefreshRepositoryImpl
import application.android.refresh.ui.cards.CardsViewModelFactory
import application.android.refresh.ui.cards.create.CardsCreateViewModelFactory
import application.android.refresh.ui.cards.info.CardsInfoViewModelFactory
import application.android.refresh.ui.cards.update.CardsUpdateViewModelFactory
import application.android.refresh.ui.layouts.LayoutsViewModelFactory
import application.android.refresh.ui.layouts.create.LayoutsCreateViewModelFactory
import application.android.refresh.ui.layouts.info.LayoutsInfoViewModelFactory
import application.android.refresh.ui.layouts.search.LayoutsSearchViewModelFactory
import application.android.refresh.ui.layouts.update.LayoutsUpdateViewModelFactory
import application.android.refresh.ui.routines.RoutinesViewModelFactory
import application.android.refresh.ui.routines.create.RoutinesCreateViewModelFactory
import application.android.refresh.ui.routines.info.RoutinesInfoViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class Refresh : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@Refresh))

        // Database & Repository
        bind() from singleton { RefreshDatabase(instance()) }
        bind() from singleton { instance<RefreshDatabase>().layoutDao() }
        bind() from singleton { instance<RefreshDatabase>().cardDao() }
        bind() from singleton { instance<RefreshDatabase>().routineDao() }
        bind<RefreshRepository>() with singleton {
            RefreshRepositoryImpl(
                instance(),
                instance(),
                instance()
            )
        }

        // Cards
        bind() from provider { CardsViewModelFactory(instance()) }
        bind() from provider { CardsInfoViewModelFactory(instance()) }
        bind() from provider { CardsCreateViewModelFactory(instance()) }
        bind() from provider { CardsUpdateViewModelFactory(instance()) }

        // Layouts
        bind() from provider { LayoutsViewModelFactory(instance()) }
        bind() from provider { LayoutsInfoViewModelFactory(instance()) }
        bind() from provider { LayoutsCreateViewModelFactory(instance()) }
        bind() from provider { LayoutsUpdateViewModelFactory(instance()) }
        bind() from provider { LayoutsSearchViewModelFactory(instance()) }

        // Routines
        bind() from provider { RoutinesViewModelFactory(instance()) }
        bind() from provider { RoutinesCreateViewModelFactory(instance()) }
        bind() from provider { RoutinesInfoViewModelFactory(instance()) }
    }


    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_YES
        )
    }
}