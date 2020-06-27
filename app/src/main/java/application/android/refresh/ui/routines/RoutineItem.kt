package application.android.refresh.ui.routines

import application.android.refresh.R
import application.android.refresh.data.db.entity.Routine
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_routine.view.*

class RoutineItem(val routine: Routine) : Item() {

    override fun getLayout() = R.layout.item_routine

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.routineName.text = routine.name
    }
}