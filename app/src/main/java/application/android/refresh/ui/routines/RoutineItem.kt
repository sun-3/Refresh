package application.android.refresh.ui.routines

import application.android.refresh.R
import application.android.refresh.data.internal.RoutineCard
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_routine.view.*

class RoutineItem(val routineCard: RoutineCard) : Item() {

    override fun getLayout() = R.layout.item_routine

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val completedCards = "Cards: " + routineCard.completedCards + " / " + routineCard.totalCards
        val routineLayouts = "{ ${routineCard.layoutNames.joinToString(", ")} }"
        viewHolder.itemView.routineName.text = routineCard.name
        viewHolder.itemView.routineCompletedCards.text = completedCards
        viewHolder.itemView.routineLayouts.text = routineLayouts
    }
}