package application.android.refresh.ui.cards.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import application.android.refresh.R
import application.android.refresh.data.db.entity.Card
import application.android.refresh.data.db.entity.Layout
import kotlinx.android.synthetic.main.fragment_cards_info.*
import kotlinx.android.synthetic.main.fragment_routines_info.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class CardsInfoFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private val viewModelFactory: CardsInfoViewModelFactory by
    instance<CardsInfoViewModelFactory>()

    private lateinit var viewModel: CardsInfoViewModel

    private val args: CardsInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cards_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CardsInfoViewModel::class.java)

        setupUI()
    }

    private fun setupUI() {
        setupToolbar()

        val cardId = args.cardId
        // Check for default arg value
        if (cardId == 0L) {
            findNavController().navigateUp()
            Toast.makeText(context, "Choose a card to view", Toast.LENGTH_SHORT).show()
        }

        viewModel.cardDetails(cardId).observe(viewLifecycleOwner, Observer { c ->
            c?.let { card ->
                viewModel.layoutDetails(card.layoutId).observe(viewLifecycleOwner, Observer { l ->
                    l?.let { layout ->
                        viewModel.cardName.postValue("${layout.name} Card")
                        setFields(card, layout)
                        setToolbarMenu(card)
                    }
                })
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
        cardsInfoToolbar.setupWithNavController(navController, appBarConfiguration)

        viewModel.cardName.observe(viewLifecycleOwner, Observer {
            cardsInfoToolbar.title = it
        })
    }

    private fun setToolbarMenu(card: Card) {
        cardsInfoToolbar.inflateMenu(R.menu.info_menu)
        val menu = cardsInfoToolbar.menu
        menu.findItem(R.id.action_edit).setOnMenuItemClickListener {
            val cardsInfoAction = CardsInfoFragmentDirections.cardsUpdateAction(card.id)
            findNavController().navigate(cardsInfoAction)
            return@setOnMenuItemClickListener true
        }

        menu.findItem(R.id.action_delete).setOnMenuItemClickListener {
            confirmDeleteDialog(card)
            return@setOnMenuItemClickListener true
        }
    }

    private fun setFields(card: Card, layout: Layout) {
        cardsInfoFrontTitle.text = layout.front
        cardsInfoFront.text = card.front
        cardsInfoBackTitle.text = layout.back
        cardsInfoBack.text = card.back

        if (!card.backExtraTitle.isBlank()) {
            cardsInfoBackExtraTitle.visibility = View.VISIBLE
            cardsInfoBackExtraTitle.text = card.backExtraTitle
            cardsInfoBackExtra.visibility = View.VISIBLE
            cardsInfoBackExtra.text = card.backExtra
        } else {
            cardsInfoBackExtraTitle.visibility = View.GONE
            cardsInfoBackExtra.visibility = View.GONE
        }
    }

    private fun confirmDeleteDialog(card: Card) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Card")
        builder.setMessage("This will also affect existing routines")
            .setPositiveButton(
                "Delete"
            ) { _, _ ->
                viewModel.deleteCard(card)
                viewModel.isOkayToExit.observe(viewLifecycleOwner, Observer {
                    if (it) {
                        findNavController().navigateUp()
                    }
                })
            }
            .setNegativeButton(
                "Cancel"
            ) { _, _ ->
            }
        builder.create()
        builder.show()
    }
}