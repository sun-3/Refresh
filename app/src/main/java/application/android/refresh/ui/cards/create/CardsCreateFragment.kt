package application.android.refresh.ui.cards.create

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
import application.android.refresh.data.db.DbUtils
import application.android.refresh.data.db.entity.Card
import application.android.refresh.data.db.entity.Layout
import kotlinx.android.synthetic.main.fragment_cards_create.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class CardsCreateFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private val viewModelFactory: CardsCreateViewModelFactory by
    instance<CardsCreateViewModelFactory>()

    private lateinit var viewModel: CardsCreateViewModel

    private val args: CardsCreateFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cards_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CardsCreateViewModel::class.java)

        setupUI()
    }

    private fun setupUI() {
        setupToolbar()
        val layoutId = args.cardLayoutId

        // Check for default arg value
        if (layoutId == 0L) {
            findNavController().navigateUp()
            Toast.makeText(context, "Choose a card layout", Toast.LENGTH_SHORT).show()
        }

        viewModel.layoutDetails(layoutId).observe(viewLifecycleOwner, Observer { l ->
            l?.let { layout ->
                setupCardInput(layout)
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
        cardsCreateToolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun setupCardInput(layout: Layout) {
        val fieldCount: Int = if (layout.backExtra.isBlank()) 2 else 3
        var currentCount = 1
        var cardFront = ""
        var cardBack = ""
        var cardBackExtra = ""

        changeFields(layout, fieldCount, 1)

        cardsCreateNext.setOnClickListener {
            if (cardsCreateAnswer.text.toString().isBlank()) {
                Toast.makeText(
                    context, "Enter valid input for " + cardsCreateText.text, Toast
                        .LENGTH_SHORT
                ).show()
            } else {
                when (currentCount) {
                    fieldCount -> {
                        when (currentCount) {
                            2 -> cardBack = cardsCreateAnswer.text.toString()
                            3 -> cardBackExtra =
                                cardsCreateAnswer.text.toString()
                        }

                        val card = Card(
                            DbUtils.GenerateId(),
                            layout.id,
                            layout.name,
                            cardFront,
                            cardBack,
                            layout.backExtra,
                            cardBackExtra
                        )
                        viewModel.addCard(card)
                        viewModel.isOkayToExit.observe(viewLifecycleOwner, Observer {
                            if (it) {
                                findNavController().navigateUp()
                            }
                        })
                    }
                    1 -> {
                        cardFront = cardsCreateAnswer.text.toString()
                        currentCount++
                        changeFields(layout, fieldCount, currentCount)
                    }
                    2 -> {
                        cardBack = cardsCreateAnswer.text.toString()
                        currentCount++
                        changeFields(layout, fieldCount, currentCount)
                    }
                }
            }
        }

        cardsCreateBack.setOnClickListener {
            currentCount--
            changeFields(layout, fieldCount, currentCount)
        }

    }

    private fun changeFields(layout: Layout?, fieldCount: Int, currentCount: Int) {

        when (currentCount) {
            1 -> {
                cardsCreateText.text = layout?.front
                cardsCreateBack.visibility = View.GONE
                cardsCreateAnswer.setText("")
            }
            2 -> {
                cardsCreateText.text = layout?.back
                cardsCreateBack.visibility = View.VISIBLE
                cardsCreateAnswer.setText("")
            }
            fieldCount -> {
                cardsCreateText.text = layout?.backExtra
                cardsCreateBack.visibility = View.VISIBLE
                cardsCreateAnswer.setText("")
            }
        }


    }

}
