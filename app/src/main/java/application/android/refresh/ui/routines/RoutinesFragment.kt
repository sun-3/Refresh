package application.android.refresh.ui.routines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import application.android.refresh.R
import application.android.refresh.data.db.entity.Routine
import kotlinx.android.synthetic.main.fragment_routines.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class RoutinesFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private val viewModelFactory: RoutinesViewModelFactory by
    instance<RoutinesViewModelFactory>()
    private lateinit var viewModel: RoutinesViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_routines, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(RoutinesViewModel::class.java)

        setupUI()
    }

    private fun setupUI() {
        setupToolbar()
        initRecyclerView()
        viewModel.routineList.observe(viewLifecycleOwner, Observer {
            it?.let { routineList ->
                if (routineList.isNotEmpty()) {
                    viewModel.list = routineList
                    addLayoutsToGroupie(routineList)
                }
            }

        })
    }

    private fun setupToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_layouts,
                R.id.navigation_cards,
                R.id.navigation_routines
            )
        )
        routineToolbar.setupWithNavController(navController, appBarConfiguration)
        routineToolbar.inflateMenu(R.menu.search_add)
        val menu = routineToolbar.menu

        menu.findItem(R.id.action_add).setOnMenuItemClickListener {
            findNavController().navigate(R.id.navigation_routine_create)
            return@setOnMenuItemClickListener true
        }
    }

    private fun initRecyclerView() {
        addLayoutsToGroupie(viewModel.list)
        routineRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RoutinesFragment.context)
            adapter = viewModel.groupieAdapter
        }

        viewModel.groupieAdapter.setOnItemClickListener { item, _ ->
            (item as? RoutineItem)?.let {
                val routineInfoAction = RoutinesFragmentDirections.routineInfoAction(
                    it.routine
                        .id
                )
                findNavController().navigate(routineInfoAction)
            }
        }
    }

    private fun addLayoutsToGroupie(routineList: List<Routine>) {
        if (routineList.isNullOrEmpty()) {
            return
        }
        viewModel.groupieAdapter.apply {
            if (!viewModel.groupieList.isNullOrEmpty()) {
                removeAll(viewModel.groupieList)
            }
            viewModel.groupieList = routineList.toRoutineItems()
            addAll(viewModel.groupieList)
        }
    }


    // Extension function to convert Layout to LayoutItem
    private fun List<Routine>.toRoutineItems(): List<RoutineItem> {
        return this.map {
            RoutineItem(it)
        }
    }
}
