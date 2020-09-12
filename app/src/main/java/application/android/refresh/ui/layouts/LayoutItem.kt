package application.android.refresh.ui.layouts
import application.android.refresh.R
import application.android.refresh.data.db.entity.Layout
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_layout.view.*

class LayoutItem(val layout: Layout) : Item() {

    override fun getLayout() = R.layout.item_layout

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val separator = viewHolder.itemView.context.getText(R.string.separator)
        var layoutText = "${layout.front} $separator ${layout.back}"
        if (layout.backExtra.isNotBlank()) {
            layoutText = "$layoutText $separator ${layout.backExtra}"
        }
        viewHolder.itemView.layoutName.text = layout.name
        viewHolder.itemView.layoutText.text = layoutText
    }
}