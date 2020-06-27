package application.android.refresh.ui.layouts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import application.android.refresh.R
import application.android.refresh.data.db.entity.Layout
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_layouts.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class LayoutsFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private val viewModelFactory: LayoutsViewModelFactory by
    instance<LayoutsViewModelFactory>()

    private lateinit var viewModel: LayoutsViewModel
    private lateinit var groupieAdapter: GroupAdapter<GroupieViewHolder>
    private lateinit var groupieList: List<LayoutItem>
    private var shouldInitRecyclerView: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_layouts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LayoutsViewModel::class.java)

        setupUI()
    }

    private fun setupUI() {
        initVars()
        setupToolbar()

        viewModel.layoutList.observe(viewLifecycleOwner, Observer {
            viewModel.list = it
            initRecyclerView()
        })
    }

    private fun initVars() {
        groupieList = listOf()
        shouldInitRecyclerView = true
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
        layoutsToolbar.setupWithNavController(navController, appBarConfiguration)
        layoutsToolbar.inflateMenu(R.menu.search_add)
        val menu = layoutsToolbar.menu
        val searchView: SearchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(searchViewQueryListener)
        searchView.setOnCloseListener {
            addLayoutsToGroupie(viewModel.list)
            return@setOnCloseListener true
        }
        menu.findItem(R.id.action_add).setOnMenuItemClickListener {
            findNavController().navigate(R.id.layoutsCreateAction)
            return@setOnMenuItemClickListener true
        }
    }

    private fun initRecyclerView() {
        if (viewModel.list.isNullOrEmpty() || !shouldInitRecyclerView) return
        shouldInitRecyclerView = false
        groupieAdapter = GroupAdapter<GroupieViewHolder>()
        addLayoutsToGroupie(viewModel.list)
        layoutsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@LayoutsFragment.context)
            adapter = groupieAdapter
        }

        groupieAdapter.setOnItemClickListener { item, _ ->
            (item as? LayoutItem)?.let {
                val layoutsInfoAction = LayoutsFragmentDirections.layoutsInfoAction(it.layout.id)
                findNavController().navigate(layoutsInfoAction)
            }
        }
    }

    private fun addLayoutsToGroupie(layoutList: List<Layout>) {
        groupieAdapter.apply {
            if (!groupieList.isNullOrEmpty()) {
                removeAll(groupieList)
            }
            groupieList = layoutList.toLayoutItems()
            addAll(groupieList)
        }
    }


    // Extension function to convert Layout to LayoutItem
    private fun List<Layout>.toLayoutItems(): List<LayoutItem> {
        return this.map {
            LayoutItem(it)
        }
    }

    private val searchViewQueryListener: SearchView.OnQueryTextListener =
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.searchLayouts(query).observe(viewLifecycleOwner, Observer {
                    addLayoutsToGroupie(it)
                })
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                viewModel.searchLayouts(query).observe(viewLifecycleOwner, Observer {
                    addLayoutsToGroupie(it)
                })
                return true
            }
        }
}
