package application.android.refresh.ui.cards.update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import kotlinx.android.synthetic.main.fragment_cards_update.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class CardsUpdateFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private val viewModelFactory: CardsUpdateViewModelFactory by
    instance<CardsUpdateViewModelFactory>()

    private lateinit var viewModel: CardsUpdateViewModel

    private val args: CardsUpdateFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cards_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CardsUpdateViewModel::class.java)

        setupUI()
    }

    private fun setupUI() {
        setupToolbar()

        val cardId = args.cardId

        // Check for default arg value
        if (cardId == 0L) {
            findNavController().navigateUp()
            Toast.makeText(context, "Choose a card to update", Toast.LENGTH_SHORT).show()
        }

        viewModel.cardDetails(cardId).observe(viewLifecycleOwner, Observer { card ->
            viewModel.layoutDetails(card.layoutId).observe(viewLifecycleOwner, Observer { layout ->
                setFields(card, layout)
            })
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
        cardsUpdateToolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun setFields(card: Card, layout: Layout) {
        cardsUpdateFrontTitle.text = layout.front
        cardsUpdateFront.setText(card.front)
        cardsUpdateBackTitle.text = layout.back
        cardsUpdateBack.setText(card.back)

        if (!card.backExtraTitle.isBlank()) {
            cardsUpdateBackExtraTitle.visibility = View.VISIBLE
            cardsUpdateBackExtraTitle.text = card.backExtraTitle
            cardsUpdateBackExtra.visibility = View.VISIBLE
            cardsUpdateBackExtra.setText(card.backExtra)
        } else {
            cardsUpdateBackExtraTitle.visibility = View.GONE
            cardsUpdateBackExtra.visibility = View.GONE
        }
        cardsUpdateUpdate.setOnClickListener {
            if (validateFields(card)) {
                viewModel.updateCard(
                    Card(
                        card.id,
                        card.layoutId,
                        cardsUpdateFront.text.toString(),
                        cardsUpdateBack.text.toString(),
                        cardsUpdateBackExtraTitle.text.toString(),
                        cardsUpdateBackExtra.text.toString()
                    )
                )
                viewModel.isOkayToExit.observe(viewLifecycleOwner, Observer {
                    if (it) {
                        findNavController().navigateUp()
                    }
                })
            } else {
                Toast.makeText(context, "Enter valid input", Toast.LENGTH_SHORT).show()
            }
        }

        cardsUpdateDiscard.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun validateFields(card: Card): Boolean {
        if (cardsUpdateFront.text.isNullOrBlank()) {
            return false
        }
        if (cardsUpdateBack.text.isNullOrBlank()) {
            return false
        }
        if (!card.backExtraTitle.isBlank() && cardsUpdateBackExtra.text.isNullOrBlank()) {
            return false
        }
        return true
    }
}