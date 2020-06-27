package application.android.refresh.ui.layouts.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import application.android.refresh.R
import application.android.refresh.data.db.DbUtils
import application.android.refresh.data.db.entity.Layout
import kotlinx.android.synthetic.main.fragment_layouts_create.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class LayoutsCreateFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private val viewModelFactory: LayoutsCreateViewModelFactory by
    instance<LayoutsCreateViewModelFactory>()

    private lateinit var viewModel: LayoutsCreateViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_layouts_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LayoutsCreateViewModel::class.java)

        setupUI()
    }

    private fun setupUI() {
        createLayout()
    }

    private fun createLayout() {
        var currentCount = 1
        var layoutName = ""
        var layoutFront = ""
        var layoutBack = ""
        var layoutBackExtra: String

        changeFields(1)

        layoutsCreateNext.setOnClickListener {
            if (layoutsCreateAnswer.text.toString().isBlank() && layoutsCreateText.text.toString() != getString(R.string.extra)
            ) {
                Toast.makeText(context, "Enter valid input for " + layoutsCreateText.text, Toast
                    .LENGTH_SHORT).show()
            } else {
                when (currentCount) {
                    1 -> {
                        layoutName = layoutsCreateAnswer.text.toString()
                        currentCount++
                        changeFields(currentCount)
                    }
                    2 -> {
                        layoutFront = layoutsCreateAnswer.text.toString()
                        currentCount++
                        changeFields(currentCount)
                    }
                    3 -> {
                        layoutBack = layoutsCreateAnswer.text.toString()
                        currentCount++
                        changeFields(currentCount)
                    }
                    4 -> {
                        layoutBackExtra = layoutsCreateAnswer.text.toString()
                        val layout = Layout(
                            DbUtils.GenerateId(),
                            layoutName,
                            layoutFront,
                            layoutBack,
                            layoutBackExtra)
                        viewModel.addLayout(layout)
                        viewModel.isOkayToExit.observe(viewLifecycleOwner, Observer {
                            if (it) {
                                findNavController().navigateUp()
                            }
                        })
                    }
                }
            }
        }

        layoutsCreateBack.setOnClickListener {
            currentCount--
            changeFields(currentCount)
        }

    }

    private fun changeFields(currentCount: Int) {

        when (currentCount) {
            1 -> {
                layoutsCreateText.text = getString(R.string.layout_name)
                layoutsCreateBack.visibility = View.GONE
                layoutsCreateAnswer.setText("")
            }
            2 -> {
                layoutsCreateText.text = getString(R.string.front)
                layoutsCreateBack.visibility = View.VISIBLE
                layoutsCreateAnswer.setText("")
            }
            3 -> {
                layoutsCreateText.text = getString(R.string.back)
                layoutsCreateBack.visibility = View.VISIBLE
                layoutsCreateAnswer.setText("")
            }
            4 -> {
                layoutsCreateText.text = getString(R.string.extra)
                layoutsCreateBack.visibility = View.VISIBLE
                layoutsCreateAnswer.setText("")
            }
        }


    }

}
