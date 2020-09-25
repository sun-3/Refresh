package application.android.refresh.ui.routines.create

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import application.android.refresh.R
import application.android.refresh.data.db.DbUtils
import application.android.refresh.data.db.UserUtils
import application.android.refresh.data.db.entity.Routine
import kotlinx.android.synthetic.main.fragment_routines_create.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class RoutinesCreateFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private val viewModelFactory: RoutinesCreateViewModelFactory by
    instance<RoutinesCreateViewModelFactory>()
    private lateinit var viewModel: RoutinesCreateViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_routines_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(RoutinesCreateViewModel::class.java)

        viewModel.shouldSetupUI.observe(viewLifecycleOwner, Observer {
            if (it) {
                setupUI()
            }
        })
    }

    private fun setupUI() {
        createRoutine()
    }

    private fun createRoutine() {
        var currentCount = 1
        changeFields(1)

        routineCreateLayoutText.setOnClickListener {
            createLayoutSelectionDialog()
        }
        routineCreateLayoutAdd.setOnClickListener {
            createLayoutSelectionDialog()
        }

        routineCreateNext.setOnClickListener {
            if (validate()) {
                Toast.makeText(
                    context, "Enter valid input for " + routineCreateText.text, Toast
                        .LENGTH_SHORT
                ).show()
            } else {
                when (currentCount) {
                    1 -> {
                        viewModel.routineName = routineCreateTextAnswer.text.toString()
                        currentCount++
                        changeFields(currentCount)
                    }
                    2 -> {
                        val routineRoutine = Routine(
                            DbUtils.GenerateId(),
                            UserUtils.getUserId(),
                            viewModel.routineName,
                            viewModel.selectedLayoutIds,
                            mutableListOf()
                        )

                        viewModel.addRoutine(routineRoutine)
                        viewModel.shouldExit.observe(viewLifecycleOwner,
                            Observer {
                                if (it) {
                                    findNavController().navigateUp()
                                }
                            })
                    }
                }
            }
        }

        routineCreateBack.setOnClickListener {
            currentCount--
            changeFields(currentCount)
        }

    }

    private fun validate(): Boolean {
        return (routineCreateTextAnswer.visibility == View.VISIBLE && routineCreateTextAnswer.text
            .toString()
            .isBlank()) ||
                (routineCreateLayoutText.visibility == View.VISIBLE && routineCreateLayoutText
                    .text
                    .toString() == resources.getQuantityString(
                    R.plurals
                        .layoutsSelected, 0, 0
                ))
    }

    private fun createLayoutSelectionDialog() {
        val selectedItems = viewModel.selectedIndices
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.select_layouts))
            .setMultiChoiceItems(
                viewModel.layoutNames.toTypedArray(), viewModel
                    .alreadySelectedLayouts.toBooleanArray()
            ) { _, which, isChecked ->
                if (isChecked) {
                    viewModel.alreadySelectedLayouts[which] = true
                    selectedItems.add(which)
                } else {
                    viewModel.alreadySelectedLayouts[which] = false
                    if (selectedItems.contains(which)) {
                        selectedItems.remove(Integer.valueOf(which))
                    }
                }
            }
            .setPositiveButton(
                R.string.ok
            ) { _, _ ->
                viewModel.generateSelectedLayouts()
                routineCreateLayoutText.text = resources.getQuantityString(
                    R.plurals
                        .layoutsSelected,
                    viewModel.selectedLayoutCount,
                    viewModel.selectedLayoutCount
                )
            }
            .setNegativeButton(
                R.string.cancel
            ) { _, _ ->
                for (i in 0 until viewModel.alreadySelectedLayouts.size) {
                    viewModel.alreadySelectedLayouts[i] = false
                }
            }

        builder.create()
        builder.show()
    }

    private fun changeFields(currentCount: Int) {

        when (currentCount) {
            1 -> {
                routineCreateText.text = getString(R.string.routine_name)
                routineCreateBack.visibility = View.GONE
                routineCreateLayoutText.visibility = View.GONE
                routineCreateLayoutAdd.visibility = View.GONE
                routineCreateTextAnswer.visibility = View.VISIBLE
                routineCreateTextAnswer.setText(viewModel.routineName)
            }
            2 -> {
                routineCreateText.text = getString(R.string.select_layouts)
                routineCreateBack.visibility = View.VISIBLE
                routineCreateLayoutText.visibility = View.VISIBLE
                routineCreateLayoutAdd.visibility = View.VISIBLE
                routineCreateTextAnswer.visibility = View.GONE
                routineCreateLayoutText.text = resources.getQuantityString(
                    R.plurals
                        .layoutsSelected,
                    viewModel.selectedLayoutCount,
                    viewModel.selectedLayoutCount
                )
                routineCreateLayoutText.hideKeyboard()
            }
        }


    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}