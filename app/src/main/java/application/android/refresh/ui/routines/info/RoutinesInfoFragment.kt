package application.android.refresh.ui.routines.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
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
import kotlinx.android.synthetic.main.fragment_routines_info.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*


class RoutinesInfoFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private val viewModelFactory: RoutinesInfoViewModelFactory by
    instance<RoutinesInfoViewModelFactory>()

    private lateinit var viewModel: RoutinesInfoViewModel

    private val args: RoutinesInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_routines_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(RoutinesInfoViewModel::class.java)

        setupUI()
    }

    private fun setupUI() {
        setupToolbar()

        val routineId = args.routineId
        // Check for default arg value
        if (routineId == 0L) {
            findNavController().navigateUp()
            Toast.makeText(
                context, "Choose a routine", Toast
                    .LENGTH_SHORT
            )
                .show()
        }

        routineInfoShow.setOnClickListener {
            answerVisibility(true)
        }

        viewModel.routineDetails(routineId).observe(viewLifecycleOwner, Observer {
            it?.let { routine ->
                viewModel.routine = routine
                if (viewModel.firstLaunch) {
                    viewModel.firstLaunch = false
                    viewModel.prepareCardList()
                }
            }
        })

        viewModel.shouldSetupCard.observe(viewLifecycleOwner, Observer {
            if (it) {
                setupCard()
            }
        })

        viewModel.isDone.observe(viewLifecycleOwner, Observer {
            routineCompleted(it)
        })
    }

    private fun answerVisibility(b: Boolean) {
        if (b) {
            routineInfoShow.visibility = View.GONE
            routineInfoAnswerGroup.visibility = View.VISIBLE
        } else {
            routineInfoShow.visibility = View.VISIBLE
            routineInfoAnswerGroup.visibility = View.GONE
        }
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
        routineInfoToolbar.setupWithNavController(navController, appBarConfiguration)
        routineInfoToolbar.inflateMenu(R.menu.routine_menu)
        val menu = routineInfoToolbar.menu
        menu.findItem(R.id.action_reset).setOnMenuItemClickListener {
            showResetDialog()
            return@setOnMenuItemClickListener true
        }

        menu.findItem(R.id.action_delete).setOnMenuItemClickListener {
            confirmDeleteDialog()
            return@setOnMenuItemClickListener true
        }

        viewModel.cardsLeft.observe(viewLifecycleOwner, Observer {
            routineInfoToolbar.subtitle = "Remaining Cards: $it"
        })

        viewModel.routineName.observe(viewLifecycleOwner, Observer {
            routineInfoToolbar.title = it
        })
    }

    private fun setupCard() {
        viewModel.getNextCard()
        routineInfoNext.setOnClickListener {
            answerVisibility(false)
            viewModel.completeCard(viewModel.cardId)
            routineInfoNext.isEnabled = false
            viewModel.getNextCard()
        }

        routineInfoDelay.setOnClickListener {
            showDelayDialog()
        }

        viewModel.isDataReady.observe(viewLifecycleOwner, Observer {
            if (it) {
                changeCardValues()
                routineInfoNext.isEnabled = true
            }
        })
    }

    private fun changeCardValues() {
        routineInfoQuestionTitle.text = viewModel.questionTitle
        routineInfoQuestion.text = viewModel.question
        routineInfoAnswerTitle.text = viewModel.answerTitle
        routineInfoAnswer.text = viewModel.answer
        routineInfoAnswerExtraTitle.text = viewModel.extraTitle
        routineInfoAnswerExtra.text = viewModel.extra
    }

    private fun routineCompleted(isCompleted: Boolean) {
        if (isCompleted) {
            routineRemaining.visibility = View.GONE
            routineCompleted.visibility = View.VISIBLE
            routineInfoToolbar.subtitle = ""
        } else {
            routineRemaining.visibility = View.VISIBLE
            routineCompleted.visibility = View.GONE
        }
    }

    private fun showResetDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setTitle("Reset")
            setMessage("Reset delay/progress of completed cards")
            setPositiveButton("Progress") { _, _ ->
                viewModel.resetProgress()
            }
            setNegativeButton("Delay") { _, _ ->
                viewModel.resetDelay()
            }
            setNeutralButton("Cancel") { _, _ ->

            }
        }

        builder.create()
        builder.show()
    }

    private fun showDelayDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_delay, null)
        val datePicker = dialogView.findViewById<DatePicker>(R.id.dialog_delay_date_picker)
        builder.apply {
            setTitle("Card Delay")
            setMessage("Card won't show up until the specified date")
            setView(dialogView)
            setPositiveButton(
                R.string.ok
            ) { _, _ ->
                val cal: Calendar = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_MONTH, datePicker.dayOfMonth)
                cal.set(Calendar.MONTH, datePicker.month)
                cal.set(Calendar.YEAR, datePicker.year)
                viewModel.setCardDelay(viewModel.cardId, cal.timeInMillis)
                viewModel.getNextCard()
            }
            setNegativeButton(
                R.string.cancel
            ) { _, _ ->

            }
        }

        builder.create()
        builder.show()
    }

    private fun confirmDeleteDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Routine")
        builder.setMessage("Are you sure?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.routine?.let { routine ->
                    viewModel.deleteRoutine(routine)
                    viewModel.isOkayToExit.observe(viewLifecycleOwner, Observer {
                        if (it) {
                            findNavController().navigateUp()
                        }
                    })
                }
            }
            .setNegativeButton("Cancel") { _, _ -> }
        builder.create()
        builder.show()
    }
}