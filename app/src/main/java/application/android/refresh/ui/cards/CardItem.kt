package application.android.refresh.ui.cards

import android.view.View
import application.android.refresh.R
import application.android.refresh.data.db.entity.Card
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_card.view.*

class CardItem(val card: Card): Item() {

    override fun getLayout() = R.layout.item_card

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.cardFront.text = card.front
        viewHolder.itemView.cardBack.text = card.back
        if (!card.backExtraTitle.isBlank()) {
            viewHolder.itemView.cardExtraLayout.visibility = View.VISIBLE
            viewHolder.itemView.cardBackExtraTitle.text = card.backExtraTitle
            viewHolder.itemView.cardBackExtra.text = card.backExtra
            viewHolder.itemView.cardLayout.text = card.layoutName
        } else {
            viewHolder.itemView.cardExtraLayout.visibility = View.GONE
        }
    }
}