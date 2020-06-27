package application.android.refresh.ui.layouts

import application.android.refresh.R
import application.android.refresh.data.db.entity.Layout
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_layout.view.*

class LayoutItem(val layout: Layout) : Item() {

    override fun getLayout() = R.layout.item_layout

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.layoutName.text = layout.front
        viewHolder.itemView.layoutBack.text =
            layout.front + " / " + layout.back + " / " + layout.backExtra
    }
}