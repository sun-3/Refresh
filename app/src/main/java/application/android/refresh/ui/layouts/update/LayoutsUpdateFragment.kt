package application.android.refresh.ui.layouts.update

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
import application.android.refresh.data.db.entity.Layout
import kotlinx.android.synthetic.main.fragment_layouts_info.*
import kotlinx.android.synthetic.main.fragment_layouts_update.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class LayoutsUpdateFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private val viewModelFactory: LayoutsUpdateViewModelFactory by
    instance<LayoutsUpdateViewModelFactory>()

    private lateinit var viewModel: LayoutsUpdateViewModel

    private val args: LayoutsUpdateFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_layouts_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(LayoutsUpdateViewModel::class.java)

        setupUI()
    }

    private fun setupUI() {
        setupToolbar()

        val layoutId = args.layoutId

        // Check for default arg value
        if (layoutId == 0L) {
            findNavController().navigateUp()
            Toast.makeText(context, "Choose a layout to edit", Toast.LENGTH_SHORT).show()
        }

        viewModel.layoutDetails(layoutId).observe(viewLifecycleOwner, Observer { layout ->
            setFields(layout)
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
        layoutsUpdateToolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun setFields(layout: Layout) {
        layoutsUpdateName.setText(layout.name)
        layoutsUpdateFront.setText(layout.front)
        layoutsUpdateBack.setText(layout.back)
        layoutsUpdateBackExtra.setText(layout.backExtra)

        layoutsUpdateUpdate.setOnClickListener {
            if (validateFields()) {
                viewModel.updateLayout(
                    Layout(
                        layout.id,
                        layoutsUpdateName.text.toString(),
                        layoutsUpdateFront.text.toString(),
                        layoutsUpdateBack.text.toString(),
                        layoutsUpdateBackExtra.text.toString()
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

        layoutsUpdateDiscard.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun validateFields(): Boolean {
        if (layoutsUpdateName.text.isNullOrBlank()) {
            return false
        }
        if (layoutsUpdateFront.text.isNullOrBlank()) {
            return false
        }
        if (layoutsUpdateBack.text.isNullOrBlank()) {
            return false
        }
        return true
    }
}