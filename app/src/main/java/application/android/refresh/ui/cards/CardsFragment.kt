package application.android.refresh.ui.cards

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import application.android.refresh.R
import application.android.refresh.data.db.entity.Card
import application.android.refresh.internal.ScrollDirection
import application.android.refresh.internal.SearchRequest
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_cards.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class CardsFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private val viewModelFactory: CardsViewModelFactory by
    instance<CardsViewModelFactory>()

    private lateinit var viewModel: CardsViewModel
    private lateinit var groupieAdapter: GroupAdapter<GroupieViewHolder>
    private lateinit var groupieList: List<CardItem>
    private var shouldInitRecyclerView: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CardsViewModel::class.java)

        setupUI()
    }

    private fun setupUI() {
        initVars()
        setupToolbar()
        viewModel.cardList.observe(viewLifecycleOwner, Observer { c ->
            c?.let { card ->
                viewModel.list = card
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
        cardsToolbar.setupWithNavController(navController, appBarConfiguration)
        cardsToolbar.inflateMenu(R.menu.search_add)
        val menu = cardsToolbar.menu
        val searchView: SearchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(searchViewQueryListener)
        searchView.setOnCloseListener {
            addLayoutsToGroupie(viewModel.list)
            return@setOnCloseListener true
        }
        menu.findItem(R.id.action_add).setOnMenuItemClickListener {
            val layoutSelectionAction =
                CardsFragmentDirections.layoutSelectionAction(SearchRequest.CARDS_CREATE)
            findNavController().navigate(layoutSelectionAction)
            return@setOnMenuItemClickListener true
        }
    }

    private fun initRecyclerView() {
        if (viewModel.list.isNullOrEmpty() || !shouldInitRecyclerView) return
        shouldInitRecyclerView = false
        groupieAdapter = GroupAdapter<GroupieViewHolder>()
        addLayoutsToGroupie(viewModel.list)
        cardsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CardsFragment.context)
            adapter = groupieAdapter
        }

        // Showing a floating action button whenever user scrolls vertically
        cardsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            // Using boolean flags to avoid unnecessary function calls
            var setupTop = true
            var setupBottom = true
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && setupBottom) {
                    setupBottom = false
                    setupTop = true
                    setupScrollFab(ScrollDirection.DOWN)
                } else if (dy < 0 && setupTop) {
                    setupTop = false
                    setupBottom = true
                    setupScrollFab(ScrollDirection.UP)
                }
            }

            // Show the floating action button only when there are enough items and while the
            // user is scrolling
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!setupTop || !setupBottom) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        cardsScrollFab.hide()
                    } else {
                        cardsScrollFab.show()
                    }
                }
            }
        })

        groupieAdapter.setOnItemClickListener { item, _ ->
            (item as? CardItem)?.let {
                val cardsInfoAction = CardsFragmentDirections.cardsInfoAction(it.card.id)
                findNavController().navigate(cardsInfoAction)
            }
        }
    }

    // Change icon and scroll-to-position
    private fun setupScrollFab(direction: ScrollDirection) {
        cardsScrollFab.show()
        val fabDrawable: Drawable?
        val scrollToPosition: Int
        when (direction) {
            ScrollDirection.UP -> {
                fabDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_up)
                scrollToPosition = 0
            }

            ScrollDirection.DOWN -> {
                fabDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_down)
                scrollToPosition = groupieAdapter.itemCount - 1
            }
        }

        cardsScrollFab.setImageDrawable(fabDrawable)
        cardsScrollFab.setOnClickListener {
            cardsRecyclerView.scrollToPosition(scrollToPosition)
            cardsScrollFab.hide()
        }
    }

    private fun addLayoutsToGroupie(cardList: List<Card>) {
        groupieAdapter.apply {
            if (!groupieList.isNullOrEmpty()) {
                removeAll(groupieList)
            }
            groupieList = cardList.toCardItems()
            addAll(groupieList)
        }
    }


    // Extension function to convert Card to CardItem
    private fun List<Card>.toCardItems(): List<CardItem> {
        return this.map {
            CardItem(it)
        }
    }

    private val searchViewQueryListener: SearchView.OnQueryTextListener =
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.searchCards(query).observe(viewLifecycleOwner, Observer {
                    it?.let { cardList ->
                        addLayoutsToGroupie(cardList)
                    }
                })
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                viewModel.searchCards(query).observe(viewLifecycleOwner, Observer {
                    it?.let { cardList ->
                        addLayoutsToGroupie(cardList)
                    }
                })
                return true
            }
        }
}
