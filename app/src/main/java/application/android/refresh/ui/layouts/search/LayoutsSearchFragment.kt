package application.android.refresh.ui.layouts.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import application.android.refresh.R
import application.android.refresh.data.db.entity.Layout
import application.android.refresh.internal.SearchRequest
import application.android.refresh.ui.layouts.LayoutItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_layouts_search.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class LayoutsSearchFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private val viewModelFactory: LayoutsSearchViewModelFactory by
    instance<LayoutsSearchViewModelFactory>()

    private val groupieAdapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var viewModel: LayoutsSearchViewModel
    private lateinit var groupieList: List<LayoutItem>
    private var shouldInitRecyclerView: Boolean = false
    private lateinit var navId: SearchRequest

    private val args: LayoutsSearchFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_layouts_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(LayoutsSearchViewModel::class.java)
        setupUI()
    }

    private fun setupUI() {
        initVars()
        setupToolbar()

        navId = args.navId
        // Check for default arg value
        if (navId == SearchRequest.NONE) {
            findNavController().navigateUp()
        }
        viewModel.layoutList.observe(viewLifecycleOwner, Observer {
            it?.let { layoutList ->
                viewModel.list = layoutList
                initRecyclerView()
            }
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
        layoutsSearchToolbar.setupWithNavController(navController, appBarConfiguration)
        layoutsSearchToolbar.inflateMenu(R.menu.search)
        val menu = layoutsSearchToolbar.menu
        val searchView: SearchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(searchViewQueryListener)
    }

    private fun initRecyclerView() {
        if (viewModel.list.isNullOrEmpty() || !shouldInitRecyclerView) return
        shouldInitRecyclerView = false
        addLayoutsToGroupie(viewModel.list)
        layoutsSearchRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@LayoutsSearchFragment.context)
            adapter = groupieAdapter
        }

        groupieAdapter.setOnItemClickListener { item, _ ->
            (item as? LayoutItem)?.let {
                if (navId == SearchRequest.CARDS_CREATE) {
                    val cardsCreateAction =
                        LayoutsSearchFragmentDirections.cardsCreateAction(it.layout.id)
                    findNavController().navigate(cardsCreateAction)
                }
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
                    it?.let { layoutList ->
                        addLayoutsToGroupie(layoutList)
                    }
                })
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                viewModel.searchLayouts(query).observe(viewLifecycleOwner, Observer {
                    it?.let { layoutList ->
                        addLayoutsToGroupie(layoutList)
                    }
                })
                return true
            }
        }
}